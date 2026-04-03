package com.yourcaryourway.backend.service;

import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Scheduler responsible for scheduling one-time timeout checks for chat sessions.
 * Triggers timeout handlers for OPEN and ACTIVE conversations.
 */
@Service
public class ChatTimeoutScheduler {

    private final TaskScheduler taskScheduler;
    private final ChatTimeoutService chatTimeoutService;

    public ChatTimeoutScheduler(TaskScheduler taskScheduler,
                                ChatTimeoutService chatTimeoutService) {
        this.taskScheduler = taskScheduler;
        this.chatTimeoutService = chatTimeoutService;
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

}