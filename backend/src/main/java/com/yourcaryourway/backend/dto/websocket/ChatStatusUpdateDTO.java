package com.yourcaryourway.backend.dto.websocket;

import com.yourcaryourway.backend.enumeration.ConversationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO representing a status update request sent by a client over WebSocket.
 * Used to change the status of a support conversation.
 */
@Data
public class ChatStatusUpdateDTO {

    @NotNull(message = "Chat session ID is required")
    @Schema(description = "UUID of the chat session", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID chatSessionId;

    @NotNull(message = "Status is required")
    @Schema(description = "New status of the conversation", example = "ACTIVE")
    private ConversationStatus status;

}