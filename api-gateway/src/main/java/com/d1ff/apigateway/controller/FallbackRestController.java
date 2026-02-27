package com.d1ff.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/fallback")
public class FallbackRestController {
    @GetMapping("/auth-service")
    public Mono<ResponseEntity<String>> authServiceFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Auth service is currently unavailable. Please try again later."));
    }

    @GetMapping("/message-service")
    public Mono<ResponseEntity<String>> messageServiceFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Message service is currently unavailable. Please try again later."));
    }

    @GetMapping("/user-service")
    public Mono<ResponseEntity<String>> userServiceFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User service is currently unavailable. Please try again later."));
    }

    @GetMapping("/chat-service")
    public Mono<ResponseEntity<String>> chatServiceFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Chat service is currently unavailable. Please try again later."));
    }

    @GetMapping("/account-service")
    public Mono<ResponseEntity<String>> accountServiceFallback() {
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Account service is currently unavailable. Please try again later."));
    }
}

