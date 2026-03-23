package com.d1ff.authservice.analytic.payload;


import com.d1ff.authservice.analytic.enums.AnalyticEventType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuthAnalyticPayload(
    UUID userId,
    AnalyticEventType eventType,
    Instant occurredAt,
    String ipAddress,
    String userAgent) {}
