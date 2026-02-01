package org.d1ff.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Schema(name = "AuthResponse", description = "Response containing authentication tokens")
public record AuthResponse(
    @Schema(description = "Access token for authentication", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String accessToken,
    @Schema(description = "Refresh token for obtaining new access tokens", example = "dGhpcy1pcz1hLXJlZnJlc2gtdG9rZW4...")
    String refreshToken,
    @Schema(description = "Expiration time of the access token in seconds", example = "3600")
    Long expiresIn
)
{}

