package com.d1ff.realtimegateway.service.impl;

import com.d1ff.realtimegateway.dto.event.MessageDeliveredEvent;
import com.d1ff.realtimegateway.dto.event.MessageSentEvent;
import com.d1ff.realtimegateway.dto.response.EventType;
import com.d1ff.realtimegateway.dto.response.WsResponse;
import com.d1ff.realtimegateway.service.interfaces.RealtimeMessageService;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RealtimeMessageServiceImpl implements RealtimeMessageService {

    private final SimpUserRegistry userRegistry;
    private final SimpMessagingTemplate messagingTemplate;

    @AsyncPublisher(operation = @AsyncOperation(
            channelName = "/topic/chat.{chatId}.messages",
            description = "Новое сообщение в чате"
    ))
    @Override
    public void handleMessageSent(MessageSentEvent event){
        log.info("Новое сообщение в чате {}: messageId={}", event.chatId(), event.messageId());
        WsResponse response = new WsResponse(
                EventType.MESSAGE,
                event,
                LocalDateTime.now()
        );

        String destination = "/topic/chat." + event.chatId() + ".messages";
        messagingTemplate.convertAndSend(destination, response);

        Set<String> onlineUserIds = userRegistry.getUsers()
                .stream()
                .filter(user -> user.getSessions().stream()
                        .anyMatch(session -> session.getSubscriptions().stream()
                                .anyMatch(sub -> sub.getDestination().equals(destination))))
                .map(SimpUser::getName)
                .filter(id -> !id.equals(event.senderId().toString()))
                .collect(Collectors.toSet());

        for (String userId : onlineUserIds){
            MessageDeliveredEvent delivered = new MessageDeliveredEvent(
                    event.messageId(),
                    event.chatId(),
                    UUID.fromString(userId),
                    "DELIVERED",
                    LocalDateTime.now()
            );
            handleMessageDelivered(delivered);
        }
    }

    @AsyncPublisher(operation = @AsyncOperation(
            channelName = "/topic/chat.{}.messages",
            description = "Статус доставки сообщения"
    ))
    @Override
    public void handleMessageDelivered(MessageDeliveredEvent event) {
        log.info("Сообщение {} доставлено пользователю {} в чате {}",
                event.messageId(), event.userId(), event.chatId());
        WsResponse response = new WsResponse(
                EventType.DELIVERY,
                event,
                LocalDateTime.now()
        );

        String destination = "/topic/chat." + event.chatId() + ".messages";
        messagingTemplate.convertAndSend(destination, response);
    }
}
