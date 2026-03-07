package com.d1ff.mailservice.controller;


import com.d1ff.mailservice.service.interfaces.MailTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class RetryMessageController {

    private final MailTokenService mailTokenService;

    @Operation(summary = "Resend message on email",
            description = "Send new message to confirm user's email address.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Message send successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Сообщение отправлено!")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Failed to send message",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Failed to send message")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "string", example = "Ошибка на стороне сервера. Сообщите в поддержку о ошибке!")
                            )
                    )
            }
    )
    @GetMapping("/retry")
    public ResponseEntity<String> retry(@RequestHeader(name = "X-User-Email") String email,
                                        @RequestHeader(name = "X-User-Id") UUID userId) {
        mailTokenService.sendConfirmationEmail(userId, email);
        return ResponseEntity.ok("Сообщение отправлено!");
    }
}
