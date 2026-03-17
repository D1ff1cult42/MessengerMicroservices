package com.d1ff.fulltextsearchservice.kafka.consumer;

import com.d1ff.common.avro.MessageSentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MessageSentConsumer {
    private final MessageEventHandlerFactory messageEventHandlerFactory;

    @KafkaListener(topics = "message-sent",
            groupId = "fulltext-search-service")
    public void consume(MessageSentEvent message) {
        messageEventHandlerFactory.getHandler(message.getMessageType()).processEvent(message);
    }
}
