package com.bennieslab.portfolio.controller.auth;

import com.bennieslab.portfolio.auth.AuthRequest;
import com.bennieslab.portfolio.auth.AuthResponse;
import com.bennieslab.portfolio.service.AuthService;
import com.bennieslab.portfolio.service.LoginRateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiterService rateLimiterService;
    private final long jwtExpiration;

    @Autowired
    public AuthController(
            AuthService authService,
            LoginRateLimiterService rateLimiterService,
            @Value("${application.security.jwt.expiration}") long jwtExpiration
    ) {
        this.authService = authService;
        this.rateLimiterService = rateLimiterService;
        this.jwtExpiration = jwtExpiration;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthRequest request,
            HttpServletRequest httpRequest
    ) {
        String clientIp = getClientIp(httpRequest);

        if (rateLimiterService.isBlocked(clientIp)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(
                    Map.of(
                            "status", 429,
                            "error", "Too Many Requests",
                            "message", "Too many failed login attempts. Please try again after 15 minutes."
                    )
            );
        }

        try {
            AuthResponse response = authService.login(request);
            rateLimiterService.resetAttempts(clientIp);
            ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", response.getToken())
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")
                    .path("/")
                    .maxAge(Duration.ofMillis(jwtExpiration))
                    .build();
            return ResponseEntity.ok()
                    .header("Set-Cookie", cookie.toString())
                    .body(Map.of("authenticated", true));
        } catch (AuthenticationException ex) {
            rateLimiterService.recordFailedAttempt(clientIp);
            throw ex;
        }
    }

    @GetMapping("/session")
    public ResponseEntity<?> session(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("authenticated", false));
        }
        return ResponseEntity.ok(Map.of("authenticated", true));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("AUTH_TOKEN", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(Duration.ZERO)
                .build();
        return ResponseEntity.ok()
                .header("Set-Cookie", cookie.toString())
                .body(Map.of("authenticated", false));
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
