package com.d1ff.accountservice.service.interfaces;

import com.d1ff.accountservice.entity.Account;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.Optional;
import java.util.UUID;

public interface AccountCacheService {
    @Cacheable(value = "account:id", key = "#userId.toString()")
    Optional<Account> findById(UUID userId);

    @Cacheable(value = "account:email", key = "#email")
    Optional<Account> findByEmail(String email);

    @Caching(evict = {
            @CacheEvict(value = "account:id", key = "#account.userId.toString()"),
            @CacheEvict(value = "account:email", key = "#account.email")
    })
    void evict(Account account);
}
