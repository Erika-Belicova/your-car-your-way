package com.yourcaryourway.backend.service;

import com.yourcaryourway.backend.dto.websocket.ChatMessageRequestDTO;
import com.yourcaryourway.backend.dto.websocket.ChatMessageResponseDTO;
import com.yourcaryourway.backend.enumeration.SenderType;
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
 * Service for handling real-time chat messages over WebSocket.
 * Persists messages to the database and broadcasts them to conversation participants.
 */
@Service
public class ChatService {

    private final SupportConversationRepository supportConversationRepository;
    private final SupportMessageRepository supportMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final SupportMessageMapper supportMessageMapper;

    public ChatService(SupportConversationRepository supportConversationRepository,
                       SupportMessageRepository supportMessageRepository,
                       SimpMessagingTemplate messagingTemplate,
                       SupportMessageMapper supportMessageMapper) {
        this.supportConversationRepository = supportConversationRepository;
        this.supportMessageRepository = supportMessageRepository;
        this.messagingTemplate = messagingTemplate;
        this.supportMessageMapper = supportMessageMapper;
    }

    @Transactional
    public void sendMessage(ChatMessageRequestDTO requestDTO, Authentication authentication) {
        SupportMessage saved = persistMessage(
                fetchConversation(requestDTO.getChatSessionId()),
                requestDTO.getContent(),
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

}