package com.d1ff.messageservice.controller.message;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import com.d1ff.dto.response.ErrorResponse;
import com.d1ff.messageservice.dto.response.MessageResponse;
import com.d1ff.messageservice.service.interfaces.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/messages/{messageId}")
@Tag(name = "MessageRestController", description = "Controller for managing individual messages")
public class MessageRestController {
    private final MessageService messageService;

    @Operation(summary = "Delete a message",
            description = "Delete a message by its ID. Admins can delete any message, regular users can only delete their own messages.",
            responses = {
                @ApiResponse(
                    responseCode = "204",
                    description = "Message deleted successfully"
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
    @PatchMapping("/delete")
    public ResponseEntity<Void> deleteMessage(
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Role of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "ID of the message to delete", required = true)
            @PathVariable Long messageId,
            HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();

        String userAgent = request.getHeader("User-Agent");

        if (role.equals("ADMIN")) {
            messageService.deleteMessageForAdmin(messageId, ip, userAgent);
        } else {
            messageService.deleteMessage(userId, messageId, ip, userAgent);
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update message content",
            description = "Update the content of an existing message. Only the message author can update their own messages.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Message updated successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MessageResponse.class)
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
    @PatchMapping("/update")
    public ResponseEntity<MessageResponse> updateMessage(
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "ID of the message to update", required = true)
            @PathVariable Long messageId,
            @Parameter(description = "New content for the message", required = true)
            @RequestBody String newContent,
            HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null) ip = request.getRemoteAddr();

        String userAgent = request.getHeader("User-Agent");
        MessageResponse updatedMessage = messageService.updateMessage(userId, newContent, messageId, ip, userAgent);
        return ResponseEntity.ok(updatedMessage);
    }

    @Operation(summary = "Get message by ID",
            description = "Retrieve a message by its ID without changing its read status. Admins can view any message, regular users can only view messages they have access to.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Message retrieved successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = MessageResponse.class)
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
    @GetMapping
    public ResponseEntity<MessageResponse> getMessageWithoutStatusChange(
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Role of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Role") String role,
            @Parameter(description = "ID of the message to retrieve", required = true)
            @PathVariable Long messageId) {
        if (role.equals("ADMIN")) {
            return ResponseEntity.ok(messageService.getMessageByIdForAdmin(messageId));
        } else {
            return ResponseEntity.ok(messageService.getMessageById(userId, messageId));
        }
    }
}
