package com.yourcaryourway.backend.service;

import com.yourcaryourway.backend.dto.support.conversation.SupportConversationDetailDTO;
import com.yourcaryourway.backend.dto.support.conversation.SupportConversationRequestDTO;
import com.yourcaryourway.backend.dto.support.conversation.SupportConversationResponseDTO;
import com.yourcaryourway.backend.enumeration.ConversationStatus;
import com.yourcaryourway.backend.exception.ConversationNotFoundException;
import com.yourcaryourway.backend.exception.UserNotFoundException;
import com.yourcaryourway.backend.mapper.SupportConversationMapper;
import com.yourcaryourway.backend.model.SupportConversation;
import com.yourcaryourway.backend.model.User;
import com.yourcaryourway.backend.repository.SupportConversationRepository;
import com.yourcaryourway.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing support conversations.
 * Handles creation, retrieval and status updates of support conversations.
 */
@Service
public class SupportConversationService {

    private final SupportConversationRepository supportConversationRepository;
    private final UserRepository userRepository;
    private final SupportConversationMapper supportConversationMapper;

    public SupportConversationService(SupportConversationRepository supportConversationRepository,
                                      UserRepository userRepository,
                                      SupportConversationMapper supportConversationMapper) {
        this.supportConversationRepository = supportConversationRepository;
        this.userRepository = userRepository;
        this.supportConversationMapper = supportConversationMapper;
    }

    @Transactional
    public SupportConversationResponseDTO createConversation(String email, SupportConversationRequestDTO requestDTO) {
        // fetch the authenticated user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // map request DTO to entity and set user
        SupportConversation conversation = supportConversationMapper.toSupportConversationEntity(requestDTO);
        conversation.setUser(user);
        // only assign a chat session ID if this is a live chat conversation
        if (Boolean.TRUE.equals(requestDTO.getIsChatSession())) {
            conversation.setChatSessionId(UUID.randomUUID());
        }

        SupportConversation saved = supportConversationRepository.save(conversation);
        return supportConversationMapper.toSupportConversationResponseDTO(saved);
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

    @Transactional
    public SupportConversationResponseDTO updateConversationStatus(Long id, ConversationStatus status) {
        // fetch conversation or throw if not found
        SupportConversation conversation = supportConversationRepository.findById(id)
                .orElseThrow(() -> new ConversationNotFoundException("Support conversation not found"));

        // update the status
        conversation.setStatus(status);
        SupportConversation saved = supportConversationRepository.save(conversation);
        return supportConversationMapper.toSupportConversationResponseDTO(saved);
    }

}