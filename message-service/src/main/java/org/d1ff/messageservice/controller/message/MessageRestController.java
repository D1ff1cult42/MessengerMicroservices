package org.d1ff.messageservice.controller.message;

import lombok.RequiredArgsConstructor;
import org.d1ff.messageservice.dto.request.UpdateMessageRequest;
import org.d1ff.messageservice.dto.response.MessageResponse;
import org.d1ff.messageservice.service.interfaces.MessageService;
import org.d1ff.page.PageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages/{messageId}")
public class MessageRestController {
    private MessageService messageService;

    @PatchMapping("/delete")
    public ResponseEntity<Void> deleteMessage(@RequestHeader("X-User-Id") UUID userId,
                                              @RequestHeader("X-User-Role") String role,
                                              @PathVariable Long messageId) {
        if (role.equals("ADMIN")) {
            messageService.deleteMessageForAdmin(messageId);
        } else {
            messageService.deleteMessage(userId, messageId);
        }
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/update")
    public ResponseEntity<MessageResponse> updateMessage(@RequestHeader("X-User-Id") UUID userId,
                                                         @RequestBody UpdateMessageRequest updateMessageRequest) {
        MessageResponse updatedMessage = messageService.updateMessage(userId, updateMessageRequest);
        return ResponseEntity.ok(updatedMessage);
    }

    @GetMapping
    public ResponseEntity<MessageResponse> getMessageWithoutStatusChange(@RequestHeader("X-User-Id") UUID userId,
                                                                         @RequestHeader("X-User-Role") String role,
                                                                         @PathVariable Long messageId) {
        if (role.equals("ADMIN")) {
            return ResponseEntity.ok(messageService.getMessageByIdForAdmin(messageId));
        } else {
            return ResponseEntity.ok(messageService.getMessageById(userId, messageId));
        }
    }
}
