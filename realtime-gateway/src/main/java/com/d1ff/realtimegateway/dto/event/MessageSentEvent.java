package com.d1ff.realtimegateway.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageSentEvent(
    Long messageId,
    UUID chatId,
    UUID senderId,
    String content,
    String messageType,
    LocalDateTime createdAt
){}
