package com.yourcaryourway.backend.controller;

import com.yourcaryourway.backend.dto.support.conversation.SupportConversationDetailDTO;
import com.yourcaryourway.backend.dto.support.conversation.SupportConversationRequestDTO;
import com.yourcaryourway.backend.dto.support.conversation.SupportConversationResponseDTO;
import com.yourcaryourway.backend.enumeration.ConversationStatus;
import com.yourcaryourway.backend.service.SupportConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing support conversations.
 * Handles creation, retrieval, status updates and triggers WebSocket notifications.
 * Access is restricted based on user roles.
 */
@Tag(name = "Support Conversations", description = "Endpoints for managing support conversations")
@SecurityRequirement(name = "Authorization")
@RestController
@RequestMapping("/api/support-conversations")
public class SupportConversationController {

    private final SupportConversationService supportConversationService;

    public SupportConversationController(SupportConversationService supportConversationService) {
        this.supportConversationService = supportConversationService;
    }

    @Operation(summary = "Create a support conversation", description = "Creates a new support conversation for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Conversation created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SupportConversationResponseDTO> createConversation(
            @Valid @RequestBody SupportConversationRequestDTO requestDTO,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(supportConversationService.createConversation(authentication.getName(), requestDTO));
    }

    @Operation(summary = "Get conversations of the authenticated user", description = "Returns all support conversations of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversations fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<SupportConversationResponseDTO>> getConversationsOfUser(
            Authentication authentication) {
        return ResponseEntity.ok(supportConversationService.getConversationsOfUser(authentication.getName()));
    }

    @Operation(summary = "Get all support conversations", description = "Returns all support conversations (can be filtered by status). Accessible by support agents only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversations fetched successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPPORT_AGENT')")
    public ResponseEntity<List<SupportConversationResponseDTO>> getAllConversations(
            @RequestParam(required = false) ConversationStatus status) {
        return ResponseEntity.ok(supportConversationService.getAllConversations(status));
    }

    @Operation(summary = "Get a support conversation by ID", description = "Returns a support conversation with its full message history.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conversation fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Conversation not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('SUPPORT_AGENT')")
    public ResponseEntity<SupportConversationDetailDTO> getConversationById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(supportConversationService.getConversationById(id));
    }

    @Operation(summary = "Update conversation status", description = "Updates the status of a support conversation. Accessible by support agents only.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status updated successfully"),
            @ApiResponse(responseCode = "404", description = "Conversation not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('SUPPORT_AGENT')")
    public ResponseEntity<SupportConversationResponseDTO> updateConversationStatus(
            @PathVariable("id") Long id,
            @RequestParam ConversationStatus status, // status passed as query parameter
            Authentication authentication) {
        return ResponseEntity.ok(supportConversationService.updateConversationStatus(id, status, authentication));
    }

}
