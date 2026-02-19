package com.d1ff.chatservice.dto.request;

import com.d1ff.chatservice.entity.ChatRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "UpdateParticipantStatusRequest", description = "Request to update a participant's role in a chat")
public record UpdateParticipantStatusRequest(
        @Schema(description = "ID of the chat", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull
        UUID chatId,

        @Schema(description = "ID of the user whose role is being updated", example = "123e4567-e89b-12d3-a456-426614174001")
        @NotNull
        UUID userId,

        @Schema(description = "New role for the participant", example = "ADMIN")
        @NotNull
        ChatRole newStatus
) {
}
