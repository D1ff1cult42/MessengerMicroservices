package com.d1ff.chatservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record CreateOneToOneChatRequest(
    @NotNull
    @Email
    String otherUserEmail
) {}
