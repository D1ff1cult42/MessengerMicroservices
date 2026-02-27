package com.d1ff.realtimegateway.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;
public record MessageDeliveredEvent(
        Long messageId,
        UUID chatId,
        UUID userId,
        String status,
        LocalDateTime timestamp
) {}
