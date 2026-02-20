package com.d1ff.chatservice.controller.chatController;

import com.d1ff.chatservice.dto.request.CreateChatRequest;
import com.d1ff.chatservice.dto.request.CreateOneToOneChatRequest;
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

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
@Tag(name = "ChatsRestController", description = "Controller for creating chats")
public class ChatsRestController {

    private final ChatService chatService;

    @Operation(summary = "Create a group chat",
            description = "Create a new group chat with the specified participants. The creator becomes the OWNER of the chat.",
            responses = {
                @ApiResponse(
                    responseCode = "201",
                    description = "Group chat created successfully",
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
                    responseCode = "500",
                    description = "Internal server error",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                )
            }
    )
    @PostMapping("/group")
    public ResponseEntity<ChatResponse> createGroupChat(
            @Parameter(description = "ID of the user creating the chat(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Group chat creation data", required = true)
            @RequestBody @Valid CreateChatRequest createChatRequest,
            @Parameter(description = "Page number for participants pagination")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size for participants pagination")
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        ChatResponse chat = chatService.createGroupChat(userId, createChatRequest, pageable);
        return ResponseEntity.created(URI.create("/api/chats/" + chat.id())).body(chat);
    }

    @Operation(summary = "Create a one-to-one chat",
            description = "Create a new one-to-one chat between the current user and the specified user.",
            responses = {
                @ApiResponse(
                    responseCode = "201",
                    description = "One-to-one chat created successfully",
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
                    responseCode = "500",
                    description = "Internal server error",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                )
            }
    )
    @PostMapping
    public ResponseEntity<ChatResponse> createOneToOneChat(
            @Parameter(description = "ID of the user creating the chat(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "One-to-one chat creation data", required = true)
            @RequestBody @Valid CreateOneToOneChatRequest createOneToOneChatRequest,
            @Parameter(description = "Page number for participants pagination")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size for participants pagination")
            @RequestParam(defaultValue = "10") int size){

        Pageable pageable = PageRequest.of(page, size);
        ChatResponse chat = chatService.createOneToOneChat(userId, createOneToOneChatRequest, pageable);
        return ResponseEntity.created(URI.create("/api/chats/" + chat.id())).body(chat);
    }
}
