package org.d1ff.messageservice.controller.message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.d1ff.dto.response.ErrorResponse;
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
@Tag(name = "MessagesRestController", description = "Controller for sending messages")
public class MessagesRestController {
    private final MessageService messageService;

    @Operation(summary = "Send a new message",
            description = "Send a new message with optional file attachments. Supports multipart form data.",
            responses = {
                @ApiResponse(
                    responseCode = "201",
                    description = "Message sent successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MessageResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "404",
                    description = "Chat not found",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                )
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageResponse> sendMessage(
            @Parameter(description = "Message data with optional attachments", required = true)
            @Valid @ModelAttribute CreateMessageRequest message,
            @Parameter(description = "ID of the user sending the message(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId) {
        MessageResponse messageResponse = messageService.sendMessage(message, userId);
        return ResponseEntity.created(URI.create("/api/messages/" + messageResponse.id()))
                .body(messageResponse);
    }
}
