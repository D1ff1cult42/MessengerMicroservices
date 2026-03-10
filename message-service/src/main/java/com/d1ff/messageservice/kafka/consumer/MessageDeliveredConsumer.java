package com.d1ff.messageservice.kafka.consumer;

import com.d1ff.common.avro.MessageDeliveredEvent;
import com.d1ff.messageservice.entity.DeliveryStatus;
import com.d1ff.messageservice.repository.MessageStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class MessageDeliveredConsumer {
    private final MessageStatusRepository messageStatusRepository;
    @KafkaListener(
            topics = "message-delivered",
            groupId = "message-service-group"
    )
    public void handleMessageDelivered(MessageDeliveredEvent event,
                                       Acknowledgment acknowledgment,
                                       @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
                                       @Header(KafkaHeaders.OFFSET) long offset) {
        try{
            log.info("Received MessageDeliveredEvent: messageId={}, chatId={}, userId={}, partition={}, offset={}",
                    event.getMessageId(), event.getChatId(), event.getUserId(), partition, offset);

            messageStatusRepository.updateMessageStatusForUserAndMessage(
                    event.getUserId(), event.getMessageId(), DeliveryStatus.DELIVERED);

            acknowledgment.acknowledge();
        }catch (Exception e){
            log.error("Failed to process MessageDeliveredEvent: messageId={}, error={}",
                    event.getMessageId(), e.getMessage(), e);
            throw e;
        }
    }
}
