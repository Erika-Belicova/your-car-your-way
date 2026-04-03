package com.yourcaryourway.backend.service.chat;

import com.yourcaryourway.backend.dto.websocket.ChatMessageRequestDTO;
import com.yourcaryourway.backend.dto.websocket.ChatMessageResponseDTO;
import com.yourcaryourway.backend.dto.websocket.ChatStatusUpdateDTO;
import com.yourcaryourway.backend.enumeration.ConversationStatus;
import com.yourcaryourway.backend.enumeration.SenderType;
import com.yourcaryourway.backend.exception.ConversationNotActiveException;
import com.yourcaryourway.backend.exception.ConversationNotFoundException;
import com.yourcaryourway.backend.mapper.SupportMessageMapper;
import com.yourcaryourway.backend.model.SupportConversation;
import com.yourcaryourway.backend.model.SupportMessage;
import com.yourcaryourway.backend.repository.SupportConversationRepository;
import com.yourcaryourway.backend.repository.SupportMessageRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for handling real-time chat messages and conversation status updates over WebSocket.
 * Persists messages to the database, broadcasts messages and status change notifications
 * to conversation participants. Triggers timeout scheduling on status changes.
 */
@Service
public class ChatService {

    private final SupportConversationRepository supportConversationRepository;
    private final SupportMessageRepository supportMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SupportMessageMapper supportMessageMapper;
    private final ChatTimeoutScheduler chatTimeoutScheduler;

    public ChatService(SupportConversationRepository supportConversationRepository,
                       SupportMessageRepository supportMessageRepository,
                       SimpMessagingTemplate messagingTemplate,
                       SupportMessageMapper supportMessageMapper,
                       ChatTimeoutScheduler chatTimeoutScheduler) {
        this.supportConversationRepository = supportConversationRepository;
        this.supportMessageRepository = supportMessageRepository;
        this.messagingTemplate = messagingTemplate;
        this.supportMessageMapper = supportMessageMapper;
        this.chatTimeoutScheduler = chatTimeoutScheduler;
    }

    @Transactional
    public void sendMessage(ChatMessageRequestDTO requestDTO, Authentication authentication) {
        SupportConversation conversation = fetchConversation(requestDTO.getChatSessionId());
        // only allow messages when conversation is active
        if (conversation.getStatus() != ConversationStatus.ACTIVE) {
            throw new ConversationNotActiveException("Messages can only be sent to active conversations");
        }
        SupportMessage saved = persistMessage(conversation, requestDTO.getContent(),
                determineSenderType(authentication));
        broadcastMessage(saved, requestDTO.getChatSessionId());
    }

    // fetch the conversation by chat session ID
    private SupportConversation fetchConversation(UUID chatSessionId) {
        return supportConversationRepository
                .findByChatSessionId(chatSessionId)
                .orElseThrow(() -> new ConversationNotFoundException("Chat session not found"));
    }

    // determine sender type from the authenticated user's role
    private SenderType determineSenderType(Authentication authentication) {
        return authentication.getAuthorities().contains(
                new SimpleGrantedAuthority("ROLE_SUPPORT_AGENT"))
                ? SenderType.SUPPORT_AGENT : SenderType.USER;
    }

    // create and persist the message to the database
    private SupportMessage persistMessage(SupportConversation conversation, String content,
                                          SenderType senderType) {
        SupportMessage message = new SupportMessage();
        message.setSupportConversation(conversation);
        message.setContent(content);
        message.setSenderType(senderType);
        return supportMessageRepository.save(message);
    }

    // build response DTO and broadcast to all subscribers
    private void broadcastMessage(SupportMessage saved, UUID chatSessionId) {
        ChatMessageResponseDTO responseDTO = supportMessageMapper.toChatMessageResponseDTO(saved);
        messagingTemplate.convertAndSend("/topic/chat/" + chatSessionId, responseDTO);
    }

    // update conversation status via WebSocket - used for real-time status changes during active chat
    @Transactional
    public void updateStatus(ChatStatusUpdateDTO statusUpdateDTO, Authentication authentication) {
        // extract data from DTO
        UUID chatSessionId = statusUpdateDTO.getChatSessionId();
        ConversationStatus newStatus = statusUpdateDTO.getStatus();

        // fetch conversation and capture previous status before updating
        SupportConversation conversation = fetchConversation(chatSessionId);
        ConversationStatus previousStatus = conversation.getStatus();
        conversation.setStatus(newStatus);
        supportConversationRepository.save(conversation);

        // handle notifications and timeout scheduling after a status update
        chatTimeoutScheduler.handlePostStatusUpdate(chatSessionId, previousStatus, newStatus, authentication);
    }

}