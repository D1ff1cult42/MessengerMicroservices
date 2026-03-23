package com.d1ff.analyticservice.model;

import java.time.Instant;
import java.util.UUID;

public record MessageAnalyticEvent(
        Long messageId,
        UUID userId,
        UUID chatId,
        String eventType,
        Instant occurredAt,
        String ipAddress,
        String userAgent,
        Long charNumber,
        boolean haveFile,
        String country
){
    public MessageAnalyticEvent withCountry(String country) {
        return new MessageAnalyticEvent(messageId, userId, chatId, eventType,
                occurredAt, ipAddress, userAgent, charNumber, haveFile,
                country);
    }
}
