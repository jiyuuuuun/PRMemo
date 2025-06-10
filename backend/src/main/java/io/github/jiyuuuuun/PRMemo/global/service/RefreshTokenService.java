package io.github.jiyuuuuun.PRMemo.global.service;

import io.github.jiyuuuuun.PRMemo.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String KEY_PREFIX = "refresh:";

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    /** Redis에 토큰 저장 (키: refresh:{username}) */
    public void storeRefreshToken(String refreshToken) {
        String login = jwtUtil.getSubject(refreshToken);
        long ttl = jwtUtil.getExpiration(refreshToken).getTime() - System.currentTimeMillis();
        redisTemplate.opsForValue()
                .set(KEY_PREFIX + login, refreshToken, ttl, TimeUnit.MILLISECONDS);
    }

    /** Redis에 저장된 토큰과 일치하는지 확인 */
    public boolean validateRefreshToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) return false;
        String login = jwtUtil.getSubject(refreshToken);
        String stored = redisTemplate.opsForValue().get(KEY_PREFIX + login);
        return refreshToken.equals(stored);
    }

    /** 로그아웃 시 Redis 키 삭제 */
    public void deleteRefreshToken(String login) {
        redisTemplate.delete(KEY_PREFIX + login);
    }
}

