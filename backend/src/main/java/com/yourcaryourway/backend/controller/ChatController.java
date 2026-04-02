package com.yourcaryourway.backend.controller;

import com.yourcaryourway.backend.dto.websocket.ChatMessageRequestDTO;
import com.yourcaryourway.backend.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

/**
 * Controller for handling real-time chat messages over WebSocket.
 * Receives messages from clients and forwards to ChatService for processing and broadcasting.
 */
@Tag(name = "Chat", description = "WebSocket endpoint for real-time chat messaging")
@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "Send a chat message",
            description = "Receives a chat message from a client and broadcasts it to all conversation participants.")
    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessageRequestDTO requestDTO, Authentication authentication) {
        chatService.sendMessage(requestDTO, authentication);
    }

}
