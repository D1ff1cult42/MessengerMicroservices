package com.d1ff.realtimegateway.kafka.consumer;

import com.d1ff.common.avro.MessageSentEvent;
import com.d1ff.realtimegateway.service.interfaces.RealtimeMessageService;
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
public class MessageSentConsumer {
    private final RealtimeMessageService realtimeMessageService;

    @KafkaListener(
            topics = "message-sent",
            groupId = "realtime-gateway-group"
    )
    public void handleMessageSent(
            MessageSentEvent messageSentEvent,
            Acknowledgment acknowledgment,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ){
        log.info("Received MessageSentEvent: messageId={}, chatId={}, senderId={}, partition={}, offset={}",
                messageSentEvent.getMessageId(), messageSentEvent.getChatId(),
                messageSentEvent.getSenderId(), partition, offset);

        try {
            realtimeMessageService.handleMessageSent(messageSentEvent);
            acknowledgment.acknowledge();
            log.info("Processed MessageSentEvent successfully: messageId={}", messageSentEvent.getMessageId());
        } catch (Exception ex){
            log.error("Failed to process MessageSentEvent: messageId={}, error={}",
                    messageSentEvent.getMessageId(), ex.getMessage(), ex);
            throw ex;
        }
    }
}
