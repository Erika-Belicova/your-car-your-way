package com.yourcaryourway.backend.service.support;

import com.yourcaryourway.backend.dto.support.conversation.SupportConversationDetailDTO;
import com.yourcaryourway.backend.dto.support.conversation.SupportConversationRequestDTO;
import com.yourcaryourway.backend.dto.support.conversation.SupportConversationResponseDTO;
import com.yourcaryourway.backend.enumeration.ConversationStatus;
import com.yourcaryourway.backend.enumeration.SenderType;
import com.yourcaryourway.backend.exception.ConversationNotFoundException;
import com.yourcaryourway.backend.exception.UserNotFoundException;
import com.yourcaryourway.backend.mapper.SupportConversationMapper;
import com.yourcaryourway.backend.model.SupportConversation;
import com.yourcaryourway.backend.model.SupportMessage;
import com.yourcaryourway.backend.model.User;
import com.yourcaryourway.backend.repository.SupportConversationRepository;
import com.yourcaryourway.backend.repository.SupportMessageRepository;
import com.yourcaryourway.backend.repository.UserRepository;
import com.yourcaryourway.backend.service.chat.ChatTimeoutScheduler;
import com.yourcaryourway.backend.service.chat.ChatNotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing support conversations.
 * Handles creation, retrieval and status updates of support conversations.
 * Triggers WebSocket notifications and timeout scheduling on status changes.
 */
@Service
public class SupportConversationService {

    private final SupportConversationRepository supportConversationRepository;
    private final UserRepository userRepository;
    private final SupportMessageRepository supportMessageRepository;
    private final SupportConversationMapper supportConversationMapper;
    private final ChatNotificationService chatNotificationService;
    private final ChatTimeoutScheduler chatTimeoutScheduler;

    public SupportConversationService(SupportConversationRepository supportConversationRepository,
                                      UserRepository userRepository,
                                      SupportMessageRepository supportMessageRepository,
                                      SupportConversationMapper supportConversationMapper,
                                      ChatNotificationService chatNotificationService,
                                      ChatTimeoutScheduler chatTimeoutScheduler) {
        this.supportConversationRepository = supportConversationRepository;
        this.userRepository = userRepository;
        this.supportMessageRepository = supportMessageRepository;
        this.supportConversationMapper = supportConversationMapper;
        this.chatNotificationService = chatNotificationService;
        this.chatTimeoutScheduler = chatTimeoutScheduler;
    }

    @Transactional
    public SupportConversationResponseDTO createConversation(String email,
                                                             SupportConversationRequestDTO requestDTO) {
        // fetch the authenticated user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // map request DTO to entity and set user and chat session ID
        SupportConversation conversation = supportConversationMapper.toSupportConversationEntity(requestDTO);
        conversation.setUser(user);
        conversation.setChatSessionId(UUID.randomUUID());
        SupportConversation saved = supportConversationRepository.save(conversation);

        // save initial message, schedule timeouts and notify user
        initializeConversation(saved, requestDTO.getMessageContent());
        return supportConversationMapper.toSupportConversationResponseDTO(saved);
    }

    // save initial message, schedule timeouts and notify user
    private void initializeConversation(SupportConversation conversation, String messageContent) {
        saveInitialMessage(conversation, messageContent);
        // schedule timeout checks for the new conversation
        chatTimeoutScheduler.scheduleOpenTimeouts(conversation.getChatSessionId());
        // notify user that the conversation is open and waiting for an agent
        chatNotificationService.broadcastSystemNotification(conversation.getChatSessionId(),
                "Waiting for a support agent to join the conversation.");
    }

    // create and save the initial message for a new conversation
    private void saveInitialMessage(SupportConversation conversation, String content) {
        SupportMessage initialMessage = new SupportMessage();
        initialMessage.setSupportConversation(conversation);
        initialMessage.setContent(content);
        initialMessage.setSenderType(SenderType.USER); // first message is always from the user
        supportMessageRepository.save(initialMessage);
    }

    @Transactional(readOnly = true)
    public List<SupportConversationResponseDTO> getConversationsOfUser(String email) {
        // fetch the authenticated user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // fetch all conversations of the user
        List<SupportConversation> conversations = supportConversationRepository.findByUserId(user.getId());
        return supportConversationMapper.toSupportConversationList(conversations);
    }

    @Transactional(readOnly = true)
    public List<SupportConversationResponseDTO> getAllConversations(ConversationStatus status) {
        // fetch all conversations (can be filtered by status)
        List<SupportConversation> conversations = status != null
                ? supportConversationRepository.findByStatus(status)
                : supportConversationRepository.findAll();
        return supportConversationMapper.toSupportConversationList(conversations);
    }

    @Transactional(readOnly = true)
    public SupportConversationDetailDTO getConversationById(Long id) {
        // fetch conversation or throw if not found
        SupportConversation conversation = supportConversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException("Support conversation not found"));
        return supportConversationMapper.toSupportConversationDetailDTO(conversation);
    }

    // update conversation status via REST - used for manual dropdown status changes
    @Transactional
    public SupportConversationResponseDTO updateConversationStatus(Long id, ConversationStatus status,
                                                                   Authentication authentication) {
        // fetch conversation or throw if not found
        SupportConversation conversation = supportConversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException("Support conversation not found"));

        // capture previous status before updating
        ConversationStatus previousStatus = conversation.getStatus();
        conversation.setStatus(status);
        SupportConversation saved = supportConversationRepository.save(conversation);

        // handle notifications and timeout scheduling after a status update
        chatTimeoutScheduler.handlePostStatusUpdate(conversation.getChatSessionId(),
                previousStatus, status, authentication);
        return supportConversationMapper.toSupportConversationResponseDTO(saved);
    }

}