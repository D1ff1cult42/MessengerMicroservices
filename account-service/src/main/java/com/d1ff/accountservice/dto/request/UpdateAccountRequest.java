package com.d1ff.accountservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.multipart.MultipartFile;

@Schema(name = "UpdateAccountRequest", description = "Request to update user account profile fields")
public record UpdateAccountRequest(
        @Schema(description = "New display username", example = "john_doe_updated", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String username,
        @Schema(description = "New profile description", example = "Updated bio.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String description,
        @Schema(description = "New avatar image file (jpg, jpeg, png, gif, bmp, webp, svg)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        MultipartFile file
){}
