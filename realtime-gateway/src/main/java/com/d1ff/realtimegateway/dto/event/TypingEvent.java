package com.d1ff.realtimegateway.dto.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record TypingEvent(
    UUID chatId,
    UUID userId,
    boolean typing,
    LocalDateTime timestamp
    ){}
