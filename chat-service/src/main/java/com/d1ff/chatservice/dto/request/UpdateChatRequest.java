package com.d1ff.chatservice.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record UpdateChatRequest(
   String name,
   String description,
   MultipartFile multipartFile
) {}
