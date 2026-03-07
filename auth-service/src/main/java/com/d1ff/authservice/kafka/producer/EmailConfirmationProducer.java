package com.d1ff.authservice.kafka.producer;

import com.d1ff.common.avro.EmailConfirmationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailConfirmationProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEmailConfirmation(UUID userId, String email){
        EmailConfirmationEvent event = EmailConfirmationEvent.newBuilder()
                .setEventId(UUID.randomUUID())
                .setUserId(userId)
                .setEmail(email)
                .setTimestamp(Instant.now())
                .build();

        kafkaTemplate.send("email-confirmation", userId.toString(), event)
                .whenComplete((result, ex) -> {
                    if(ex != null){
                        log.error("Failed to send email confirmation event: userId={}, error={}"
                                ,userId, ex.getMessage(), ex);
                    }else{
                        RecordMetadata meta = result.getRecordMetadata();
                        log.info("Email confirmation event sent: userId={}, topic={}, partition={}, offset={}",
                                userId, meta.topic(), meta.partition(), meta.offset());
                    }
                });
    }
}
