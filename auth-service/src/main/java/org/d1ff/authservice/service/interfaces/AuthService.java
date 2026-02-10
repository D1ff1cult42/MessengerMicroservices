package org.d1ff.authservice.service.interfaces;

import org.d1ff.authservice.dto.request.LoginRequest;
import org.d1ff.authservice.dto.request.RegisterRequest;
import org.d1ff.authservice.dto.response.AuthResponse;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {

    @Transactional
    AuthResponse register(RegisterRequest request);

    @Transactional
    AuthResponse login(LoginRequest request);

    @Transactional
    AuthResponse refreshToken(String refreshTokenStr);

    @Transactional
    void logout(String refreshTokenStr);

    @Transactional
    void logoutAll(String email);
}
