package com.d1ff.chatservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import com.d1ff.page.PageResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(name = "ChatResponse", description = "Chat information response")
public record ChatResponse (
    @Schema(description = "Unique identifier of the chat", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,

    @Schema(description = "Name of the chat", example = "My Group Chat")
    String name,

    @Schema(description = "Description of the chat", example = "A chat for discussing projects")
    String description,

    @Schema(description = "Timestamp when the chat was created", example = "2025-01-15T10:30:00")
    LocalDateTime createdAt,

    @Schema(description = "Presigned URL of the chat icon")
    String presignedIconUrl,

    @Schema(description = "TTL of the presigned icon URL in seconds", example = "3600")
    Long iconUrlTtl,

    @Schema(description = "Whether the chat is a group chat", example = "true")
    boolean isGroupChat,

    @Schema(description = "Paginated list of chat participants")
    PageResponse<ChatParticipantResponse> participants
){}

