package com.d1ff.chatservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

@Schema(name = "AddParticipantRequest", description = "Request to add a participant to a chat")
public record AddParticipantRequest(
        @Schema(description = "Email of the user to add to the chat", example = "user@example.com")
        @NotNull
        @Email
        String email
) {}
