package org.d1ff.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackRestController {
    @GetMapping("/auth-service")
    public ResponseEntity<?> authServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Auth service is currently unavailable. Please try again later.");
    }

    @GetMapping("/message-service")
    public ResponseEntity<?> messageServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Message service is currently unavailable. Please try again later.");
    }

    @GetMapping("/user-service")
    public ResponseEntity<?> userServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User service is currently unavailable. Please try again later.");
    }

    @GetMapping("/chat-service")
    public ResponseEntity<?> chatServiceFallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Chat service is currently unavailable. Please try again later.");
    }
}
