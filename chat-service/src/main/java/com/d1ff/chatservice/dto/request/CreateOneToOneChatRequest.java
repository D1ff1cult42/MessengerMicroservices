package com.d1ff.chatservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(name = "CreateOneToOneChatRequest", description = "Request to create a one-to-one chat")
public record CreateOneToOneChatRequest(
    @Schema(description = "Email of the other user to start a chat with", example = "user@example.com")
    @NotNull
    @Email
    String otherUserEmail
) {}
