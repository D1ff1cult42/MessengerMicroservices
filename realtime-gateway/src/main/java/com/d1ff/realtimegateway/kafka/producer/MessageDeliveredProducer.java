package com.d1ff.realtimegateway.kafka.producer;

import com.d1ff.common.avro.MessageDeliveredEvent;
import com.d1ff.realtimegateway.entity.OutboxEvent;
import com.d1ff.realtimegateway.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MessageDeliveredProducer {
    private final OutboxEventRepository outboxEventRepository;

    public void handleMessageDelivered(MessageDeliveredEvent event) {
        try {
            outboxEventRepository.save(
                    OutboxEvent.builder()
                            .aggregateId(String.valueOf(event.getMessageId()))
                            .topic("message-delivered")
                            .payload(event.toByteBuffer().array())
                            .build()
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
