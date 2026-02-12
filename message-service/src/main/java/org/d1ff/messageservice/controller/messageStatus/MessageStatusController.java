package org.d1ff.messageservice.controller.messageStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.d1ff.dto.response.ErrorResponse;
import org.d1ff.messageservice.dto.response.MessageStatusResponse;
import org.d1ff.messageservice.dto.response.UnreadMessagesCounterResponse;
import org.d1ff.messageservice.service.interfaces.MessageStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/message-status")
@Tag(name = "MessageStatusController", description = "Controller for managing individual message status")
public class MessageStatusController {
    private final MessageStatusService messageStatusService;

    @Operation(summary = "Get message and mark as read",
            description = "Retrieve a message and update its status to READ for the requesting user.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Message status updated successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MessageStatusResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "404",
                    description = "Message not found",
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
    @PatchMapping("/message/{messageId}")
    public ResponseEntity<MessageStatusResponse> getMessageWithStatus(
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "ID of the message to retrieve and mark as read", required = true)
            @PathVariable Long messageId) {
        return ResponseEntity.ok(messageStatusService.getMessageWithStatus(userId, messageId));
    }

    @Operation(summary = "Get last message status in chat",
            description = "Retrieve the status of the last message in a chat for the requesting user.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Last message status retrieved successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MessageStatusResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "404",
                    description = "Chat not found or no messages in chat",
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
    @GetMapping("/chat/{chatId}/last")
    public ResponseEntity<MessageStatusResponse> getLastMessageStatusForUserInChat(
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "ID of the chat", required = true)
            @PathVariable UUID chatId) {
        return ResponseEntity.ok(messageStatusService.getLastMessageStatusForUserInChat(userId, chatId));
    }

    @Operation(summary = "Get unread messages count in chat",
            description = "Retrieve the count of unread messages in a specific chat for the requesting user.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Unread count retrieved successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = UnreadMessagesCounterResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "403",
                    description = "Access denied",
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
    @GetMapping("/chat/{chatId}/unread-count")
    public ResponseEntity<UnreadMessagesCounterResponse> unreadMessageCounterInChat(
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "ID of the chat", required = true)
            @PathVariable UUID chatId) {
        long unreadCount = messageStatusService.getUnreadCountForChat(userId, chatId);
        return ResponseEntity.ok(new UnreadMessagesCounterResponse(unreadCount, chatId, userId));
    }
}
