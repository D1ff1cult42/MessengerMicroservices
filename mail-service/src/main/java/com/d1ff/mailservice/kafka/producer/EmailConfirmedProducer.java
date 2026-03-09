package com.d1ff.mailservice.kafka.producer;

import com.d1ff.common.avro.EmailConfirmationEvent;
import com.d1ff.mailservice.entity.OutboxEvent;
import com.d1ff.mailservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmailConfirmedProducer {
    private final OutboxEventRepository outboxEventRepository;

    public void sendEmailConfirmed(UUID userId, String email) {
        try {
            EmailConfirmationEvent event = EmailConfirmationEvent.newBuilder()
                    .setEventId(UUID.randomUUID())
                    .setUserId(userId)
                    .setEmail(email)
                    .setTimestamp(Instant.now())
                    .build();

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(userId.toString())
                    .topic("email-confirmed")
                    .payload(event.toByteBuffer().array())
                    .build();

            outboxEventRepository.save(outboxEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
