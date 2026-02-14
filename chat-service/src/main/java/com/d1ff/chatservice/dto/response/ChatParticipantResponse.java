package com.d1ff.chatservice.dto.response;

import com.d1ff.chatservice.entity.ChatRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatParticipantResponse (
        UUID userId,
        ChatRole role,
        LocalDateTime joinedAt,
        boolean isMuted
){}
