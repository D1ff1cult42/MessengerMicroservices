package com.d1ff.authservice.kafka.producer;

import com.d1ff.authservice.entity.OutboxEvent;
import com.d1ff.authservice.repository.OutboxEventRepository;
import com.d1ff.common.avro.EmailConfirmationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailConfirmationProducer {
    private final OutboxEventRepository outboxEventRepository;

    public void sendEmailConfirmation(UUID userId, String email) {
        try {
            EmailConfirmationEvent event = EmailConfirmationEvent.newBuilder()
                    .setEventId(UUID.randomUUID())
                    .setUserId(userId)
                    .setEmail(email)
                    .setTimestamp(Instant.now())
                    .build();

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(userId.toString())
                    .topic("email-confirmation")
                    .payload(event.toByteBuffer().array())
                    .build();

            outboxEventRepository.save(outboxEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
