package com.d1ff.chatservice.dto.response;

import com.d1ff.chatservice.entity.ChatRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "ChatParticipantResponse", description = "Chat participant information response")
public record ChatParticipantResponse (
        @Schema(description = "ID of the participant user", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID userId,

        @Schema(description = "Role of the participant in the chat", example = "ADMIN")
        ChatRole role,

        @Schema(description = "Timestamp when the user joined the chat", example = "2025-01-15T10:30:00")
        LocalDateTime joinedAt
){}
