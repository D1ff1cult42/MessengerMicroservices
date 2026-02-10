package org.d1ff.messageservice.controller.messageStatus;

import lombok.RequiredArgsConstructor;
import org.d1ff.messageservice.dto.response.MessageStatusResponse;
import org.d1ff.messageservice.dto.response.MessageStatusWithoutMessageResponse;
import org.d1ff.messageservice.service.interfaces.MessageStatusService;
import org.d1ff.page.PageResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message-statuses")
public class MessageStatusesController {
    private final MessageStatusService messageStatusService;

    @PatchMapping("/message/{messageId}")
    public ResponseEntity<PageResponse<MessageStatusWithoutMessageResponse>> getAllMessageStatuses(@PathVariable Long messageId,
                                                                                                  @RequestParam(defaultValue = "0") int page,
                                                                                                  @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(PageResponse
                .fromPage(messageStatusService
                        .getAllStatusForMessage(messageId, PageRequest.of(page, size))));
    }

    @PatchMapping("/chat/{chatId}")
    public ResponseEntity<PageResponse<MessageStatusResponse>> getMessagesForUserInChat(@RequestHeader("X-User-Id") UUID userId,
                                                              @PathVariable UUID chatId,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(PageResponse
                .fromPage(messageStatusService
                        .getMessagesForUserInChat(userId, chatId, PageRequest.of(page, size))));
    }
}
