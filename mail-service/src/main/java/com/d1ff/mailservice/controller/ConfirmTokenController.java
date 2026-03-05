package com.d1ff.mailservice.controller;

import com.d1ff.mailservice.service.interfaces.MailTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/mail")
@Tag(name = "MailController", description = "Controller for email confirmation operations")
public class ConfirmTokenController {

    private final MailTokenService mailTokenService;

    @Operation(summary = "Confirm email",
            description = "Confirm user's email address using a token sent to their email. "
                    + "The user clicks the link in the email and is redirected to this endpoint.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Email confirmed successfully",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(type = "string", example = "Аккаунт успешно подтвержден!")
                    )
                ),
                @ApiResponse(
                    responseCode = "400",
                    description = "Invalid, expired or already used token",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(type = "string", example = "Token expired")
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
    @GetMapping("/confirm")
    public ResponseEntity<String> confirmEmail(
            @Parameter(description = "Confirmation token from the email link", required = true,
                    example = "550e8400-e29b-41d4-a716-446655440000")
            @RequestParam UUID token) {
        log.info("Received email confirmation request with token: {}", token);
        UUID userId = mailTokenService.confirmToken(token);
        log.info("Email confirmed for userId: {}", userId);
        return ResponseEntity.ok("Аккаунт успешно подтвержден!");
    }
}
