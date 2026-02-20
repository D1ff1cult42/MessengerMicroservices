package com.d1ff.chatservice.exceptions;

public class ParticipantAlreadyInChat extends RuntimeException {
    public ParticipantAlreadyInChat(String message) {
        super(message);
    }
}
