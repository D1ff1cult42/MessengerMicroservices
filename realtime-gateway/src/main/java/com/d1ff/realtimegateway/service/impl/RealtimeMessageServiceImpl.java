package com.d1ff.realtimegateway.service.impl;

import com.d1ff.common.avro.MessageDeliveredEvent;
import com.d1ff.common.avro.MessageSentEvent;
import com.d1ff.realtimegateway.dto.response.EventType;
import com.d1ff.realtimegateway.dto.response.WsResponse;
import com.d1ff.realtimegateway.kafka.producer.MessageDeliveredProducer;
import com.d1ff.realtimegateway.service.interfaces.RealtimeMessageService;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealtimeMessageServiceImpl implements RealtimeMessageService {

    private final SimpUserRegistry userRegistry;
    private final MessageDeliveredProducer messageDeliveredProducer;
    private final SimpMessagingTemplate messagingTemplate;

    @AsyncPublisher(operation = @AsyncOperation(
            channelName = "/topic/chat.{chatId}.messages",
            description = "Новое сообщение в чате"
    ))
    @Override
    public void handleMessageSent(MessageSentEvent event){
        log.info("Новое сообщение в чате {}: messageId={}", event.getChatId(), event.getMessageId());
        WsResponse response = new WsResponse(
                EventType.MESSAGE,
                event,
                LocalDateTime.now()
        );

        String destination = "/topic/chat." + event.getChatId() + ".messages";
        messagingTemplate.convertAndSend(destination, response);

        Set<String> onlineUserIds = userRegistry.getUsers()
                .stream()
                .filter(user -> user.getSessions().stream()
                        .anyMatch(session -> session.getSubscriptions().stream()
                                .anyMatch(sub -> sub.getDestination().equals(destination))))
                .map(SimpUser::getName)
                .filter(id -> !id.equals(event.getSenderId().toString()))
                .collect(Collectors.toSet());

        for (String userId : onlineUserIds){
            MessageDeliveredEvent delivered = MessageDeliveredEvent.newBuilder()
                    .setMessageId(event.getMessageId())
                    .setChatId(event.getChatId())
                    .setUserId(UUID.fromString(userId))
                    .setTimestamp(Instant.now())
                    .build();
            messageDeliveredProducer.handleMessageDelivered(delivered);
        }
    }
}
