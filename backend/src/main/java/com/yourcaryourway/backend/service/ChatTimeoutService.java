package com.yourcaryourway.backend.service;

import com.yourcaryourway.backend.enumeration.ChatTimeoutNotification;
import com.yourcaryourway.backend.enumeration.ConversationStatus;
import com.yourcaryourway.backend.enumeration.SenderType;
import com.yourcaryourway.backend.model.SupportConversation;
import com.yourcaryourway.backend.repository.SupportConversationRepository;
import com.yourcaryourway.backend.repository.SupportMessageRepository;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service responsible for handling chat session timeout logic.
 * Manages inactivity checks for OPEN, ACTIVE and WAITING conversations.
 */
@Service
public class ChatTimeoutService {

    private final TaskScheduler taskScheduler;
    private final SupportConversationRepository supportConversationRepository;
    private final SupportMessageRepository supportMessageRepository;
    private final ChatNotificationService chatNotificationService;

    public ChatTimeoutService(TaskScheduler taskScheduler,
                              SupportConversationRepository supportConversationRepository,
                              SupportMessageRepository supportMessageRepository,
                              ChatNotificationService chatNotificationService) {
        this.taskScheduler = taskScheduler;
        this.supportConversationRepository = supportConversationRepository;
        this.supportMessageRepository = supportMessageRepository;
        this.chatNotificationService = chatNotificationService;
    }

    // notify user that agents are busy after 5 minutes with no agent joining
    @Transactional
    void handleOpenFiveMinuteTimeout(UUID chatSessionId) {
        findConversationWithStatus(chatSessionId, ConversationStatus.OPEN)
                .ifPresent(conversation -> notifyChat(chatSessionId,
                        ChatTimeoutNotification.AGENTS_BUSY));
    }

    // auto-close conversation after 15 minutes with no agent joining
    @Transactional
    void handleOpenFifteenMinuteTimeout(UUID chatSessionId) {
        findConversationWithStatus(chatSessionId, ConversationStatus.OPEN)
                .ifPresent(conversation -> {
                    conversation.setStatus(ConversationStatus.CLOSED);
                    supportConversationRepository.save(conversation);
                    notifyChat(chatSessionId, ChatTimeoutNotification.ALL_AGENTS_OCCUPIED);
                });
    }

    // switch to WAITING if agent has not responded within 5 minutes
    @Transactional
    void handleAgentInactivityTimeout(UUID chatSessionId) {
        findConversationWithStatus(chatSessionId, ConversationStatus.ACTIVE)
                .ifPresent(conversation -> checkAgentInactivity(conversation, chatSessionId));
    }

    // auto-close if user has not responded within 15 minutes
    @Transactional
    void handleUserInactivityTimeout(UUID chatSessionId) {
        findConversationWithStatus(chatSessionId, ConversationStatus.ACTIVE)
                .ifPresent(conversation -> checkUserInactivity(conversation, chatSessionId));
    }

    // check if the last message was from the user and switch to WAITING if so
    private void checkAgentInactivity(SupportConversation conversation, UUID chatSessionId) {
        supportMessageRepository
                // fetch the most recent message in a conversation
                .findFirstBySupportConversationIdOrderBySentAtDesc(conversation.getId())
                .filter(message -> message.getSenderType() == SenderType.USER)
                .ifPresent(message -> pauseConversation(conversation, chatSessionId));
    }

    // check if the last message was from the agent and close the conversation if so
    private void checkUserInactivity(SupportConversation conversation, UUID chatSessionId) {
        supportMessageRepository
                // fetch the most recent message in a conversation
                .findFirstBySupportConversationIdOrderBySentAtDesc(conversation.getId())
                .filter(message -> message.getSenderType() == SenderType.SUPPORT_AGENT)
                .ifPresent(message -> closeConversation(conversation, chatSessionId,
                        ChatTimeoutNotification.CHAT_TIMED_OUT));
    }

    // pause the conversation and schedule a waiting timeout
    private void pauseConversation(SupportConversation conversation, UUID chatSessionId) {
        conversation.setStatus(ConversationStatus.WAITING);
        supportConversationRepository.save(conversation);
        scheduleWaitingTimeout(chatSessionId);
        notifyChat(chatSessionId, ChatTimeoutNotification.AGENT_VERIFYING);
    }

    // auto-close if agent has not resumed within 15 minutes of pausing
    @Transactional
    void handleWaitingTimeout(UUID chatSessionId) {
        findConversationWithStatus(chatSessionId, ConversationStatus.WAITING)
                .ifPresent(conversation -> closeConversation(conversation, chatSessionId,
                        ChatTimeoutNotification.AGENT_UNABLE));
    }

    // schedule waiting timeout when conversation is paused
    void scheduleWaitingTimeout(UUID chatSessionId) {
        taskScheduler.schedule(() -> handleWaitingTimeout(chatSessionId),
                Instant.now().plus(Duration.ofMinutes(15)));
    }

    // close the conversation and notify participants
    private void closeConversation(SupportConversation conversation, UUID chatSessionId,
                                   ChatTimeoutNotification notification) {
        conversation.setStatus(ConversationStatus.CLOSED);
        supportConversationRepository.save(conversation);
        notifyChat(chatSessionId, notification);
    }

    // fetch conversation by chat session ID and filter by the given status
    private Optional<SupportConversation> findConversationWithStatus(UUID chatSessionId,
                                                                     ConversationStatus status) {
        return supportConversationRepository.findByChatSessionId(chatSessionId)
                .filter(conversation -> conversation.getStatus() == status);
    }

    // notify the participants of the chat via a system notification
    private void notifyChat(UUID chatSessionId, ChatTimeoutNotification notification) {
        chatNotificationService.broadcastSystemNotification(chatSessionId, notification.getMessage());
    }

}