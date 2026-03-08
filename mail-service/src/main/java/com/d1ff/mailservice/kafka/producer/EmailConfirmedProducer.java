package com.d1ff.mailservice.kafka.producer;

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
public class EmailConfirmedProducer{
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendEmailConfirmed(UUID userId, String email) {
        EmailConfirmationEvent event = EmailConfirmationEvent.newBuilder()
                .setEventId(UUID.randomUUID())
                .setUserId(userId)
                .setEmail(email)
                .setTimestamp(Instant.now())
                .build();

        kafkaTemplate.send("email-confirmed", userId.toString(), event)
                .whenComplete((result,ex) -> {
                    if(ex != null){
                        log.error("Failed to send email confirmed event: userId={}, error={}"
                                ,userId, ex.getMessage(), ex);
                    }else{
                        RecordMetadata meta = result.getRecordMetadata();
                        log.info("Email confirmed event sent: userId={}, topic={}, partition={}, offset={}",
                                userId, meta.topic(), meta.partition(), meta.offset());
                    }
                });
    }
}
