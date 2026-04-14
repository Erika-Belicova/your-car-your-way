package com.yourcaryourway.backend.service.chat;

import com.yourcaryourway.backend.enumeration.ChatTimeoutNotification;
import com.yourcaryourway.backend.enumeration.ConversationStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

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
    private final Map<String, ScheduledFuture<?>> scheduledTasks = new ConcurrentHashMap<>();

    public ChatTimeoutScheduler(TaskScheduler taskScheduler,
                                ChatTimeoutService chatTimeoutService,
                                ChatNotificationService chatNotificationService) {
        this.taskScheduler = taskScheduler;
        this.chatTimeoutService = chatTimeoutService;
        this.chatNotificationService = chatNotificationService;
    }

    // schedule timeout checks when a conversation is created (OPEN status)
    public void scheduleOpenTimeouts(UUID chatSessionId) {
        // notify user that they are waiting for an agent to join
        chatNotificationService.broadcastSystemNotification(
                chatSessionId, ChatTimeoutNotification.WAITING_FOR_AGENT.getMessage());

        // 5 min - notify user that agents are busy
        taskScheduler.schedule(() -> chatTimeoutService.handleOpenFiveMinuteTimeout(chatSessionId),
                Instant.now().plus(Duration.ofMinutes(5)));

        // 15 min - auto-close conversation if status is still OPEN and no response from support agent
        taskScheduler.schedule(() -> chatTimeoutService.handleOpenFifteenMinuteTimeout(chatSessionId),
                Instant.now().plus(Duration.ofMinutes(15)));
    }

    // schedule timeout checks after a conversation becomes ACTIVE
    public void scheduleActiveTimeouts(UUID chatSessionId) {
        // schedule agent inactivity check - triggered when user sends a message
        scheduleAgentInactivityTimeout(chatSessionId);
        // schedule user inactivity check - triggered when agent sends a message
        // temporarily disabled - agent is responsible for closing the conversation
        // scheduleUserInactivityTimeout(chatSessionId);
    }

    // 5 min - notify user that agents are busy
    public void scheduleAgentInactivityTimeout(UUID chatSessionId) {
        // cancel existing task if any
        cancelTask(chatSessionId + "_agent");
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> chatTimeoutService.handleAgentInactivityTimeout(chatSessionId),
                Instant.now().plus(Duration.ofMinutes(5)));
        scheduledTasks.put(chatSessionId + "_agent", future);
    }

    // 15 min - check if user has responded, if not auto-close conversation due to inactivity
    public void scheduleUserInactivityTimeout(UUID chatSessionId) {
        cancelTask(chatSessionId + "_user");
        ScheduledFuture<?> future = taskScheduler.schedule(
                () -> chatTimeoutService.handleUserInactivityTimeout(chatSessionId),
                Instant.now().plus(Duration.ofMinutes(15)));
        scheduledTasks.put(chatSessionId + "_user", future);
    }

    private void cancelTask(String key) {
        ScheduledFuture<?> existing = scheduledTasks.get(key);
        if (existing != null) {
            existing.cancel(false); // false = don't interrupt if running
            scheduledTasks.remove(key);
        }
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