package com.d1ff.fulltextsearchservice.kafka.consumer.eventStrategy.impl;

import com.d1ff.common.avro.MessageSentEvent;
import com.d1ff.common.avro.MessageType;
import com.d1ff.fulltextsearchservice.entity.document.MessageDocument;
import com.d1ff.fulltextsearchservice.kafka.consumer.eventStrategy.interfaces.MessageEventHandler;
import com.d1ff.fulltextsearchservice.mapper.MessageEventMapper;
import com.d1ff.fulltextsearchservice.repository.MessageDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatedHandler implements MessageEventHandler {
    private final MessageEventMapper messageEventMapper;
    private final MessageDocumentRepository messageDocumentRepository;

    @Override
    public void processEvent(MessageSentEvent messageEvent) {
        MessageDocument messageDocument = messageEventMapper.toMessageDocument(messageEvent);
        messageDocumentRepository.save(messageDocument);
        log.info("Indexed message: messageId={}, chatId={}",
                messageDocument.getMessageId(), messageDocument.getChatId());
    }

    @Override
    public MessageType getMessageType() {
        return MessageType.CREATED;
    }
}
