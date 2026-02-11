package org.d1ff.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.sql.Timestamp;

@Builder
public record ErrorResponse(
    @Schema(description = "Error message detailing the cause of the error", example = "Invalid credentials")
    String message,
    @Schema(description = "Specific error type or code", example = "Unauthorized")
    String error,
    @Schema(description = "HTTP status code associated with the error", example = "401")
    int status,
    @Schema(description = "Timestamp when the error occurred", example = "2024-06-01T12:00:00Z")
    Timestamp timestamp,
    @Schema(description = "Path of the request that caused the error", example = "/api/auth/login")
    String path
){}


