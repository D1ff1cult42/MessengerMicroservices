package com.d1ff.authservice.service.impl;

import com.d1ff.authservice.analytic.enums.AnalyticEventType;
import com.d1ff.authservice.analytic.factory.AnalyticPayloadFactory;
import com.d1ff.authservice.kafka.producer.AnalyticSentProducerImpl;
import com.d1ff.authservice.kafka.producer.EmailConfirmationProducer;
import com.d1ff.authservice.kafka.producer.UserRegisteredProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.d1ff.authservice.dto.response.AuthResponse;
import com.d1ff.authservice.dto.request.LoginRequest;
import com.d1ff.authservice.dto.request.RegisterRequest;
import com.d1ff.authservice.entity.User;
import com.d1ff.authservice.exception.InvalidCredentialsException;
import com.d1ff.authservice.exception.UserAlreadyExistsException;
import com.d1ff.authservice.repository.UserRepository;
import com.d1ff.authservice.service.interfaces.AuthService;
import com.d1ff.authservice.service.jwt.JwtService;
import com.d1ff.authservice.service.jwt.RefreshTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AnalyticSentProducerImpl analyticSentProducer;
    private final UserRegisteredProducer userRegisteredProducer;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailConfirmationProducer emailConfirmationProducer;

    @Value("${auth.access-token-expiration}")
    private Duration accessTokenExpiration;

    @Override
    public AuthResponse register(String ip, String userAgent ,RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Registration attempt for existing email: {}", request.email());
            throw new UserAlreadyExistsException("User with email " + request.email() + " already exists");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setActive(true);
        user.setVerified(false);
        user.setRole(User.Role.USER);

        user = userRepository.save(user);
        log.info("New user registered: {}", user.getEmail());

        //analytic
        byte[] payload = AnalyticPayloadFactory.createPayload(ip, userAgent, user.getId(), AnalyticEventType.REGISTER);
        analyticSentProducer.saveAnalytic(payload);

        //Kafka
        emailConfirmationProducer.sendEmailConfirmation(user.getId(), user.getEmail());
        userRegisteredProducer.sendUserRegistered(user.getId(), user.getEmail(), request.username());

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpiration.toSeconds())
                .build();
    }

    @Override
    public AuthResponse login(String ip, String userAgent, LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> {
                    log.warn("Login attempt with non-existent email: {}", request.email());
                    return new InvalidCredentialsException("Invalid email or password");
                });

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            log.warn("Failed login attempt for user: {}", request.email());
            throw new InvalidCredentialsException("Invalid email or password");
        }

        if (!user.isActive()) {
            log.warn("Login attempt for deactivated user: {}", request.email());
            throw new InvalidCredentialsException("User account is deactivated");
        }

        user.setLastLogin(new Timestamp(System.currentTimeMillis()));
        userRepository.save(user);
        log.info("User logged in successfully: {}", user.getEmail());

        //analytic
        byte[] payload = AnalyticPayloadFactory.createPayload(ip, userAgent, user.getId(), AnalyticEventType.LOGIN);
        analyticSentProducer.saveAnalytic(payload);

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(accessTokenExpiration.toSeconds())
                .build();
    }

    @Override
    public AuthResponse refreshToken(String ip, String userAgent, String refreshTokenStr) {
        String userId = refreshTokenService.findByToken(refreshTokenStr);
        User user = userRepository.findById(UUID.fromString(userId))
                        .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        log.info("Token refreshed for user: {}", user.getEmail());

        //analytic
        byte[] payload = AnalyticPayloadFactory.createPayload(ip, userAgent, user.getId(), AnalyticEventType.REFRESH_TOKEN);
        analyticSentProducer.saveAnalytic(payload);

        String accessToken = jwtService.generateToken(user);

        refreshTokenService.revokeToken(refreshTokenStr);
        String newRefreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(accessTokenExpiration.toSeconds())
                .build();
    }

    @Override
    public void logout(String ip, String userAgent, String refreshTokenStr) {
        String userId = refreshTokenService.findByToken(refreshTokenStr);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        //analytic
        byte[] payload = AnalyticPayloadFactory.createPayload(ip, userAgent, null, AnalyticEventType.LOGOUT);
        analyticSentProducer.saveAnalytic(payload);

        log.info("User logged out: {}", user.getEmail());
        refreshTokenService.revokeToken(refreshTokenStr);
    }

    @Override
    public void logoutAll(String ip, String userAgent, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        //analytic
        byte[] payload = AnalyticPayloadFactory.createPayload(ip, userAgent, null, AnalyticEventType.LOGOUT_ALL);
        analyticSentProducer.saveAnalytic(payload);

        log.info("All sessions terminated for user: {}", email);
        refreshTokenService.revokeAllUserTokens(user);
    }
}
