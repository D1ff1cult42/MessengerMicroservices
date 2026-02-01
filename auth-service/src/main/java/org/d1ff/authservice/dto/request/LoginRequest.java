package org.d1ff.authservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "LoginRequest", description = "Request to log in a user")
public record LoginRequest(
    @Schema(description = "User's email address", example = "user@gmail.com")
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @Schema(description = "User's password", example = "strongpassword123")
    @NotBlank(message = "Password is required")
    String password
    )
{}

