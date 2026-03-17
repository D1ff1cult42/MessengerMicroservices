package com.d1ff.accountservice.kafka.producer;

import com.d1ff.accountservice.entity.OutboxEvent;
import com.d1ff.accountservice.repository.OutboxEventRepository;
import com.d1ff.common.avro.AccountDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class AccountDeletedProducer {

    private final OutboxEventRepository outboxEventRepository;

    public void sendAccountDeletedMessage(UUID accountId) {
        try {
            log.info("Send Account Deleted Message to Auth Service");
            AccountDeletedEvent accountDeletedEvent = AccountDeletedEvent.newBuilder()
                    .setEventId(UUID.randomUUID())
                    .setUserId(accountId)
                    .setCreatedAt(Instant.now())
                    .build();

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(accountId.toString())
                    .topic("account-deleted")
                    .payload(accountDeletedEvent.toByteBuffer().array())
                    .build();

            outboxEventRepository.save(outboxEvent);
            log.info("Account Deleted Message sent to OutboxScheduler");
        }catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
