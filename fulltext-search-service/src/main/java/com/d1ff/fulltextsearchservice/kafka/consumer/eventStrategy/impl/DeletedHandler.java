package com.d1ff.fulltextsearchservice.kafka.consumer.eventStrategy.impl;

import com.d1ff.common.avro.MessageSentEvent;
import com.d1ff.common.avro.MessageType;
import com.d1ff.fulltextsearchservice.entity.document.MessageDocument;
import com.d1ff.fulltextsearchservice.exceptions.DocumentNotFound;
import com.d1ff.fulltextsearchservice.kafka.consumer.eventStrategy.interfaces.MessageEventHandler;
import com.d1ff.fulltextsearchservice.mapper.MessageEventMapper;
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
        MessageDocument message = messageDocumentRepository.findById(String.valueOf(messageEvent.getMessageId()))
                .orElseThrow(() -> new DocumentNotFound("Message document not found for messageId: " + messageEvent.getMessageId()));
        messageDocumentRepository.delete(message);
        log.info("Deleted message: messageId={}, chatId={}",
                message.getMessageId(), message.getChatId());
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.DELETED;
    }
}
