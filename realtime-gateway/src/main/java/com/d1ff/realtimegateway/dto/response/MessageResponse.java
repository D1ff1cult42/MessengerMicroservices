package com.d1ff.realtimegateway.dto.response;

import java.util.UUID;

public record MessageResponse(
        UUID eventId,
        UUID chatId,
        UUID senderId,
        long messageId,
        String content,
        String messageType,
        long timestamp
) {}
