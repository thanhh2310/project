package com.example.project.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String,Object> redisTemplate;
    private static final String BLACKLIST_PREFIX = "BLACKLIST:";

    public void blackListToken(String token, long expirationMillis){
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token,true,
                expirationMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    public void saveRefreshTokenToRedis(String token, long expirationTimeInSeconds){
        redisTemplate.opsForValue().set("refreshToken: " + token,"active", expirationTimeInSeconds);
    }
}
