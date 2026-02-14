package com.d1ff.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class PresignedUrlException extends RuntimeException {
    public PresignedUrlException(String message) {
        super("Failed to generate presigned URL: " + message);
    }
}
