package com.d1ff.realtimegateway.kafka;

import com.d1ff.realtimegateway.dto.event.MessageDeliveredEvent;
import com.d1ff.realtimegateway.dto.event.MessageSentEvent;
import com.d1ff.realtimegateway.dto.event.TypingEvent;
import com.d1ff.realtimegateway.service.interfaces.RealtimeMessageService;
import com.d1ff.realtimegateway.service.interfaces.RealtimeTypingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaListener {
    private final RealtimeTypingService realtimeTypingService;
    private final RealtimeMessageService realtimeMessageService;

    public void onMessageSent(MessageSentEvent event){
        realtimeMessageService.handleMessageSent(event);
    }

    public void onMessageDelivered(MessageDeliveredEvent event){
        realtimeMessageService.handleMessageDelivered(event);
    }

    public void onTyping(TypingEvent event){
        realtimeTypingService.handleTyping(event);
    }
}
