package com.d1ff.authservice.service.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.d1ff.authservice.entity.RefreshToken;
import com.d1ff.authservice.entity.User;
import com.d1ff.authservice.exception.TokenException;
import com.d1ff.authservice.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${auth.refresh-token-expiration}")
    private Duration refreshTokenExpiration;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        log.info("Creating refresh token for user {}", user.getId());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(new Timestamp(System.currentTimeMillis() + refreshTokenExpiration.toMillis()));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getRevoked()) {
            log.error("Token has been expired");
            throw new TokenException("Refresh token was revoked");
        }

        if (token.getExpiryDate().before(Timestamp.from(Instant.now()))) {
            refreshTokenRepository.delete(token);
            log.info("Token has been expired");
            throw new TokenException("Refresh token was expired. Please make a new login request");
        }

        return token;
    }

    @Transactional(readOnly = true)
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenException("Refresh token not found"));
    }

    @Transactional
    public void revokeToken(RefreshToken token) {
        log.info("Revoking refresh token {}", token.getToken());
        token.setRevokedAt(new Timestamp(System.currentTimeMillis()));
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }

    @Transactional
    @Scheduled(fixedRate = 86400000) // 24 hours
    public void deleteExpiredTokens() {
        log.info("Deleting expired refresh tokens");
        refreshTokenRepository.deleteExpiredAndRevoked(new Timestamp(System.currentTimeMillis()));
    }
}
