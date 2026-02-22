package com.d1ff.accountservice.dto.request;

import jakarta.validation.constraints.Email;

public record GetAccountByEmailRequest(
    @Email
    String email
){}