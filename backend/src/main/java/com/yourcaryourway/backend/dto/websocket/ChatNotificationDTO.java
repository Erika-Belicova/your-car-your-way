package com.yourcaryourway.backend.dto.websocket;

import com.yourcaryourway.backend.enumeration.ConversationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO representing a notification broadcast to conversation participants over WebSocket.
 * Sent by the server when the conversation status changes.
 */
@Data
public class ChatNotificationDTO {

    @Schema(description = "UUID of the chat session", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID chatSessionId;

    @Schema(description = "Updated status of the conversation")
    private ConversationStatus status;

    @Schema(description = "Notification message for the participants",
            example = "A support agent has joined the conversation")
    private String notificationMessage;

    @Schema(description = "Updated timestamp of the conversation")
    private OffsetDateTime updatedAt;

}