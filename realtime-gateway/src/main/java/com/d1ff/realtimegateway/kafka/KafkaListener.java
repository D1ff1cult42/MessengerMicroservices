package com.d1ff.realtimegateway.kafka;

import com.d1ff.common.avro.MessageDeliveredEvent;
import com.d1ff.realtimegateway.dto.event.TypingEvent;
import com.d1ff.realtimegateway.service.interfaces.RealtimeMessageService;
import com.d1ff.realtimegateway.service.interfaces.RealtimeTypingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaListener {
    private final RealtimeTypingService realtimeTypingService;
    private final RealtimeMessageService realtimeMessageService;

    public void onTyping(TypingEvent event){
        realtimeTypingService.handleTyping(event);
    }
}
