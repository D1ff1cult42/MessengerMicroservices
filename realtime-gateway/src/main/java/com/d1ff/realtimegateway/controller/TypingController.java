package com.d1ff.realtimegateway.controller;

import com.d1ff.realtimegateway.dto.event.TypingEvent;
import com.d1ff.realtimegateway.dto.request.TypingRequest;
import com.d1ff.realtimegateway.service.interfaces.RealtimeTypingService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TypingController {
    private final RealtimeTypingService typingService;

    @MessageMapping("/typing")
    public void typing(@Payload TypingRequest request, Principal principal){
        UUID userId = UUID.fromString(principal.getName());
        TypingEvent event = new TypingEvent(
                request.chatId(),
                userId,
                request.typing(),
                LocalDateTime.now()
        );
        typingService.handleTyping(event);
    }
}
