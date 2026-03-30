package com.d1ff.accountservice.controller;

import com.d1ff.accountservice.dto.request.UpdateAccountRequest;
import com.d1ff.accountservice.dto.response.AccountResponse;
import com.d1ff.accountservice.service.interfaces.AccountService;
import com.d1ff.dto.response.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/my_account")
@RequiredArgsConstructor
@Tag(name = "MyAccountRestController", description = "Controller for managing the authenticated user's own account")
public class MyAccountRestController {
    private final AccountService accountService;

    @Operation(summary = "Get my account",
            description = "Retrieve the profile of the currently authenticated user. If the account does not exist yet, it will be created automatically.",
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
                    responseCode = "500",
                    description = "Internal server error",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
                    )
                )
            }
    )
    @PostMapping
    public ResponseEntity<AccountResponse> getMyAccount(
            @Parameter(description = "ID of the authenticated user (forwarded by API Gateway)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Email of the authenticated user (forwarded by API Gateway)", required = true)
            @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(accountService.getAccount(userId, email));
    }

    @Operation(summary = "Delete my account",
            description = "Permanently delete the account of the currently authenticated user.",
            responses = {
                @ApiResponse(
                    responseCode = "204",
                    description = "Account deleted successfully"
                ),
                @ApiResponse(
                    responseCode = "404",
                    description = "Account not found",
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
    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(
            @Parameter(description = "ID of the authenticated user (forwarded by API Gateway)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Email of the authenticated user (forwarded by API Gateway)", required = true)
            @RequestHeader("X-User-Email") String email) {
        accountService.deleteAccount(userId, email);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update my account",
            description = "Update the profile fields (username, description, avatar) of the currently authenticated user. All fields are optional.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Account updated successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AccountResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
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
    @PatchMapping
    public ResponseEntity<AccountResponse> updateAccount(
            @Parameter(description = "ID of the authenticated user (forwarded by API Gateway)", required = true)
            @RequestHeader("X-User-Id") UUID userId,
            @Parameter(description = "Email of the authenticated user (forwarded by API Gateway)", required = true)
            @RequestHeader("X-User-Email") String email,
            @RequestBody @Valid UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(userId, email, request));
    }
}
