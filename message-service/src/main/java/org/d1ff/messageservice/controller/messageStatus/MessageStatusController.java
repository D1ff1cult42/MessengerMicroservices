package org.d1ff.messageservice.controller.messageStatus;

import lombok.RequiredArgsConstructor;
import org.d1ff.messageservice.dto.response.MessageStatusResponse;
import org.d1ff.messageservice.dto.response.UnreadMessagesCounterResponse;
import org.d1ff.messageservice.service.interfaces.MessageStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/message-status")
public class MessageStatusController {
    private final MessageStatusService messageStatusService;

    @PatchMapping("/message/{messageId}")
    public ResponseEntity<MessageStatusResponse> getMessageWithStatus(@RequestHeader("X-User-Id") UUID userId,
                                                                      @PathVariable Long messageId) {
        return ResponseEntity.ok(messageStatusService.getMessageWithStatus(userId, messageId));
    }

    @GetMapping("/chat/{chatId}/last")
    public ResponseEntity<MessageStatusResponse> getLastMessageStatusForUserInChat(@RequestHeader("X-User-Id") UUID userId,
                                                                                   @PathVariable UUID chatId) {
        return ResponseEntity.ok(messageStatusService.getLastMessageStatusForUserInChat(userId, chatId));
    }

    @GetMapping("/chat/{chatId}/unread-count")
    public ResponseEntity<UnreadMessagesCounterResponse> unreadMessageCounterInChat(@RequestHeader("X-User-Id") UUID userId,
                                                                                    @PathVariable UUID chatId) {
        long unreadCount = messageStatusService.getUnreadCountForChat(userId, chatId);
        return ResponseEntity.ok(new UnreadMessagesCounterResponse(unreadCount, chatId, userId));
    }
}
