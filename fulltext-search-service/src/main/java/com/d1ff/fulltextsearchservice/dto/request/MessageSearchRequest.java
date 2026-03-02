package com.d1ff.fulltextsearchservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "MessageSearchRequest", description = "Request for full-text search of messages within a chat")
public record MessageSearchRequest(
        @Schema(description = "ID of the chat to search in", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "Chat ID cannot be null")
        UUID chatId,

        @Schema(description = "Search query text", example = "привет")
        @NotNull(message = "Query cannot be null")
        @NotBlank(message = "Query cannot be blank")
        String query
){}
