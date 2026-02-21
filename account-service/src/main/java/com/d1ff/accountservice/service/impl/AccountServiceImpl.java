package com.d1ff.accountservice.service.impl;

import com.d1ff.accountservice.dto.request.CreateAccountRequest;
import com.d1ff.accountservice.dto.request.UpdateAccountRequest;
import com.d1ff.accountservice.dto.response.AccountResponse;
import com.d1ff.accountservice.entity.Account;
import com.d1ff.accountservice.exceptions.AccountAlreadyExists;
import com.d1ff.accountservice.mapper.request.CreateAccountRequestMapper;
import com.d1ff.accountservice.mapper.request.UpdateAccountRequestMapper;
import com.d1ff.accountservice.mapper.response.AccountResponseMapper;
import com.d1ff.accountservice.repository.AccountRepository;
import com.d1ff.accountservice.service.interfaces.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CreateAccountRequestMapper createAccountRequestMapper;
    private final AccountResponseMapper accountResponseMapper;
    private final UpdateAccountRequestMapper updateAccountRequestMapper;

    @Override
    public AccountResponse createAccount(UUID userId, String email, CreateAccountRequest request) {
        try {
            Account account = accountRepository.save(createAccountRequestMapper
                    .fromRequest(request, userId, email));

            return accountResponseMapper.toResponse(account);
        }catch(DataIntegrityViolationException ex){
            throw new AccountAlreadyExists("User with email:" + email + " already exists");
        }
    }

    @Override
    @Transactional
    public AccountResponse updateAccount(UUID userId, String email, UpdateAccountRequest request) {
        Account account = getOrCreateAccount(userId, email);
        updateAccountRequestMapper.updateFromRequest(account, request);
        return
    }

    @Override
    public void deleteAccount(UUID userId) {

    }

    @Override
    public AccountResponse getAccount() {
        return null;
    }

    private Account getOrCreateAccount(UUID userId, String email) {
        return accountRepository.findById(userId)
                .orElseGet(() -> accountRepository.save(
                        Account.builder()
                                .userId(userId)
                                .email(email)
                                .build()
                ));
    }
}