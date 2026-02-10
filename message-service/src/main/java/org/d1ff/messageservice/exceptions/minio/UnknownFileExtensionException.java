package org.d1ff.messageservice.exceptions.minio;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UnknownFileExtensionException extends RuntimeException {
    public UnknownFileExtensionException(String message) {
        super("Unknown file extension: " + message);
    }
}
