package com.yourcaryourway.backend.dto.websocket;

import com.yourcaryourway.backend.enumeration.SenderType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO representing a chat message broadcast by the server
 * to conversation participants over WebSocket.
 * The chat session ID, sender type and timestamp are set server-side before broadcasting.
 */
@Data
public class ChatMessageResponseDTO {

    @Schema(description = "UUID of the chat session", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID chatSessionId;

    @Schema(description = "Content of the message", example = "Hello, I need help with my reservation")
    private String content;

    @Schema(description = "Type of sender", example = "USER")
    private SenderType senderType;

    @Schema(description = "Date and time the message was sent")
    private OffsetDateTime sentAt;

}