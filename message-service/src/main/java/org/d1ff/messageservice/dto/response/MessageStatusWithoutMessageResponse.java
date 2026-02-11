package org.d1ff.messageservice.dto.response;

import org.d1ff.messageservice.entity.DeliveryStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record MessageStatusWithoutMessageResponse(
    Long id,
    Long message,
    UUID userId,
    DeliveryStatus status,
    LocalDateTime date
)
{}
