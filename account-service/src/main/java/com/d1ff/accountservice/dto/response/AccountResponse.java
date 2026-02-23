package com.d1ff.accountservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "AccountResponse", description = "Account profile information")
public record AccountResponse(
        @Schema(description = "Email address of the account owner", example = "user@example.com")
        String email,
        @Schema(description = "Display username", example = "john_doe")
        String username,
        @Schema(description = "Profile description", example = "Just a developer.")
        String description,
        @Schema(description = "Pre-signed URL to the account avatar image (may be null if no avatar is set)", example = "https://storage.example.com/bucket/avatar.jpg?X-Amz-Signature=...")
        String accountIconPresignedUrl,
        @Schema(description = "Time-to-live of the pre-signed URL in seconds", example = "3600")
        Long accountIconUrlTtl,
        @Schema(description = "Date and time when the account was created", example = "2024-06-01T12:00:00")
        LocalDateTime createdAt,
        @Schema(description = "Whether the user's email has been verified", example = "true")
        boolean isVerified
){}
