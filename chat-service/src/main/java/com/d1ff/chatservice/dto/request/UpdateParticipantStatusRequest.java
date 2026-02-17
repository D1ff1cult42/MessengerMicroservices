package com.d1ff.chatservice.dto.request;

import com.d1ff.chatservice.entity.ChatRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateParticipantStatusRequest(
        @NotNull
        UUID chatId,
        @NotNull
        UUID userId,
        @NotNull
        ChatRole newStatus
) {
}
