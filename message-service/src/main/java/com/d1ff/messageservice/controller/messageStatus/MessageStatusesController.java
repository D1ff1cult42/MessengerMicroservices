package com.d1ff.messageservice.controller.messageStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import com.d1ff.dto.response.ErrorResponse;
import com.d1ff.messageservice.dto.response.MessageStatusResponse;
import com.d1ff.messageservice.dto.response.MessageStatusWithoutMessageResponse;
import com.d1ff.messageservice.service.interfaces.MessageStatusService;
import com.d1ff.page.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/message-statuses")
@Tag(name = "MessageStatusesController", description = "Controller for managing multiple message statuses")
public class MessageStatusesController {
    private final MessageStatusService messageStatusService;

    @Operation(summary = "Get all statuses for a message",
            description = "Retrieve all read statuses for a specific message across all recipients.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Message statuses retrieved successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PageResponse.class)
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
    public ResponseEntity<PageResponse<MessageStatusWithoutMessageResponse>> getAllMessageStatuses(
            @Parameter(description = "ID of the message", required = true)
            @PathVariable Long messageId,
            @Parameter(description = "Page number (0-indexed)", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", required = false)
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(PageResponse
                .fromPage(messageStatusService
                        .getAllStatusForMessage(messageId, PageRequest.of(page, size))));
    }

    @Operation(summary = "Get messages for user in chat",
            description = "Retrieve all messages with their statuses for a specific user in a chat.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Messages retrieved successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PageResponse.class)
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
    @PatchMapping("/chat/{chatId}")
    public ResponseEntity<PageResponse<MessageStatusResponse>> getMessagesForUserInChat(
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "ID of the chat", required = true)
            @PathVariable UUID chatId,
            @Parameter(description = "Page number (0-indexed)", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", required = false)
            @RequestParam(defaultValue = "10") int size){
        return ResponseEntity.ok(PageResponse
                .fromPage(messageStatusService
                        .getMessagesForUserInChat(userId, chatId, PageRequest.of(page, size))));
    }

    @Operation(summary = "Get all statuses from a user in chat",
            description = "Retrieve all message statuses for messages sent by a specific user in a chat. Admins can view statuses for any user, regular users can only view statuses for their own messages.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Statuses retrieved successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = PageResponse.class)
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
                    description = "User or chat not found",
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
    @GetMapping("/from-user/{userId}")
    public ResponseEntity<PageResponse<MessageStatusResponse>> getAllStatusesFromUserInChat(
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID participantId,
            @Parameter(description = "Role of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "ID of the user whose message statuses to retrieve", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "ID of the chat", required = true)
            @RequestParam UUID chatId,
            @Parameter(description = "Page number (0-indexed)", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", required = false)
            @RequestParam(defaultValue = "10") int size){
        Page<MessageStatusResponse> response;
        if(role.equals("ADMIN")){
            response = messageStatusService.getAllStatusesForMessageForAdmin(userId, chatId, PageRequest.of(page, size));
        }else{
            response = messageStatusService.getAllStatusesFromUserInChat(participantId, userId, chatId, PageRequest.of(page, size));
        }
        return ResponseEntity.ok(PageResponse.fromPage(response));
     }
}
