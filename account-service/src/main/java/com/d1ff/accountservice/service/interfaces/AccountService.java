package com.d1ff.accountservice.service.interfaces;

import com.d1ff.accountservice.dto.request.CreateAccountRequest;
import com.d1ff.accountservice.dto.request.UpdateAccountRequest;
import com.d1ff.accountservice.dto.response.AccountResponse;

import java.util.UUID;

public interface AccountService {
    AccountResponse createAccount(UUID userId, String email, CreateAccountRequest request);
    AccountResponse updateAccount(UUID userId, String email, UpdateAccountRequest request);
    void deleteAccount(UUID userId); //TODO: нужно лезть в auth и удалять там данные, лучше через кафку
    AccountResponse getAccount();
}
