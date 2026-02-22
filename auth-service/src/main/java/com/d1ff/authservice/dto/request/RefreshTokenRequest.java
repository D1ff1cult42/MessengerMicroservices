package com.d1ff.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;


@Schema(name = "RefreshTokenRequest", description = "Request to refresh authentication tokens")
public record RefreshTokenRequest(
    @NotBlank(message = "Refresh token is required")
    @Schema(description = "Refresh token for obtaining new access tokens", example = "dGhpcy1pcz1hLXJlZnJlc2gtdG9rZW4...")
    String refreshToken
)
{}

