package com.d1ff.accountservice.service.impl;

import com.d1ff.accountservice.dto.request.CreateAccountRequest;
import com.d1ff.accountservice.dto.request.GetAccountByEmailRequest;
import com.d1ff.accountservice.dto.request.UpdateAccountRequest;
import com.d1ff.accountservice.dto.response.AccountResponse;
import com.d1ff.accountservice.entity.Account;
import com.d1ff.accountservice.exceptions.AccountAlreadyExists;
import com.d1ff.accountservice.exceptions.AccountNotFoundException;
import com.d1ff.accountservice.kafka.producer.AccountDeletedProducer;
import com.d1ff.accountservice.mapper.request.CreateAccountRequestMapper;
import com.d1ff.accountservice.mapper.request.UpdateAccountRequestMapper;
import com.d1ff.accountservice.mapper.response.AccountResponseMapper;
import com.d1ff.accountservice.repository.AccountRepository;
import com.d1ff.accountservice.service.interfaces.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.d1ff.bucket.BucketResolver;
import com.d1ff.dto.response.FileUploadResponse;
import com.d1ff.grpc.client.file.FileGrpcClient;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final CreateAccountRequestMapper createAccountRequestMapper;
    private final AccountResponseMapper accountResponseMapper;
    private final UpdateAccountRequestMapper updateAccountRequestMapper;
    private final AccountDeletedProducer accountDeletedProducer;
    private final BucketResolver bucketResolver;
    private final FileGrpcClient fileService;

    @Override
    public AccountResponse createAccount(UUID userId, String email, CreateAccountRequest request) {
        try {
            if(accountRepository.findByEmail(email).isPresent()){
                throw new AccountAlreadyExists("User with email:" + email + " already exists");
            }
            Account account = createAccountRequestMapper.fromRequest(request, userId, email);

            if (request.file() != null) {
                log.info("Uploading avatar for account of user {}", userId);
                FileUploadResponse fileUploadResponse = fileService.uploadFile(
                        bucketResolver.resolveBucket(request.file().getOriginalFilename()),
                        request.file());
                account.setAvatarBucketName(fileUploadResponse.bucketName());
                account.setAvatarObjectName(fileUploadResponse.objectName());
            }

            accountRepository.save(account);
            return accountResponseMapper.toResponse(account, fileService);
        } catch (DataIntegrityViolationException ex) {
            throw new AccountAlreadyExists("User with email:" + email + " already exists");
        }
    }

    @Override
    public AccountResponse updateAccount(UUID userId, String email, UpdateAccountRequest request) {
        Account account = getOrCreateAccount(userId, email);
        updateAccountRequestMapper.updateFromRequest(account, request);

        if (request.file() != null) {
            log.info("Uploading new avatar for account of user {}", userId);
            FileUploadResponse fileUploadResponse = fileService.uploadFile(
                    bucketResolver.resolveBucket(request.file().getOriginalFilename()),
                    request.file());
            account.setAvatarBucketName(fileUploadResponse.bucketName());
            account.setAvatarObjectName(fileUploadResponse.objectName());
        }

        return accountResponseMapper.toResponse(account, fileService);
    }

    @Override
    public void deleteAccount(UUID userId, String email) {
        log.info("Deleting account for user {}", userId);
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user: " + userId));
        accountRepository.delete(account);

        //kafka account->auth producer
        log.info("Sent to kafka producer(outbox-table) for account of user {}", userId);
        accountDeletedProducer.sendAccountDeletedMessage(account.getUserId());
    }

    @Override
    public AccountResponse getAccount(UUID userId, String email) {
        Account account = getOrCreateAccount(userId, email);
        log.info("Getting account for user {}", userId);
        return accountResponseMapper.toResponse(account, fileService);
    }

    @Override
    public AccountResponse getAccountByEmail(GetAccountByEmailRequest request) {
        Account account = accountRepository.findByEmail(request.email())
                .orElseThrow(() -> new AccountNotFoundException("Account not found for email: " + request.email()));
        log.info("Getting account for user {}", account.getUserId());
        return accountResponseMapper.toResponse(account, fileService);
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