package org.d1ff.messageservice.dto;

public record MinioFileProperties(
        String bucketName,
        String objectName
) {}
