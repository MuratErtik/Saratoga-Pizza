package com.example.saratogapizza.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    // Key prefixler
    private static final String REFRESH_PREFIX = "refresh:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    // refresh token kaydet
    public void storeRefreshToken(String refreshToken, Long userId, long ttlSeconds) {
        String key = REFRESH_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(key, String.valueOf(userId), ttlSeconds, TimeUnit.SECONDS);
    }

    // refresh token doğrulama
    public boolean isRefreshTokenValid(String refreshToken) {
        String key = REFRESH_PREFIX + refreshToken;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Long getUserIdForRefreshToken(String refreshToken) {
        String key = REFRESH_PREFIX + refreshToken;
        String val = redisTemplate.opsForValue().get(key);
        if (val == null) return null;
        try { return Long.parseLong(val); } catch (NumberFormatException e) { return null; }
    }

    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.delete(REFRESH_PREFIX + refreshToken);
    }

    // Access token blacklist: jti -> "1" with TTL = remaining seconds
    public void blacklistAccessTokenByJti(String jti, long ttlSeconds) {
        String key = BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(key, "1", Duration.ofSeconds(ttlSeconds));
    }

    public boolean isAccessTokenBlacklisted(String jti) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + jti));
    }
}
