package com.d1ff.authservice.kafka.producer;

import com.d1ff.authservice.entity.OutboxEvent;
import com.d1ff.authservice.repository.OutboxEventRepository;
import com.d1ff.common.avro.AnalyticSentEvent;
import com.d1ff.common.avro.EmailConfirmationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
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
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final OutboxEventRepository outboxEventRepository;

    @Scheduled(fixedRate = 1000)
    @Transactional
    public void publishOutboxEvent() {
        List<OutboxEvent> outboxEvents = outboxEventRepository.findTop100BySentFalseOrderByCreatedAtAsc();
        for (OutboxEvent outboxEvent : outboxEvents) {
            try{
                SpecificRecord event = deserialize(outboxEvent);

                kafkaTemplate.send(outboxEvent.getTopic(), outboxEvent.getAggregateId(), event)
                        .get();
                outboxEvent.setSent(true);

            }catch (Exception e){
                log.error("Failed to send outbox event: id={}, error={}", outboxEvent.getId(), e.getMessage());
                break;
            }
        }
    }

    private SpecificRecord deserialize(OutboxEvent outboxEvent) throws Exception {
        ByteBuffer payload = ByteBuffer.wrap(outboxEvent.getPayload());
        return switch(outboxEvent.getTopic()){
            case "email-confirmation" -> EmailConfirmationEvent.fromByteBuffer(payload);
            case "analytic-sent" -> AnalyticSentEvent.fromByteBuffer(payload);
            default -> throw new IllegalStateException("Unknown topic: " + outboxEvent.getTopic());
        };
    }
}
