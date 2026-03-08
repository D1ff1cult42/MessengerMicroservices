package com.d1ff.authservice.kafka.consumer;

import com.d1ff.authservice.repository.UserRepository;
import com.d1ff.common.avro.EmailConfirmationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmailConfirmedConsumer {
    private final UserRepository userRepository;

    @KafkaListener(
        topics = "email-confirmed",
        groupId = "auth-service-group"
    )
    public void handleEmailConfirmed(EmailConfirmationEvent event,
                                     Acknowledgment ack,
                                     @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                     @Header(KafkaHeaders.OFFSET) long offset){
        log.info("Received email confirmed event: eventId={}, userId={}, email={}, partition={}",
                event.getEventId(), event.getUserId(), event.getEmail(), partition);
        try {
            userRepository.findById(event.getUserId()).ifPresent(user -> {
                user.setVerified(true);
                userRepository.save(user);
                log.info("User email verified: userId={}", event.getUserId());
            });
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to process email confirmed event for userId={}, error={}", event.getUserId(), ex.getMessage(), ex);
            throw ex;
        }
    }
}
