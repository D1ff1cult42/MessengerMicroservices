package com.d1ff.accountservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountResponse(
        String email,
        String username,
        String description,
        String accountIconPresignedUrl,
        Long accountIconUrlTtl,
        LocalDateTime createdAt,
        boolean isVerified
){}
