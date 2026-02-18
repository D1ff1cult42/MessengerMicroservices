package com.d1ff.chatservice.dto.request;

import java.util.UUID;

public record KickParticipantRequest(
        UUID participantId,
        UUID chatId
) {
}
