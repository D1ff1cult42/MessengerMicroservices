package org.d1ff.messageservice.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FileUploadResponse(
        String bucketName,
        String objectName
) {}
