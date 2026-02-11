package org.d1ff.messageservice.dto.response;

import org.d1ff.messageservice.entity.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageStatusResponse(
        Long id,
        MessageResponse message,
        UUID userId,
        DeliveryStatus status,
        LocalDateTime date
) {}
