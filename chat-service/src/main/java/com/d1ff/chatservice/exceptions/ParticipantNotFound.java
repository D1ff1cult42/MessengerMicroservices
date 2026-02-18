package com.d1ff.chatservice.exceptions;

public class ParticipantNotFound extends RuntimeException{
    public ParticipantNotFound(String message){
        super(message);
    }
}
