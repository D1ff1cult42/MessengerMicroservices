package com.d1ff.messageservice.kafka.producer;

import com.d1ff.common.avro.EmailConfirmationEvent;
import com.d1ff.messageservice.entity.OutboxEvent;
import com.d1ff.messageservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void publishOutboxEvent() {
        List<OutboxEvent> outboxEvents = outboxEventRepository.findTop100BySentFalseOrderByCreatedAtAsc();
        for (OutboxEvent outboxEvent : outboxEvents) {
            try{
                EmailConfirmationEvent emailConfirmationEvent = EmailConfirmationEvent
                        .fromByteBuffer(ByteBuffer.wrap(outboxEvent.getPayload()));

                kafkaTemplate.send(outboxEvent.getTopic(), outboxEvent.getAggregateId(), emailConfirmationEvent)
                        .get();
                outboxEvent.setSent(true);

            }catch (Exception e){
                log.error("Failed to send outbox event: id={}, error={}", outboxEvent.getId(), e.getMessage());
                break;
            }
        }
    }
}

