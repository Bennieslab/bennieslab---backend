package com.bennieslab.portfolio.service;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginRateLimiterService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCKOUT_DURATION_SECONDS = 15 * 60; // 15 minutes

    private static class Attempt {
        int count;
        Instant firstAttempt;
        Instant lockoutUntil;

        Attempt(Instant now) {
            this.count = 1;
            this.firstAttempt = now;
        }
    }

    private final Map<String, Attempt> attemptsCache = new ConcurrentHashMap<>();

    public boolean isBlocked(String ip) {
        cleanup();
        Attempt attempt = attemptsCache.get(ip);
        if (attempt == null) {
            return false;
        }
        if (attempt.lockoutUntil != null) {
            if (Instant.now().isBefore(attempt.lockoutUntil)) {
                return true;
            } else {
                attemptsCache.remove(ip);
                return false;
            }
        }
        return false;
    }

    public void recordFailedAttempt(String ip) {
        Instant now = Instant.now();
        attemptsCache.compute(ip, (key, attempt) -> {
            if (attempt == null || now.isAfter(attempt.firstAttempt.plusSeconds(LOCKOUT_DURATION_SECONDS))) {
                return new Attempt(now);
            }
            attempt.count++;
            if (attempt.count >= MAX_ATTEMPTS) {
                attempt.lockoutUntil = now.plusSeconds(LOCKOUT_DURATION_SECONDS);
            }
            return attempt;
        });
    }

    public void resetAttempts(String ip) {
        attemptsCache.remove(ip);
    }

    private void cleanup() {
        if (attemptsCache.size() > 1000) {
            Instant now = Instant.now();
            attemptsCache.entrySet().removeIf(entry -> {
                Attempt a = entry.getValue();
                return (a.lockoutUntil != null && now.isAfter(a.lockoutUntil)) ||
                        now.isAfter(a.firstAttempt.plusSeconds(LOCKOUT_DURATION_SECONDS * 2));
            });
        }
    }
}
