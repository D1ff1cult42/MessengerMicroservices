package org.d1ff.authservice.service.jwt;

import lombok.RequiredArgsConstructor;
import org.d1ff.authservice.entity.RefreshToken;
import org.d1ff.authservice.entity.User;
import org.d1ff.authservice.exception.TokenException;
import org.d1ff.authservice.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${auth.refresh-token-expiration}")
    private Duration refreshTokenExpiration;

    @Transactional
    public RefreshToken createRefreshToken(User user) {
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
            throw new TokenException("Refresh token was revoked");
        }

        if (token.getExpiryDate().before(Timestamp.from(Instant.now()))) {
            refreshTokenRepository.delete(token);
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
        token.setRevokedAt(new Timestamp(System.currentTimeMillis()));
        token.setRevoked(true);
        refreshTokenRepository.save(token);
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        refreshTokenRepository.revokeAllByUser(user);
    }

    @Transactional
    @Scheduled(fixedRate = 864000) // Every 24 hours
    public void deleteExpiredTokens() {
        refreshTokenRepository.deleteExpiredAndRevoked(new Timestamp(System.currentTimeMillis()));
    }
}


