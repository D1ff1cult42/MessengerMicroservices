package com.d1ff.chatservice.dto.response;

import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatResponse (
    UUID id,
    String name,
    String description,
    LocalDateTime createdAt,
    String presignedIconUrl,
    boolean isGroupChat,
    Page<ChatParticipantResponse> participants
){}

