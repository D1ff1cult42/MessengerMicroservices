package com.d1ff.mailservice.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EmailException.class)
    public ResponseEntity<String> handleEmailException(EmailException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGlobalException(Exception ex){
        return ResponseEntity.internalServerError().body("Ошибка на стороне сервера. " +
                "Сообщите в поддержку о ошибке!");
    }

}
