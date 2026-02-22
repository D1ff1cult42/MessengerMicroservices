package com.d1ff.accountservice.controller;

import com.d1ff.accountservice.dto.request.GetAccountByEmailRequest;
import com.d1ff.accountservice.dto.response.AccountResponse;
import com.d1ff.accountservice.service.interfaces.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class OtherAccountRestController {
    private final AccountService accountService;

    @GetMapping
    public ResponseEntity<AccountResponse> getAccountByEmail(GetAccountByEmailRequest getAccountByEmailRequest) {
        return ResponseEntity.ok(accountService.getAccountByEmail(getAccountByEmailRequest));
    }
}
