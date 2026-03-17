package com.d1ff.fulltextsearchservice.exceptions;

public class DocumentNotFound extends RuntimeException {
    public DocumentNotFound(String message) {
        super(message);
    }
}
