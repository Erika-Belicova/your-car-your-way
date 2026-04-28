package com.yourcaryourway.backend.dto.websocket;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * DTO representing a chat message received from a client over WebSocket.
 * Contains the chat session ID and message content provided by the client.
 */
@Data
public class ChatMessageRequestDTO {

    @NotNull(message = "Chat session ID is required")
    @Schema(description = "UUID of the chat session", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID chatSessionId;

    @NotBlank(message = "Message content cannot be blank")
    @Schema(description = "Content of the message", example = "Hello, I need help with my reservation")
    private String content;

}