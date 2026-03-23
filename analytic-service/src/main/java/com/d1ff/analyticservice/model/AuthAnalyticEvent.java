package com.d1ff.analyticservice.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuthAnalyticEvent(UUID userId,
                                String eventType,
                                Instant occurredAt,
                                String ipAddress,
                                String userAgent,
                                String country
                                ) {
    public AuthAnalyticEvent withCountry(String country){
        return new AuthAnalyticEvent(userId, eventType, occurredAt, ipAddress, userAgent, country);
    }
}
