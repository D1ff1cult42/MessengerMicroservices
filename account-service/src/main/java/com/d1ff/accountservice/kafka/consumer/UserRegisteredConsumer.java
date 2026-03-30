package com.d1ff.accountservice.kafka.consumer;

import com.d1ff.accountservice.exceptions.AccountAlreadyExists;
import com.d1ff.accountservice.service.interfaces.AccountService;
import com.d1ff.common.avro.UserRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserRegisteredConsumer {
    private final AccountService accountService;

    @KafkaListener(topics = "user-registered", groupId = "account-service")
    public void consume(UserRegisteredEvent event,
                        Acknowledgment ack,
                        @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                        @Header(KafkaHeaders.OFFSET) long offset){
        try{
            log.info("Received user-registered event for userId={}", event.getUserId());
            accountService.createAccountFromEvent(event.getUserId(), event.getEmail(),
                    event.getUsername() != null ? event.getUsername().toString() : null);
            ack.acknowledge();
        } catch (AccountAlreadyExists ex) {
            log.warn("Account already exists for userId={}, skipping", event.getUserId());
            ack.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to process user-registered event for userId={}", event.getUserId(), ex);
            throw ex;
        }
    }

}
