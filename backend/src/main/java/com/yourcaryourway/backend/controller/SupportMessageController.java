package com.yourcaryourway.backend.controller;

import com.yourcaryourway.backend.dto.support.message.SupportMessageResponseDTO;
import com.yourcaryourway.backend.service.SupportMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for fetching messages belonging to a support conversation.
 * Accessible by both users and support agents.
 */
@Tag(name = "Support Messages", description = "Endpoint for retrieving support messages")
@SecurityRequirement(name = "Authorization")
@RestController
@RequestMapping("/api/support-conversations")
public class SupportMessageController {

    private final SupportMessageService supportMessageService;

    public SupportMessageController(SupportMessageService supportMessageService) {
        this.supportMessageService = supportMessageService;
    }

    @Operation(summary = "Get messages for a support conversation", description = "Returns all messages for a specific support conversation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Support conversation not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<SupportMessageResponseDTO>> getMessagesByConversationId(@PathVariable("id") Long id) {
        return ResponseEntity.ok(supportMessageService.getMessagesByConversationId(id));
    }

}