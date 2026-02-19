package com.d1ff.chatservice.controller.chatController;

import com.d1ff.chatservice.dto.request.UpdateChatRequest;
import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.service.interfaces.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats/{chatId}")
public class ChatRestController {

    private final ChatService chatService;

    @GetMapping
    public ResponseEntity<ChatResponse> getChat(@PathVariable UUID chatId,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        ChatResponse chat = chatService.getChat(chatId, pageable);
        return ResponseEntity.ok(chat);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteChat(@RequestHeader("X-User-Id") UUID userId,
                                           @PathVariable UUID chatId){
        chatService.deleteChat(userId, chatId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    public ResponseEntity<ChatResponse> updateChat(@PathVariable UUID chatId,
                                                   @RequestHeader("X-User-Id") UUID userId,
                                                   @RequestBody @Valid UpdateChatRequest updateChatRequest,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        ChatResponse chat = chatService.updateChat(updateChatRequest, userId, pageable);
        return ResponseEntity.ok(chat);
    }
}
