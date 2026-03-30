package com.d1ff.authservice.service.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.d1ff.authservice.entity.User;
import com.d1ff.authservice.exception.TokenException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${auth.refresh-token-expiration}")
    private Duration refreshTokenExpiration;

    private static final String TOKEN_PREFIX = "refresh_token:";
    private static final String USER_TOKENS_PREFIX = "user_tokens:";

    public String createRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        String userId = user.getId().toString();

        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, userId,
                refreshTokenExpiration);
        redisTemplate.opsForSet().add(USER_TOKENS_PREFIX + userId , token);
        redisTemplate.expire(USER_TOKENS_PREFIX + userId, refreshTokenExpiration);

        log.info("Created refresh token for user {}", userId);

        return token;
    }

    public String findByToken(String token) {
        String userId = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if(userId == null){
            throw new TokenException("Refresh token not found or expired");
        }
        return userId;
    }

    public void revokeToken(String token) {
        String userId = redisTemplate.opsForValue().get(TOKEN_PREFIX + token);
        if(userId != null){
            redisTemplate.delete(TOKEN_PREFIX + token);
            redisTemplate.opsForSet().remove(USER_TOKENS_PREFIX + userId, token);
            log.info("Revoked refresh token for user {}", userId);
        }
    }

    public void revokeAllUserTokens(User user) {
        String userId = user.getId().toString();
        Set<String> tokens = redisTemplate.opsForSet().members(USER_TOKENS_PREFIX + userId);
        if (tokens != null) {
            tokens.forEach(t -> redisTemplate.delete(TOKEN_PREFIX + t));
        }
        redisTemplate.delete(USER_TOKENS_PREFIX + userId);
        log.info("Revoked all tokens for user {}", userId);
    }
}
