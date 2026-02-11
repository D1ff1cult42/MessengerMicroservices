package org.d1ff.messageservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.d1ff.messageservice.entity.MessageType;

import java.time.LocalDateTime;
import java.util.UUID;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record MessageResponse(Long id,
                              UUID chatId,
                              UUID fromUser,
                              String content,
                              MessageType type,
                              String fileUrl,
                              String fileName,
                              Long fileSize,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt,
                              LocalDateTime deletedAt,
                              Boolean isEdited,
                              Long replyTo
                              ) {}

