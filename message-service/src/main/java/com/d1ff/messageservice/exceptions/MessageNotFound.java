package com.d1ff.messageservice.exceptions;

public class MessageNotFound extends RuntimeException {
    public MessageNotFound(String message) {
        super(message);
    }
}
