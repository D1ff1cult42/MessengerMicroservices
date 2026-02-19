package com.d1ff.chatservice.controller.chatController;

import com.d1ff.chatservice.dto.request.CreateChatRequest;
import com.d1ff.chatservice.dto.request.CreateOneToOneChatRequest;
import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.service.interfaces.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatsRestController {

    private final ChatService chatService;

    @PostMapping("/group")
    public ResponseEntity<ChatResponse> createGroupChat(@RequestHeader("X-User-Id") UUID userId,
                                                        @RequestBody @Valid CreateChatRequest createChatRequest,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        ChatResponse chat = chatService.createGroupChat(userId, createChatRequest, pageable);
        return ResponseEntity.created(URI.create("/api/chats/" + chat.id())).body(chat);
    }

    @PostMapping
    public ResponseEntity<ChatResponse> createOneToOneChat(@RequestHeader("X-User-Id") UUID userId,
                                                           @RequestBody @Valid CreateOneToOneChatRequest createOneToOneChatRequest,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size){

        Pageable pageable = PageRequest.of(page, size);
        ChatResponse chat = chatService.createOneToOneChat(userId, createOneToOneChatRequest, pageable);
        return ResponseEntity.created(URI.create("/api/chats/" + chat.id())).body(chat);
    }
}
