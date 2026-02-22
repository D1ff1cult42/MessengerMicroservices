package com.d1ff.accountservice.controller;

import com.d1ff.accountservice.dto.request.CreateAccountRequest;
import com.d1ff.accountservice.dto.request.UpdateAccountRequest;
import com.d1ff.accountservice.dto.response.AccountResponse;
import com.d1ff.accountservice.service.interfaces.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/my_account")
@RequiredArgsConstructor
public class MyAccountRestController {
    private final AccountService accountService;
    @PostMapping
    public ResponseEntity<AccountResponse> getMyAccount(@RequestHeader("X-User-Id") UUID userId,
                                                      @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(accountService.getAccount(userId, email));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@RequestHeader("X-User-Id") UUID userId,
                                              @RequestHeader("X-User-Email") String email) {
        accountService.deleteAccount(userId, email);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<AccountResponse> updateAccount(@RequestHeader("X-User-Id") UUID userId,
                                                         @RequestHeader("X-User-Email") String email,
                                                         @RequestBody @Valid UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(userId, email, request));
    }

    @PostMapping("/multi_step")
    public ResponseEntity<AccountResponse> createAccount(@RequestHeader("X-User-Id") UUID userId,
                                                         @RequestHeader("X-User-Email") String email,
                                                         @RequestBody @Valid CreateAccountRequest request) {
        return ResponseEntity.ok(accountService.createAccount(userId, email, request));
    }
}
