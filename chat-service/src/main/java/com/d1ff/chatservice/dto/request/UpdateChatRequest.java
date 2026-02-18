package com.d1ff.chatservice.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UpdateChatRequest(
   @NotNull
   UUID chatId,
   String name,
   String description,
   MultipartFile multipartFile
) {}
