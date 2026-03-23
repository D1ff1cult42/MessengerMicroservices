package com.d1ff.authservice.service.interfaces;

import com.d1ff.authservice.dto.request.LoginRequest;
import com.d1ff.authservice.dto.request.RegisterRequest;
import com.d1ff.authservice.dto.response.AuthResponse;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {

    @Transactional
    AuthResponse register(String ip, String userAgent, RegisterRequest request);

    @Transactional
    AuthResponse login(String ip, String userAgent, LoginRequest request);

    @Transactional
    AuthResponse refreshToken(String ip, String userAgent, String refreshTokenStr);

    @Transactional
    void logout(String ip, String userAgent, String refreshTokenStr);

    @Transactional
    void logoutAll(String ip, String userAgent, String email);
}
