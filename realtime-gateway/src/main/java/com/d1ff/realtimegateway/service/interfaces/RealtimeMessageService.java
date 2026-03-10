package com.d1ff.realtimegateway.service.interfaces;

import com.d1ff.common.avro.MessageSentEvent;

public interface RealtimeMessageService {
    void handleMessageSent(MessageSentEvent event);
}
