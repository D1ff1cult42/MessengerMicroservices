package com.d1ff.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FileUploadResponse(
        String bucketName,
        String objectName
) {}
