package com.yourcaryourway.backend.service;

import com.yourcaryourway.backend.dto.websocket.ChatNotificationDTO;
import com.yourcaryourway.backend.enumeration.ConversationStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for building and broadcasting WebSocket notifications
 * to conversation participants when the conversation status changes.
 */
@Service
public class ChatNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatNotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // build and broadcast a status change notification to all conversation participants
    public void broadcastStatusNotification(UUID chatSessionId, ConversationStatus previousStatus,
                                            ConversationStatus newStatus, Authentication authentication) {
        ChatNotificationDTO notification = new ChatNotificationDTO();
        notification.setChatSessionId(chatSessionId);
        notification.setStatus(newStatus);
        notification.setNotificationMessage(
                // determine which notification message to display
                determineNotificationMessage(previousStatus, newStatus, authentication));
        messagingTemplate.convertAndSend("/topic/chat/" + chatSessionId, notification);
    }

    // determine notification message based on previous status, new status and who triggered it
    private String determineNotificationMessage(ConversationStatus previousStatus,
                                                ConversationStatus newStatus,
                                                Authentication authentication) {
        return switch (newStatus) {
            case ACTIVE -> determineActiveMessage(previousStatus);
            case WAITING -> "The support agent is verifying information, they will be with you shortly";
            case CLOSED -> (isSupportAgent(authentication) ? "The support agent" : "The user")
                    + " has ended the conversation";
            default -> "The conversation status has been updated";
        };
    }

    // determine the appropriate message when conversation becomes active
    private String determineActiveMessage(ConversationStatus previousStatus) {
        return switch (previousStatus) {
            case WAITING -> "The support agent has resumed the conversation";
            case CLOSED -> "The support agent has reopened the conversation";
            default -> "A support agent has joined the conversation";
        };
    }

    // check if the authenticated user is a support agent
    private boolean isSupportAgent(Authentication authentication) {
        return authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_SUPPORT_AGENT"));
    }

}