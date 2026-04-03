package com.yourcaryourway.backend.controller;

import com.yourcaryourway.backend.dto.authentication.LoginRequestDTO;
import com.yourcaryourway.backend.dto.authentication.RefreshTokenRequestDTO;
import com.yourcaryourway.backend.response.AuthResponse;
import com.yourcaryourway.backend.service.authentication.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user authentication.
 * Login endpoint authenticates a user by email and password,
 * returning JWT access and refresh tokens.
 * Refresh endpoint validates a refresh token and returns a new access token.
 */
@Tag(name = "Authentication", description = "Endpoint for user authentication")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "User login", description = "Authenticates user and returns access and refresh tokens.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User logged in successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials or unauthorized")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }

    @Operation(summary = "Refresh access token", description = "Validates the refresh token and returns a new access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenRequestDTO refreshTokenRequest) {
        return ResponseEntity.ok(authService.refresh(refreshTokenRequest));
    }

}
