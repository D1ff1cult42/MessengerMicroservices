package org.d1ff.messageservice.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.d1ff.dto.response.ErrorResponse;
import org.d1ff.messageservice.exceptions.minio.FailedToUploadMinio;
import org.d1ff.messageservice.exceptions.minio.UnknownFileExtensionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
   @ExceptionHandler(AccessDeniedException.class) 
   public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex,
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

   @ExceptionHandler(MessageNotFound.class)
   public ResponseEntity<ErrorResponse> handleMessageNotFoundException(MessageNotFound ex,
                                                                   HttpServletRequest request) {
       ErrorResponse errorResponse = ErrorResponse.builder()
               .message(ex.getMessage())
               .error("Message Not Found")
               .status(404)
               .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
               .path(request.getRequestURI())
               .build();
       return ResponseEntity.status(404).body(errorResponse);
   }

   @ExceptionHandler(FailedToUploadMinio.class)
   public ResponseEntity<ErrorResponse> handleFailedToUploadMinioException(FailedToUploadMinio ex,
                                                                     HttpServletRequest request) {
      ErrorResponse errorResponse = ErrorResponse.builder()
              .message(ex.getMessage())
              .error("Failed to upload file to Minio")
              .status(500)
              .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
              .path(request.getRequestURI())
              .build();
         return ResponseEntity.status(500).body(errorResponse);
   }

   @ExceptionHandler(UnknownFileExtensionException.class)
   public ResponseEntity<ErrorResponse> handleUnknownFileExtensionException(UnknownFileExtensionException ex,
                                                                     HttpServletRequest request) {
      ErrorResponse errorResponse = ErrorResponse.builder()
              .message(ex.getMessage())
              .error("Unknown file extension")
              .status(400)
              .timestamp(new java.sql.Timestamp(System.currentTimeMillis()))
              .path(request.getRequestURI())
              .build();
         return ResponseEntity.status(400).body(errorResponse);
   }

   @ExceptionHandler(MethodArgumentNotValidException.class)
   public ResponseEntity<Map<String, String>> handleValidationErrors(
           MethodArgumentNotValidException ex) {
      Map<String, String> errors = new HashMap<>();
      ex.getBindingResult().getAllErrors().forEach(error -> {
         String fieldName = ((FieldError) error).getField();
         String errorMessage = error.getDefaultMessage();
         errors.put(fieldName, errorMessage);
      });
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
   }

   @ExceptionHandler(Exception.class)
   public ResponseEntity<ErrorResponse> handleGenericException(
           Exception ex,
           HttpServletRequest request) {
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
