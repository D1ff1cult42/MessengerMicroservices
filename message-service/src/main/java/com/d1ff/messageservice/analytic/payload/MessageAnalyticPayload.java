package com.d1ff.messageservice.analytic.payload;

import com.d1ff.messageservice.analytic.enums.AnalyticEventType;

import java.time.Instant;
import java.util.UUID;

public record MessageAnalyticPayload(
        Long messageId,
        UUID userId,
        UUID chatId,
        AnalyticEventType eventType,
        Instant occurredAt,
        String ipAddress,
        String userAgent,
        Long charNumber,
        boolean haveFile
){}
