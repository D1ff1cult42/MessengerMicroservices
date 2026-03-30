package com.d1ff.accountservice.service.impl;

import com.d1ff.accountservice.dto.request.GetAccountByEmailRequest;
import com.d1ff.accountservice.dto.request.UpdateAccountRequest;
import com.d1ff.accountservice.dto.response.AccountResponse;
import com.d1ff.accountservice.entity.Account;
import com.d1ff.accountservice.exceptions.AccountNotFoundException;
import com.d1ff.accountservice.kafka.producer.AccountDeletedProducer;
import com.d1ff.accountservice.mapper.request.UpdateAccountRequestMapper;
import com.d1ff.accountservice.mapper.response.AccountResponseMapper;
import com.d1ff.accountservice.repository.AccountRepository;
import com.d1ff.accountservice.service.interfaces.AccountCacheService;
import com.d1ff.accountservice.service.interfaces.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.d1ff.bucket.BucketResolver;
import com.d1ff.dto.response.FileUploadResponse;
import com.d1ff.grpc.client.file.FileGrpcClient;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountCacheService accountCacheService;
    private final AccountResponseMapper accountResponseMapper;
    private final UpdateAccountRequestMapper updateAccountRequestMapper;
    private final AccountDeletedProducer accountDeletedProducer;
    private final BucketResolver bucketResolver;
    private final FileGrpcClient fileService;

    @Override
    public AccountResponse updateAccount(UUID userId, String email, UpdateAccountRequest request) {
        Account account = accountCacheService.findById(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user: " + userId));
        updateAccountRequestMapper.updateFromRequest(account, request);

        if (request.file() != null) {
            log.info("Uploading new avatar for account of user {}", userId);
            FileUploadResponse fileUploadResponse = fileService.uploadFile(
                    bucketResolver.resolveBucket(request.file().getOriginalFilename()),
                    request.file());
            account.setAvatarBucketName(fileUploadResponse.bucketName());
            account.setAvatarObjectName(fileUploadResponse.objectName());
        }
        accountRepository.save(account);
        accountCacheService.evict(account);

        return accountResponseMapper.toResponse(account, fileService);
    }

    @Override
    public void deleteAccount(UUID userId, String email) {
        log.info("Deleting account for user {}", userId);
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user: " + userId));
        accountRepository.delete(account);
        accountCacheService.evict(account);

        //kafka account->auth producer
        log.info("Sent to kafka producer(outbox-table) for account of user {}", userId);
        accountDeletedProducer.sendAccountDeletedMessage(account.getUserId());
    }

    @Override
    public AccountResponse getAccount(UUID userId, String email) {
        Account account = accountCacheService.findById(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user: " + userId));
        return accountResponseMapper.toResponse(account, fileService);
    }

    @Override
    public AccountResponse getAccountByEmail(GetAccountByEmailRequest request) {
        Account account = accountCacheService.findByEmail(request.email())
                .orElseThrow(() -> new AccountNotFoundException("Account not found for email: " + request.email()));
        return accountResponseMapper.toResponse(account, fileService);
    }

    @Override
    public void createAccountFromEvent(UUID userId, String email, String username) {
        if (accountRepository.existsById(userId)) return;
        Account account = Account.builder()
                .userId(userId)
                .email(email)
                .username(username)
                .build();
        accountRepository.save(account);
        log.info("Account created from UserRegisteredEvent for userId={}", userId);
    }
}
