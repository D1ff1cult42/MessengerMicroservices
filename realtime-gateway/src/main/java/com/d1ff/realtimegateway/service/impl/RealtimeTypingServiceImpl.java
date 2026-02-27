package com.d1ff.realtimegateway.service.impl;

import com.d1ff.realtimegateway.dto.event.TypingEvent;
import com.d1ff.realtimegateway.dto.response.EventType;
import com.d1ff.realtimegateway.dto.response.WsResponse;
import com.d1ff.realtimegateway.service.interfaces.RealtimeTypingService;
import io.github.springwolf.core.asyncapi.annotations.AsyncOperation;
import io.github.springwolf.core.asyncapi.annotations.AsyncPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class RealtimeTypingServiceImpl implements RealtimeTypingService {
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @AsyncPublisher(operation = @AsyncOperation(
            channelName = "/topic/chat.{chatId}.typing",
            description = "Индикатор печатания"
    ))
    public void handleTyping(TypingEvent event){
        log.info("Пользователь {} {} в чате {}",
                event.userId(),
                event.typing() ? "печатает" : "перестал печатать",
                event.chatId());

        WsResponse response = new WsResponse(
                EventType.TYPING,
                event,
                LocalDateTime.now()
        );

        String destination = "/topic/chat." + event.chatId() + ".typing";
        messagingTemplate.convertAndSend(destination, response);
    }
}
