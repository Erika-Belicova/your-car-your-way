package com.yourcaryourway.backend.service.chat;

import com.yourcaryourway.backend.dto.websocket.ChatNotificationDTO;
import com.yourcaryourway.backend.enumeration.ChatTimeoutNotification;
import com.yourcaryourway.backend.enumeration.ConversationStatus;
import com.yourcaryourway.backend.repository.SupportConversationRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service responsible for building and broadcasting WebSocket notifications
 * to conversation participants when the conversation status changes.
 */
@Service
public class ChatNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final SupportConversationRepository supportConversationRepository;

    public ChatNotificationService(SimpMessagingTemplate messagingTemplate,
                                   SupportConversationRepository supportConversationRepository) {
        this.messagingTemplate = messagingTemplate;
        this.supportConversationRepository = supportConversationRepository;
    }

    // build and broadcast a status change notification to all conversation participants
    public void broadcastStatusNotification(UUID chatSessionId, ConversationStatus previousStatus,
                                            ConversationStatus newStatus, String customMessage) {
        ChatNotificationDTO notification = new ChatNotificationDTO();
        notification.setChatSessionId(chatSessionId);
        notification.setStatus(newStatus);

        // include the latest updated timestamp in the notification
        supportConversationRepository.findByChatSessionId(chatSessionId)
                .ifPresent(conversation -> notification.setUpdatedAt(conversation.getUpdatedAt()));

        notification.setNotificationMessage(
                customMessage != null
                        // if custom notification message is present, show it
                        ? customMessage
                        // if not, determine which notification message to display
                        : determineNotificationMessage(previousStatus, newStatus));
        messagingTemplate.convertAndSend("/topic/chat/" + chatSessionId, notification);
    }

    // determine notification message based on previous status and new status
    private String determineNotificationMessage(ConversationStatus previousStatus,
                                                ConversationStatus newStatus) {
        return switch (newStatus) {
            case ACTIVE -> determineActiveMessage(previousStatus);
            case WAITING -> determineWaitingMessage(previousStatus);
            case CLOSED -> "The support agent has ended the conversation.";
            default -> "The conversation status has been updated.";
        };
    }

    // determine the appropriate message when conversation becomes active
    private String determineActiveMessage(ConversationStatus previousStatus) {
        return switch (previousStatus) {
            case WAITING -> "The support agent has resumed the conversation.";
            case CLOSED -> "The support agent has reopened the conversation.";
            default -> "A support agent has joined the conversation.";
        };
    }

    // determine the appropriate message when conversation changes to WAITING
    private String determineWaitingMessage(ConversationStatus previousStatus) {
        return switch (previousStatus) {
            case OPEN -> ChatTimeoutNotification.AGENTS_BUSY.getMessage();
            case CLOSED -> "The conversation status has been updated. " +
                    "A support agent will respond as soon as possible.";
            default -> ChatTimeoutNotification.AGENT_VERIFYING.getMessage();
        };
    }

    // broadcast a system notification to all participants without a status change
    public void broadcastSystemNotification(UUID chatSessionId, String notificationMessage) {
        ChatNotificationDTO notification = new ChatNotificationDTO();
        notification.setChatSessionId(chatSessionId);
        notification.setNotificationMessage(notificationMessage);
        messagingTemplate.convertAndSend("/topic/chat/" + chatSessionId, notification);
    }

}