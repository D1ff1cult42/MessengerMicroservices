package com.d1ff.authservice.kafka.consumer;

import com.d1ff.authservice.repository.UserRepository;
import com.d1ff.common.avro.AccountDeletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AccountDeletedConsumer {
    private final UserRepository userRepository;

    @KafkaListener(
            topics = "account-deleted",
            groupId = "auth-service-group"
    )
    public void handleAccountDeletedMessage(AccountDeletedEvent accountDeletedEvent,
                                            Acknowledgment ack,
                                            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                            @Header(KafkaHeaders.OFFSET) long offset) {
        try {
            log.info("Received account-deleted message for account {}", accountDeletedEvent.getUserId());
            userRepository.deleteById(accountDeletedEvent.getUserId());
            ack.acknowledge();
        }catch (Exception ex){
            log.error("Failed to delete account-deleted message for account {}", accountDeletedEvent.getUserId(), ex);
            throw ex;
        }
    }
}
