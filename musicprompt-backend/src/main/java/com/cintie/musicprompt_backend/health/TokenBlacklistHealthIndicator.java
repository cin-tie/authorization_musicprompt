package com.cintie.musicprompt_backend.health;

import com.cintie.musicprompt_backend.services.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TokenBlacklistHealthIndicator implements HealthIndicator {

    private final TokenBlacklistService tokenBlacklistService;

    @Override
    public Health health() {
        try {
            String testToken = "health-check-token";
            tokenBlacklistService.blacklistToken(testToken, 5000);
            boolean isBlacklisted = tokenBlacklistService.isTokenBlacklisted(testToken);

            if (isBlacklisted) {
                return Health.up()
                        .withDetail("tokenBlacklistService", "Redis token blacklist is operational")
                        .withDetail("blacklistOperation", "Working")
                        .withDetail("checkOperation", "Working")
                        .build();
            } else {
                return Health.down()
                        .withDetail("tokenBlacklistService", "Token blacklist operations failed")
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("tokenBlacklistService", "Token blacklist service is unavailable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}