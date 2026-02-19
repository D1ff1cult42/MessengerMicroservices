package com.d1ff.chatservice.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record KickParticipantRequest(
        @NotNull UUID participantId,
        @NotNull UUID chatId
) {
}
