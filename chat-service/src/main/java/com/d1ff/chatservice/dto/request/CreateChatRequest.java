package com.d1ff.chatservice.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateChatRequest(
    @NotNull(message = "Chat name cannot be null")
    String name,
    MultipartFile multipartFile,
    String description
) {
}
