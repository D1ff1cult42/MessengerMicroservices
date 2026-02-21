package com.d1ff.accountservice.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record CreateAccountRequest(
    MultipartFile file,
    @NotNull
    String username,
    String description
) {}
