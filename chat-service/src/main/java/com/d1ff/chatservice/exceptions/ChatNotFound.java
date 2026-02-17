package com.d1ff.chatservice.exceptions;

public class ChatNotFound extends RuntimeException {
    public ChatNotFound(String message) {
        super(message);
    }
}
