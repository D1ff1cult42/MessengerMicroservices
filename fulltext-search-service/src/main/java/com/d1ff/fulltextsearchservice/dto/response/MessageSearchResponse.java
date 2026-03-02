package com.d1ff.fulltextsearchservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "MessageSearchResponse", description = "Single message search result with relevance score")
public record MessageSearchResponse(
        @Schema(description = "Unique identifier of the message", example = "550e8400-e29b-41d4-a716-446655440000")
        String messageId,

        @Schema(description = "ID of the chat the message belongs to", example = "550e8400-e29b-41d4-a716-446655440000")
        String chatId,

        @Schema(description = "Text content of the message", example = "Привет, как дела?")
        String text,

        @Schema(description = "Elasticsearch relevance score", example = "4.25")
        float score
) {}
