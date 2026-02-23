package com.d1ff.accountservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

@Schema(name = "CreateAccountRequest", description = "Request to create a user account profile")
public record CreateAccountRequest(
    @Schema(description = "Optional avatar image file (jpg, jpeg, png, gif, bmp, webp, svg)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    MultipartFile file,
    @Schema(description = "Display username", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull
    String username,
    @Schema(description = "Short profile description", example = "Just a developer.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    String description
) {}
