package com.d1ff.accountservice.dto.request;

import org.springframework.web.multipart.MultipartFile;

public record UpdateAccountRequest(
        String username,
        String description,
        MultipartFile file
){}
