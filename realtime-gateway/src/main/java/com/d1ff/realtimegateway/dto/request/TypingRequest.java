package com.d1ff.realtimegateway.dto.request;

import java.util.UUID;

public record TypingRequest(UUID chatId,
                            boolean typing) {}
