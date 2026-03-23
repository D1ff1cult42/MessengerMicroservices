package com.d1ff.authservice.analytic.factory;

import com.d1ff.authservice.analytic.enums.AnalyticEventType;
import com.d1ff.authservice.analytic.payload.AuthAnalyticPayload;
import com.d1ff.authservice.repository.OutboxEventRepository;
import com.d1ff.utils.JsonUtils;

import java.time.Instant;
import java.util.UUID;

public class AnalyticPayloadFactory {
    static public byte[] createPayload(String ip, String userAgent, UUID userId, AnalyticEventType eventType) {
        AuthAnalyticPayload payload = new AuthAnalyticPayload(
                userId,
                eventType,
                Instant.now(),
                ip,
                userAgent);
        return JsonUtils.toBytes(payload);
    }
}