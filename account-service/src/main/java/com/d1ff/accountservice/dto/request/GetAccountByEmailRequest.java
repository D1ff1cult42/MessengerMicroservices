package com.d1ff.accountservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;

@Schema(name = "GetAccountByEmailRequest", description = "Request to look up an account by email address")
public record GetAccountByEmailRequest(
    @Schema(description = "Email address of the account owner", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @Email
    String email
){}