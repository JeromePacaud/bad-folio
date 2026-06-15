package com.devfolio.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Suivi en mémoire des tentatives de connexion échouées, par adresse IP,
 * pour limiter les attaques par brute force (A04-01).
 */
@Component
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(15);

    private record Attempt(AtomicInteger count, Instant lockedUntil) {
    }

    private final ConcurrentHashMap<String, Attempt> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String key) {
        Attempt attempt = attempts.get(key);
        if (attempt == null) {
            return false;
        }
        if (attempt.lockedUntil() != null && Instant.now().isAfter(attempt.lockedUntil())) {
            attempts.remove(key);
            return false;
        }
        return attempt.lockedUntil() != null;
    }

    public void loginFailed(String key) {
        attempts.compute(key, (k, existing) -> {
            int count = (existing == null ? 0 : existing.count().get()) + 1;
            AtomicInteger counter = new AtomicInteger(count);
            Instant lockedUntil = count >= MAX_ATTEMPTS ? Instant.now().plus(LOCKOUT_DURATION) : null;
            return new Attempt(counter, lockedUntil);
        });
    }

    public void loginSucceeded(String key) {
        attempts.remove(key);
    }
}
