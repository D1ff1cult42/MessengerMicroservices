package com.d1ff.messageservice.kafka.producer;


import com.d1ff.common.avro.AnalyticSentEvent;
import com.d1ff.kafka.producer.AnalyticSentProducer;
import com.d1ff.messageservice.entity.OutboxEvent;
import com.d1ff.messageservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class AnalyticSentProducerImpl extends AnalyticSentProducer {
    private final OutboxEventRepository outboxEventRepository;

    @Override
    public void saveAnalytic(byte[] jsonPayload) {
        try {
            AnalyticSentEvent event = makeAnalyticEvent(jsonPayload);
            log.info("analytic event save into outbox event");
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(event.getEventId().toString())
                    .topic("analytic-sent")
                    .payload(toOutboxPayload(event))
                    .build();

            outboxEventRepository.save(outboxEvent);
        }catch (IOException e) {
            log.error("Failed to save in outbox event analytic");
            throw new RuntimeException(e);
        }
    }
}