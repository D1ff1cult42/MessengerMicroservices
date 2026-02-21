package com.d1ff.accountservice.dto.response;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public record AccountResponse(
        String email,
        String username,
        String description,
        String accountIconPresignedUrl,
        LocalDateTime createdAt,
        boolean isVerified
){}
