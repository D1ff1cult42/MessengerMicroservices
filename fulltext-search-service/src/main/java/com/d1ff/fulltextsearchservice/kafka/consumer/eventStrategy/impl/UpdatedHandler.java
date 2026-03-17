package com.d1ff.fulltextsearchservice.kafka.consumer.eventStrategy.impl;

import com.d1ff.common.avro.MessageSentEvent;
import com.d1ff.common.avro.MessageType;
import com.d1ff.fulltextsearchservice.entity.document.MessageDocument;
import com.d1ff.fulltextsearchservice.exceptions.DocumentNotFound;
import com.d1ff.fulltextsearchservice.kafka.consumer.eventStrategy.interfaces.MessageEventHandler;
import com.d1ff.fulltextsearchservice.repository.MessageDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdatedHandler implements MessageEventHandler {
    private final MessageDocumentRepository messageDocumentRepository;

    @Override
    public void processEvent(MessageSentEvent messageEvent) {
        String messageId = String.valueOf(messageEvent.getMessageId());
        MessageDocument message = messageDocumentRepository.findById(messageId)
                .orElseThrow(() -> new DocumentNotFound("Message document not found for messageId: " + messageId));
        message.setText(messageEvent.getContent() == null ? "" : messageEvent.getContent());
        messageDocumentRepository.save(message);
        log.info("Updated message: messageId={}, chatId={}",
                message.getMessageId(), message.getChatId());
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.UPDATED;
    }
}
