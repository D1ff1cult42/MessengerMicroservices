package com.d1ff.chatservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(name = "UpdateChatRequest", description = "Request to update an existing chat")
public record UpdateChatRequest(
   @Schema(description = "New name for the chat", example = "Updated Chat Name")
   String name,

   @Schema(description = "New description for the chat", example = "Updated description")
   String description,

   @Schema(description = "New icon file for the chat")
   MultipartFile multipartFile
) {}
