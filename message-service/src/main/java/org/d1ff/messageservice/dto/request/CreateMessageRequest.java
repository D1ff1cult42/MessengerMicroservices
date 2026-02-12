package org.d1ff.messageservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Schema(name = "CreateMessageRequest", description = "Request to create a new message in a chat")
public record CreateMessageRequest(
        @Schema(description = "ID of the chat where the message will be sent", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull(message = "Chat ID cannot be null")
        UUID chatId,

        @Schema(description = "Text content of the message, needed this obj or multipartFile for message", example = "Hello, how are you?")
        String content,

        @Schema(description = "File to be attached to the message, need multipart form data value, " +
                "needed this obj or content for message", example = "file.jpg")
        MultipartFile multipartFile,

        @Schema(description = "ID of the message this message is replying to", example = "42")
        Long replyTo
        ){
    @AssertTrue(message = "Either content or multipartFile must be provided")
    public boolean isContentOrFileValid() {
        boolean hasContent = content != null && !content.trim().isEmpty();
        boolean hasFile = multipartFile != null && !multipartFile.isEmpty();

        return hasContent || hasFile;
    }
}
