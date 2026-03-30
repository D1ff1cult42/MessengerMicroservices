package com.d1ff.authservice.kafka.producer;

import com.d1ff.authservice.entity.OutboxEvent;
import com.d1ff.authservice.repository.OutboxEventRepository;
import com.d1ff.common.avro.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserRegisteredProducer {
    private final OutboxEventRepository outboxEventRepository;

    public void sendUserRegistered(UUID userId, String email, String username){
        try{
            UserRegisteredEvent event = UserRegisteredEvent.newBuilder()
                    .setEventId(UUID.randomUUID())
                    .setUserId(userId)
                    .setEmail(email)
                    .setUsername(username)
                    .setTimestamp(Instant.now())
                    .build();

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(userId.toString())
                    .topic("user-registered")
                    .payload(event.toByteBuffer().array())
                    .build();

            outboxEventRepository.save(outboxEvent);
            log.info("UserRegisteredEvent saved to outbox for userId={}", userId);
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
