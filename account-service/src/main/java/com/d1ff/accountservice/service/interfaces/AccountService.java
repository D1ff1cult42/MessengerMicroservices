package com.d1ff.accountservice.service.interfaces;

import com.d1ff.accountservice.dto.request.GetAccountByEmailRequest;
import com.d1ff.accountservice.dto.request.UpdateAccountRequest;
import com.d1ff.accountservice.dto.response.AccountResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface AccountService {
    @Transactional
    AccountResponse updateAccount(UUID userId, String email, UpdateAccountRequest request);
    @Transactional
    void deleteAccount(UUID userId, String email); //TODO: нужно лезть в auth и удалять там данные, лучше через кафку
    @Transactional
    AccountResponse getAccount(UUID userId, String email);
    @Transactional(readOnly = true)
    AccountResponse getAccountByEmail(GetAccountByEmailRequest email);
    void createAccountFromEvent(UUID userId, String email, String username);
}
