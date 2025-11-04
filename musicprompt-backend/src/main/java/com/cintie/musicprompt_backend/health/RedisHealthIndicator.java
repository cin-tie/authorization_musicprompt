package com.cintie.musicprompt_backend.health;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisHealthIndicator implements HealthIndicator {

    private final RedisConnectionFactory redisConnectionFactory;

    @Override
    public Health health() {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            String pong = connection.ping();
            if ("PONG".equals(pong)) {
                return Health.up()
                        .withDetail("redis", "Redis is reachable")
                        .withDetail("service", "Redis connection healthy")
                        .build();
            } else {
                return Health.down()
                        .withDetail("redis", "Redis ping failed")
                        .withDetail("response", pong)
                        .build();
            }
        } catch (Exception e) {
            log.error("Redis health check failed", e);
            return Health.down(e)
                    .withDetail("redis", "Redis is unreachable")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}