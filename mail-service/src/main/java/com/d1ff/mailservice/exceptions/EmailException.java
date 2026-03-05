package com.d1ff.mailservice.exceptions;

public class EmailException extends RuntimeException{
    public EmailException(String message){
        super(message);
    }
}
