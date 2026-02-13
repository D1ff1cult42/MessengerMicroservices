package com.d1ff.fileservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class FileUploadException extends RuntimeException {
    public FileUploadException(String message) {
        super("Failed to upload file: " + message);
    }
}
