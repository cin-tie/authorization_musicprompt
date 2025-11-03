package com.cintie.musicprompt_backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private static final String PREFIX = "BLACKLIST_";

    public void blacklistToken(String token, long expiryMillis){
        redisTemplate.opsForValue()
                .set(PREFIX + token, "true", Duration.ofMillis(expiryMillis));
    }

    public boolean isTokenBlacklisted(String token){
        return redisTemplate.hasKey(PREFIX + token);
    }
}
