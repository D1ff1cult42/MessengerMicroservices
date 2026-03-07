package com.d1ff.mailservice.kafka;

import com.d1ff.common.avro.EmailConfirmationEvent;
import com.d1ff.mailservice.service.interfaces.MailTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailConfirmationConsumer {
    private final MailTokenService mailTokenService;

    @KafkaListener(
            topics = "email-confirmation",
            groupId = "mail-service-group"
    )
    public void handleEmailConfirmation(
            EmailConfirmationEvent event,
            Acknowledgment ack,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        log.info("Received email confirmation event: eventId={}, userId={}, email={}, partition={}, offset={}",
                event.getEventId(), event.getUserId(), event.getEmail(), partition, offset);

        try {
            mailTokenService.sendConfirmationEmail(
                    event.getUserId(), event.getEmail());
            ack.acknowledge();
            log.info("Email confirmation processed successfully: userId={}", event.getUserId());
        } catch (Exception ex){
            log.error("Failed to process email confirmation: userId={}, error={}",
                    event.getUserId(), ex.getMessage(), ex);
            throw ex;
        }
    }
}
