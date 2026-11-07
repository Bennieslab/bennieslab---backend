package com.bennieslab.portfolio.controller.auth;

import com.bennieslab.portfolio.auth.AuthRequest;
import com.bennieslab.portfolio.auth.AuthResponse;
import com.bennieslab.portfolio.service.AuthService;
import com.bennieslab.portfolio.service.LoginRateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final LoginRateLimiterService rateLimiterService;

    @Autowired
    public AuthController(AuthService authService, LoginRateLimiterService rateLimiterService) {
        this.authService = authService;
        this.rateLimiterService = rateLimiterService;
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
            return ResponseEntity.ok(response);
        } catch (AuthenticationException ex) {
            rateLimiterService.recordFailedAttempt(clientIp);
            throw ex;
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
