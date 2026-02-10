package org.d1ff.messageservice.controller.message;

import lombok.RequiredArgsConstructor;
import org.d1ff.messageservice.dto.response.MessageResponse;
import org.d1ff.messageservice.exceptions.AccessDeniedException;
import org.d1ff.messageservice.service.interfaces.MessageService;
import org.d1ff.page.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/messages")
public class MessageAdminRestController {
    private final MessageService messageService;

    @GetMapping("/{userId}")
    public ResponseEntity<PageResponse<MessageResponse>> getUserMessagesForAdmin(
            @PathVariable UUID userId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if(role.equals("ADMIN")){
            Pageable pageable = PageRequest.of(page, size);
            Page messages = messageService.getMessagesOfUserForAdmin(userId, pageable);
            return ResponseEntity.ok(PageResponse.fromPage(messages));
        }
        throw new AccessDeniedException("Access denied!");
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<PageResponse<MessageResponse>> getMessagesInChatForAdmin(
            @PathVariable UUID chatId,
            @RequestHeader("X-User-Role") String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if(role.equals("ADMIN")){
            Pageable pageable = PageRequest.of(page, size);
            Page messages = messageService.getMessagesInChat(chatId, pageable);
            return ResponseEntity.ok(PageResponse.fromPage(messages));
        }
        throw new AccessDeniedException("Access denied!");
    }
}
