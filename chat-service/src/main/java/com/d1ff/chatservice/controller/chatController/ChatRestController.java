package com.d1ff.chatservice.controller.chatController;

import com.d1ff.chatservice.dto.request.UpdateChatRequest;
import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.service.interfaces.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.d1ff.dto.response.ErrorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats/{chatId}")
@Tag(name = "ChatRestController", description = "Controller for managing individual chats")
public class ChatRestController {

    private final ChatService chatService;

    @Operation(summary = "Get chat by ID",
            description = "Retrieve a chat by its ID with paginated participants.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Chat retrieved successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ChatResponse.class)
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
    @GetMapping
    public ResponseEntity<ChatResponse> getChat(
            @Parameter(description = "ID of the chat to retrieve", required = true)
            @PathVariable UUID chatId,
            @Parameter(description = "Page number for participants pagination")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size for participants pagination")
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        ChatResponse chat = chatService.getChat(chatId, pageable);
        return ResponseEntity.ok(chat);
    }

    @Operation(summary = "Delete a chat",
            description = "Soft-delete a chat by its ID. Only the chat owner can delete the chat.",
            responses = {
                @ApiResponse(
                    responseCode = "204",
                    description = "Chat deleted successfully"
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
    @DeleteMapping
    public ResponseEntity<Void> deleteChat(
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "ID of the chat to delete", required = true)
            @PathVariable UUID chatId){
        chatService.deleteChat(userId, chatId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a chat",
            description = "Update the name, description, or icon of a chat. Only OWNER or ADMIN participants can update the chat.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Chat updated successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ChatResponse.class)
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
                    responseCode = "403",
                    description = "Access denied",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "404",
                    description = "Chat not found or participant not found",
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
    @PatchMapping
    public ResponseEntity<ChatResponse> updateChat(
            @Parameter(description = "ID of the chat to update", required = true)
            @PathVariable UUID chatId,
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Chat update data", required = true)
            @RequestBody @Valid UpdateChatRequest updateChatRequest,
            @Parameter(description = "Page number for participants pagination")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size for participants pagination")
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        ChatResponse chat = chatService.updateChat(updateChatRequest, chatId, userId, pageable);
        return ResponseEntity.ok(chat);
    }
}
