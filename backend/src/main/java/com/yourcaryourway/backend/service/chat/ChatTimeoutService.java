package com.yourcaryourway.backend.service.chat;

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
import java.time.OffsetDateTime;
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
                .ifPresent(conversation ->
                        chatNotificationService.broadcastSystemNotification(
                                // show notification without status change
                                chatSessionId, ChatTimeoutNotification.AGENTS_BUSY.getMessage()));
    }

    // auto-close conversation after 15 minutes with no agent joining
    @Transactional
    void handleOpenFifteenMinuteTimeout(UUID chatSessionId) {
        findConversationWithStatus(chatSessionId, ConversationStatus.OPEN)
                .ifPresent(conversation -> {
                    conversation.setStatus(ConversationStatus.CLOSED);
                    supportConversationRepository.save(conversation);
                    notifyStatusChange(chatSessionId,
                            ConversationStatus.OPEN, ConversationStatus.CLOSED,
                            ChatTimeoutNotification.ALL_AGENTS_OCCUPIED);
                });
    }

    // switch to WAITING if agent has not responded within 5 minutes
    @Transactional
    void handleAgentInactivityTimeout(UUID chatSessionId) {
        findConversationWithStatus(chatSessionId, ConversationStatus.ACTIVE)
                .ifPresent(conversation -> checkAgentInactivity(conversation, chatSessionId));
    }

    // check if the last message was from the user and switch to WAITING if so
    private void checkAgentInactivity(SupportConversation conversation, UUID chatSessionId) {
        supportMessageRepository
                // fetch the most recent message in a conversation
                .findFirstBySupportConversationIdOrderBySentAtDesc(conversation.getId())

                // check if it was sent by the user
                .filter(message -> message.getSenderType() == SenderType.USER)

                // check if the message is older than 5 minutes
                .filter(message -> message.getSentAt()
                        .isBefore(OffsetDateTime.now().minusMinutes(5)))
                .ifPresent(message -> pauseConversation(conversation, chatSessionId));
    }

    // pause the conversation and schedule a waiting timeout
    private void pauseConversation(SupportConversation conversation, UUID chatSessionId) {
        conversation.setStatus(ConversationStatus.WAITING);
        supportConversationRepository.save(conversation);

        // calling taskScheduler directly to avoid circular dependency
        taskScheduler.schedule(() -> handleWaitingTimeout(chatSessionId),
                Instant.now().plus(Duration.ofMinutes(15)));
        notifyStatusChange(chatSessionId, ConversationStatus.ACTIVE,
                ConversationStatus.WAITING, ChatTimeoutNotification.AGENT_VERIFYING);
    }

    // auto-close if agent has not resumed within 15 minutes of pausing
    @Transactional
    void handleWaitingTimeout(UUID chatSessionId) {
        findConversationWithStatus(chatSessionId, ConversationStatus.WAITING)
                .ifPresent(conversation -> {
                    // change status to CLOSED
                    conversation.setStatus(ConversationStatus.CLOSED);
                    supportConversationRepository.save(conversation);

                    // show notification in chat
                    notifyStatusChange(chatSessionId,
                            ConversationStatus.WAITING, ConversationStatus.CLOSED,
                            ChatTimeoutNotification.AGENT_UNABLE);
                });
    }

    // fetch conversation by chat session ID and filter by the given status
    private Optional<SupportConversation> findConversationWithStatus(UUID chatSessionId,
                                                                     ConversationStatus status) {
        return supportConversationRepository.findByChatSessionId(chatSessionId)
                .filter(conversation -> conversation.getStatus() == status);
    }

    // notify the participants of the chat of a status change via a system notification
    private void notifyStatusChange(UUID chatSessionId, ConversationStatus previousStatus,
                                    ConversationStatus newStatus, ChatTimeoutNotification notification) {
        chatNotificationService.broadcastStatusNotification(
                chatSessionId, previousStatus, newStatus, notification.getMessage());
    }

}