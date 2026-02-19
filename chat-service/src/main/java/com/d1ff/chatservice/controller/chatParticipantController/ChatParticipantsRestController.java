package com.d1ff.chatservice.controller.chatParticipantController;

import com.d1ff.chatservice.dto.request.AddParticipantRequest;
import com.d1ff.chatservice.dto.request.KickParticipantRequest;
import com.d1ff.chatservice.dto.request.UpdateParticipantStatusRequest;
import com.d1ff.chatservice.dto.response.ChatParticipantResponse;
import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.service.interfaces.ChatParticipantService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat-participants")
@RequiredArgsConstructor
public class ChatParticipantsRestController {
    private final ChatParticipantService chatParticipantService;

    @PatchMapping
    public ResponseEntity<ChatParticipantResponse> updateUserRole(@RequestBody @Valid UpdateParticipantStatusRequest updateParticipantStatusRequest,
                                                                  @RequestHeader(name = "X-User-Id") UUID userId){
        ChatParticipantResponse chatParticipantResponse = chatParticipantService
                .updateUserRole(updateParticipantStatusRequest,userId);
        return ResponseEntity.ok(chatParticipantResponse);
    }

    @PostMapping("/{chatId}")
    public ResponseEntity<ChatResponse> addParticipant(@PathVariable UUID chatId,
                                                       @RequestHeader("X-User-Id") UUID userId,
                                                       @RequestBody @Valid AddParticipantRequest addParticipantRequest,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        ChatResponse chatResponse = chatParticipantService.addParticipant(userId, chatId, addParticipantRequest, pageable);
        return ResponseEntity.ok(chatResponse);
    }

    @DeleteMapping
    public ResponseEntity<Void> kickParticipant(@RequestHeader("X-User-Id") UUID userId,
                                                @RequestBody @Valid KickParticipantRequest kickParticipantRequest){
        chatParticipantService.kickParticipant(userId, kickParticipantRequest);
        return ResponseEntity.noContent().build();
    }
}
