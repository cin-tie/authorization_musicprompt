package com.cintie.musicprompt_backend.health;

import com.cintie.musicprompt_backend.services.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtHealthIndicator implements HealthIndicator {

    private final JwtService jwtService;

    @Override
    public Health health() {
        try {
            String testToken = jwtService.generateToken("health-check-user");
            boolean isValid = jwtService.isTokenValid(testToken);
            String username = jwtService.extractUsername(testToken);

            if (isValid && "health-check-user".equals(username)) {
                return Health.up()
                        .withDetail("jwtService", "JWT service is operational")
                        .withDetail("tokenGeneration", "Working")
                        .withDetail("tokenValidation", "Working")
                        .build();
            } else {
                return Health.down()
                        .withDetail("jwtService", "JWT service validation failed")
                        .build();
            }
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("jwtService", "JWT service is unavailable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}