package org.d1ff.messageservice.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record CreateMessageRequest(
        @NotNull(message = "Chat ID cannot be null")
        UUID chatId,
        String content,
        MultipartFile multipartFile,
        Long replyTo
        ){
    @AssertTrue(message = "Either content or multipartFile must be provided")
    public boolean isContentOrFileValid() {
        boolean hasContent = content != null && !content.trim().isEmpty();
        boolean hasFile = multipartFile != null && !multipartFile.isEmpty();

        return hasContent || hasFile;
    }
}
