package com.d1ff.chatservice.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import com.d1ff.dto.response.ErrorResponse;
import com.d1ff.exceptions.minio.FailedToUploadMinio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccessDenied.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDenied ex,
                                                                     HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Access Denied")
                .status(403)
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(403).body(errorResponse);
    }

    @ExceptionHandler(ChatNotFound.class)
    public ResponseEntity<ErrorResponse> handleChatNotFoundException(ChatNotFound ex,
                                                                        HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Chat Not Found")
                .status(404)
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFound ex,
                                                                     HttpServletRequest request){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("User Not Found")
                .status(404)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(ParticipantNotFound.class)
    public ResponseEntity<ErrorResponse> handleParticipantNotFound(ParticipantNotFound ex,
                                                                   HttpServletRequest request){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Participant Not Found")
                .status(404)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(404).body(errorResponse);
    }

    @ExceptionHandler(ParticipantAlreadyInChat.class)
    public ResponseEntity<ErrorResponse> handleParticipantAlreadyExists(ParticipantAlreadyInChat ex,
                                                                        HttpServletRequest request){
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Participant Already In Chat")
                .status(409)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(409).body(errorResponse);
    }

    @ExceptionHandler(FailedToUploadMinio.class)
    public ResponseEntity<ErrorResponse> handleFailedToUploadMinioException(FailedToUploadMinio ex,
                                                                            HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(ex.getMessage())
                .error("Failed to upload file")
                .status(502)
                .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(502).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .error("Validation Error")
                .status(400)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindErrors(
            BindException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        ErrorResponse errorResponse = ErrorResponse.builder()
                .message(message)
                .error("Validation Error")
                .status(400)
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse error = ErrorResponse.builder()
                .message("An unexpected error occurred")
                .error(ex.getClass().getSimpleName())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(new Timestamp(System.currentTimeMillis()))
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}