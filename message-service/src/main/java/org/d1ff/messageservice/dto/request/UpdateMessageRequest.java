package org.d1ff.messageservice.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateMessageRequest(
        @NotNull
        Long messageId,
        String content
) {}
