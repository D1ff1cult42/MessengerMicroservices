package com.d1ff.accountservice.controller;

import com.d1ff.accountservice.dto.request.GetAccountByEmailRequest;
import com.d1ff.accountservice.dto.response.AccountResponse;
import com.d1ff.accountservice.service.interfaces.AccountService;
import com.d1ff.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "OtherAccountRestController", description = "Controller for looking up other users' accounts")
public class OtherAccountRestController {
    private final AccountService accountService;

    @Operation(summary = "Get account by email",
            description = "Retrieve a user's account profile by their email address.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Account retrieved successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AccountResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email address",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "404",
                    description = "Account not found for the given email",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                )
            }
    )
    @GetMapping
    public ResponseEntity<AccountResponse> getAccountByEmail(GetAccountByEmailRequest getAccountByEmailRequest) {
        return ResponseEntity.ok(accountService.getAccountByEmail(getAccountByEmailRequest));
    }
}
