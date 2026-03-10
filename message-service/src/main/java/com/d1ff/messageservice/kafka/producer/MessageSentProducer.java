package com.d1ff.messageservice.kafka.producer;

import com.d1ff.common.avro.MessageSentEvent;
import com.d1ff.common.avro.MessageType;
import com.d1ff.messageservice.entity.Message;
import com.d1ff.messageservice.entity.OutboxEvent;
import com.d1ff.messageservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageSentProducer {
    private final OutboxEventRepository outboxEventRepository;

    public void send(Message message, MessageType messageType) {
        try {
            MessageSentEvent messageSentEvent = MessageSentEvent.newBuilder()
                    .setEventId(UUID.randomUUID())
                    .setMessageId(message.getId())
                    .setTimestamp(Instant.now())
                    .setChatId(message.getChatId())
                    .setContent(message.getContent())
                    .setSenderId(message.getFromUser())
                    .setMessageType(messageType)
                    .build();

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .aggregateId(message.toString())
                    .topic("message-sent")
                    .payload(messageSentEvent.toByteBuffer().array())
                    .build();

            outboxEventRepository.save(outboxEvent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
