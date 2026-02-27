package com.d1ff.realtimegateway.dto.response;

import java.time.LocalDateTime;

public record WsResponse(
        EventType type,
        Object data,
        LocalDateTime timestamp
) {}
