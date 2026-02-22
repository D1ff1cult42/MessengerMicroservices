package com.d1ff.chatservice.controller.chatParticipantController;

import com.d1ff.chatservice.dto.request.AddParticipantRequest;
import com.d1ff.chatservice.dto.request.KickParticipantRequest;
import com.d1ff.chatservice.dto.request.UpdateParticipantStatusRequest;
import com.d1ff.chatservice.dto.response.ChatParticipantResponse;
import com.d1ff.chatservice.dto.response.ChatResponse;
import com.d1ff.chatservice.service.interfaces.ChatParticipantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.d1ff.dto.response.ErrorResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat-participants")
@RequiredArgsConstructor
@Tag(name = "ChatParticipantsRestController", description = "Controller for managing chat participants")
public class ChatParticipantsRestController {
    private final ChatParticipantService chatParticipantService;

    @Operation(summary = "Update participant role",
            description = "Update the role of a participant in a chat. Only OWNER can change roles.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Participant role updated successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ChatParticipantResponse.class)
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
                    description = "Participant or chat not found",
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
    public ResponseEntity<ChatParticipantResponse> updateUserRole(
            @Parameter(description = "Role update data", required = true)
            @RequestBody @Valid UpdateParticipantStatusRequest updateParticipantStatusRequest,
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader(name = "X-User-Id") UUID userId){
        ChatParticipantResponse chatParticipantResponse = chatParticipantService
                .updateUserRole(updateParticipantStatusRequest,userId);
        return ResponseEntity.ok(chatParticipantResponse);
    }

    @Operation(summary = "Add participant to chat",
            description = "Add a new participant to a group chat. Only OWNER or ADMIN can add participants.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Participant added successfully",
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
                    description = "Chat or participant not found",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "409",
                    description = "Participant already in chat",
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
    @PostMapping("/{chatId}")
    public ResponseEntity<ChatResponse> addParticipant(
            @Parameter(description = "ID of the chat to add participant to", required = true)
            @PathVariable UUID chatId,
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Participant data to add", required = true)
            @RequestBody @Valid AddParticipantRequest addParticipantRequest,
            @Parameter(description = "Page number for participants pagination")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size for participants pagination")
            @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        ChatResponse chatResponse = chatParticipantService.addParticipant(userId, chatId, addParticipantRequest, pageable);
        return ResponseEntity.ok(chatResponse);
    }

    @Operation(summary = "Kick participant from chat",
            description = "Remove a participant from a group chat. Only OWNER or ADMIN can kick participants. ADMIN cannot kick other ADMINs or the OWNER.",
            responses = {
                @ApiResponse(
                    responseCode = "204",
                    description = "Participant kicked successfully"
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
                    description = "Chat or participant not found",
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
    public ResponseEntity<Void> kickParticipant(
            @Parameter(description = "ID of the user making the request(FOR DEBUG ONLY, THIS PARAMETER IS TAKEN FROM API-GATEWAY)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Kick participant data", required = true)
            @RequestBody @Valid KickParticipantRequest kickParticipantRequest){
        chatParticipantService.kickParticipant(userId, kickParticipantRequest);
        return ResponseEntity.noContent().build();
    }
}
