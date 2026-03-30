package com.d1ff.accountservice.service.impl;

import com.d1ff.accountservice.entity.Account;
import com.d1ff.accountservice.repository.AccountRepository;
import com.d1ff.accountservice.service.interfaces.AccountCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountCacheServiceImpl implements AccountCacheService {
    private final AccountRepository accountRepository;


    @Override
    public Optional<Account> findById(UUID userId) {
        return accountRepository.findById(userId);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public void evict(Account account) {

    }
}
