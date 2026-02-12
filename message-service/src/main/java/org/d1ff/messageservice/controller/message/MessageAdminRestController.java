package org.d1ff.messageservice.controller.message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.d1ff.dto.response.ErrorResponse;
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
@Tag(name = "MessageAdminRestController", description = "Admin controller for managing messages")
public class MessageAdminRestController {
    private final MessageService messageService;

    @Operation(summary = "Get user messages (Admin)",
            description = "Retrieve all messages sent by a specific user. This endpoint is only accessible by admins.",
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
                    description = "Access denied - Admin role required",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
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
    @GetMapping("/{userId}")
    public ResponseEntity<PageResponse<MessageResponse>> getUserMessagesForAdmin(
            @Parameter(description = "ID of the user whose messages to retrieve", required = true)
            @PathVariable UUID userId,
            @Parameter(description = "Role of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "Page number (0-indexed)", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", required = false)
            @RequestParam(defaultValue = "10") int size) {
        if(role.equals("ADMIN")){
            Pageable pageable = PageRequest.of(page, size);
            Page messages = messageService.getMessagesOfUserForAdmin(userId, pageable);
            return ResponseEntity.ok(PageResponse.fromPage(messages));
        }
        throw new AccessDeniedException("Access denied!");
    }

    @Operation(summary = "Get messages in chat (Admin)",
            description = "Retrieve all messages in a specific chat. This endpoint is only accessible by admins.",
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
                    description = "Access denied - Admin role required",
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
    @GetMapping("/{chatId}")
    public ResponseEntity<PageResponse<MessageResponse>> getMessagesInChatForAdmin(
            @Parameter(description = "ID of the chat to retrieve messages from", required = true)
            @PathVariable UUID chatId,
            @Parameter(description = "Role of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "Page number (0-indexed)", required = false)
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", required = false)
            @RequestParam(defaultValue = "10") int size) {
        if(role.equals("ADMIN")){
            Pageable pageable = PageRequest.of(page, size);
            Page messages = messageService.getMessagesInChat(chatId, pageable);
            return ResponseEntity.ok(PageResponse.fromPage(messages));
        }
        throw new AccessDeniedException("Access denied!");
    }
}
