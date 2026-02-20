package com.d1ff.chatservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(name = "KickParticipantRequest", description = "Request to kick a participant from a chat")
public record KickParticipantRequest(
        @Schema(description = "ID of the participant to kick", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull UUID participantId,

        @Schema(description = "ID of the chat to kick the participant from", example = "123e4567-e89b-12d3-a456-426614174001")
        @NotNull UUID chatId
) {
}
