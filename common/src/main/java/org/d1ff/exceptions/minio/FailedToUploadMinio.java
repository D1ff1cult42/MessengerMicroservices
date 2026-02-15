package org.d1ff.messageservice.exceptions.minio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class FailedToUploadMinio extends RuntimeException {
    public FailedToUploadMinio(String message) {
        super("Failed to upload file: "+ message);
    }
}
