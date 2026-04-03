package com.yourcaryourway.backend.service;

import com.yourcaryourway.backend.enumeration.ConversationStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Scheduler responsible for scheduling one-time timeout checks for chat sessions.
 * Handles timeout scheduling for OPEN, ACTIVE and WAITING conversations
 * and broadcasts notifications after status changes.
 */
@Service
public class ChatTimeoutScheduler {

    private final TaskScheduler taskScheduler;
    private final ChatTimeoutService chatTimeoutService;
    private final ChatNotificationService chatNotificationService;

    public ChatTimeoutScheduler(TaskScheduler taskScheduler,
                                ChatTimeoutService chatTimeoutService,
                                ChatNotificationService chatNotificationService) {
        this.taskScheduler = taskScheduler;
        this.chatTimeoutService = chatTimeoutService;
        this.chatNotificationService = chatNotificationService;
    }

    // schedule timeout checks when a conversation is created (OPEN status)
    public void scheduleOpenTimeouts(UUID chatSessionId) {
        // 5 min - notify user that agents are busy
        taskScheduler.schedule(() -> chatTimeoutService.handleOpenFiveMinuteTimeout(chatSessionId),
                Instant.now().plus(Duration.ofMinutes(5)));

        // 15 min - auto-close conversation if status is still OPEN and no response from support agent
        taskScheduler.schedule(() -> chatTimeoutService.handleOpenFifteenMinuteTimeout(chatSessionId),
                Instant.now().plus(Duration.ofMinutes(15)));
    }

    // schedule timeout checks after a conversation becomes ACTIVE
    public void scheduleActiveTimeouts(UUID chatSessionId) {
        // 5 min - check if agent has responded, if not switch conversation status to WAITING
        taskScheduler.schedule(() -> chatTimeoutService.handleAgentInactivityTimeout(chatSessionId),
                Instant.now().plus(Duration.ofMinutes(5)));

        // 15 min - check if user has responded, if not auto-close conversation due to inactivity
        taskScheduler.schedule(() -> chatTimeoutService.handleUserInactivityTimeout(chatSessionId),
                Instant.now().plus(Duration.ofMinutes(15)));
    }

    // schedule waiting timeout when conversation is paused
    public void scheduleWaitingTimeout(UUID chatSessionId) {
        taskScheduler.schedule(() -> chatTimeoutService.handleWaitingTimeout(chatSessionId),
                Instant.now().plus(Duration.ofMinutes(15)));
    }

    // schedule timeout checks and broadcast notification after a status update
    public void handlePostStatusUpdate(UUID chatSessionId, ConversationStatus previousStatus,
                                       ConversationStatus newStatus, Authentication authentication) {
        if (newStatus == ConversationStatus.ACTIVE) {
            scheduleActiveTimeouts(chatSessionId);
        } else if (newStatus == ConversationStatus.WAITING) {
            scheduleWaitingTimeout(chatSessionId);
        }
        chatNotificationService.broadcastStatusNotification(
                chatSessionId, previousStatus, newStatus, authentication);
    }

}