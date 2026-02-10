package org.d1ff.messageservice.controller.message;

import lombok.RequiredArgsConstructor;
import org.d1ff.messageservice.dto.request.CreateMessageRequest;
import org.d1ff.messageservice.dto.response.MessageResponse;
import org.d1ff.messageservice.service.interfaces.MessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages")
public class MessagesRestController {
    private final MessageService messageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> sendMessage(
            @ModelAttribute CreateMessageRequest message,
            @RequestHeader("X-User-Id") UUID userId) {
        MessageResponse messageResponse = messageService.sendMessage(message, userId);
        return ResponseEntity.created(URI.create("/api/messages/" + messageResponse.id()))
                .body(messageResponse);
    }
}
