package org.d1ff.authservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.d1ff.authservice.dto.response.AuthResponse;
import org.d1ff.authservice.dto.request.LoginRequest;
import org.d1ff.authservice.dto.request.RefreshTokenRequest;
import org.d1ff.authservice.dto.request.RegisterRequest;
import org.d1ff.authservice.dto.response.ErrorResponse;
import org.d1ff.authservice.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "Controller for user authentication operations")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Register a new user",
            description = "Register a new user with the provided email and password.",
            responses = {
                @ApiResponse(
                    responseCode = "201",
                    description = "User registered successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AuthResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "409",
                    description = "User already exists",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
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
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "User login",
            description = "Authenticate a user with email and password.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "User logged in successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AuthResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
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
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Refresh authentication token",
            description = "Refresh the authentication token using a valid refresh token.",
            responses = {
                @ApiResponse(
                    responseCode = "200",
                    description = "Token refreshed successfully",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = AuthResponse.class)
                    )
                ),
                @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or expired refresh token",
                    content = @io.swagger.v3.oas.annotations.media.Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ErrorResponse.class)
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
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request.refreshToken());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logout user",
            description = "Logout the user by invalidating the provided refresh token.",
            responses = {
                @ApiResponse(
                    responseCode = "204",
                    description = "User logged out successfully"
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
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Logout all sessions for a user",
            description = "Logout the user from all sessions by invalidating all refresh tokens associated with the user's email.",
            responses = {
                @ApiResponse(
                    responseCode = "204",
                    description = "All user sessions logged out successfully"
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
    @PostMapping("/logout-all")
    public ResponseEntity<Void> logoutAll(@RequestParam String email) {
        authService.logoutAll(email);
        return ResponseEntity.noContent().build();
    }
}

