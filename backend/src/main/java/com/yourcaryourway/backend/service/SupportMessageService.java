package com.yourcaryourway.backend.service;

import com.yourcaryourway.backend.dto.support.SupportMessageDTO;
import com.yourcaryourway.backend.mapper.SupportMessageMapper;
import com.yourcaryourway.backend.model.SupportMessage;
import com.yourcaryourway.backend.repository.SupportMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for retrieving support conversation messages.
 */
@Service
public class SupportMessageService {

    private final SupportMessageRepository supportMessageRepository;
    private final SupportMessageMapper supportMessageMapper;

    public SupportMessageService(SupportMessageRepository supportMessageRepository,
                                 SupportMessageMapper supportMessageMapper) {
        this.supportMessageRepository = supportMessageRepository;
        this.supportMessageMapper = supportMessageMapper;
    }

    @Transactional(readOnly = true)
    public List<SupportMessageDTO> getMessagesByConversationId(Long conversationId) {
        // fetch all messages for the given support conversation
        List<SupportMessage> messages = supportMessageRepository
                .findBySupportConversationId(conversationId);
        return supportMessageMapper.toSupportMessageList(messages);
    }

}