package com.d1ff.chatservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Schema(name = "CreateChatRequest", description = "Request to create a new group chat")
public record CreateChatRequest(
    @Schema(description = "Name of the group chat", example = "My Group Chat")
    @NotNull(message = "Chat name cannot be null")
    String name,

    @Schema(description = "Icon file for the group chat")
    MultipartFile multipartFile,

    @Schema(description = "Description of the group chat", example = "A chat for discussing projects")
    String description
) {
}
