package com.d1ff.realtimegateway.service.interfaces;

import com.d1ff.realtimegateway.dto.event.TypingEvent;

public interface RealtimeTypingService {
    void handleTyping(TypingEvent event);
}
