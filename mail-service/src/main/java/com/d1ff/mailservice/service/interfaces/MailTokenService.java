package com.d1ff.mailservice.service.interfaces;

import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface MailTokenService {
    @Transactional
    void sendConfirmationEmail(UUID userId, String email);

    @Transactional(readOnly = true)
    UUID confirmToken(UUID token);

    @Transactional
    void checkUnverifiedAccounts();

    @Transactional
    void deleteUsedAndExpiredTokens();
}
