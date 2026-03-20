package com.d1ff.fulltextsearchservice.kafka.consumer.eventStrategy.impl;

import com.d1ff.common.avro.MessageSentEvent;
import com.d1ff.common.avro.MessageType;
import com.d1ff.fulltextsearchservice.kafka.consumer.eventStrategy.interfaces.MessageEventHandler;
import com.d1ff.fulltextsearchservice.repository.MessageDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@Component
public class DeletedHandler implements MessageEventHandler {
    private final MessageDocumentRepository messageDocumentRepository;

    @Override
    public void processEvent(MessageSentEvent messageEvent) {
    String messageId = String.valueOf(messageEvent.getMessageId());
    if (!messageDocumentRepository.existsById(messageId)) {
        log.warn("Delete event received for missing document: messageId={}", messageId);
        return;
    }

    messageDocumentRepository.deleteById(messageId);
    log.info("Deleted message document: messageId={}", messageId);
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DELETED;
    }
}
