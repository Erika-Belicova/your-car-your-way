package com.yourcaryourway.backend.controller;

import com.yourcaryourway.backend.dto.authentication.UserResponseDTO;
import com.yourcaryourway.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user profile operations.
 * Provides an endpoint to retrieve the authenticated user's email and role.
 */
@Tag(name = "User", description = "Endpoint for user profile operations")
@SecurityRequirement(name = "Authorization")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get current user", description = "Returns the authenticated user's email and role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication));
    }

}