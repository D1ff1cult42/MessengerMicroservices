package com.d1ff.realtimegateway.service.interfaces;

import com.d1ff.realtimegateway.dto.event.MessageDeliveredEvent;
import com.d1ff.realtimegateway.dto.event.MessageSentEvent;

public interface RealtimeMessageService {
    void handleMessageSent(MessageSentEvent event);
    void handleMessageDelivered(MessageDeliveredEvent event);
}
