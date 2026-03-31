package com.yourcaryourway.backend.mapper;

import com.yourcaryourway.backend.dto.support.conversation.SupportConversationDetailDTO;
import com.yourcaryourway.backend.dto.support.conversation.SupportConversationRequestDTO;
import com.yourcaryourway.backend.dto.support.conversation.SupportConversationResponseDTO;
import com.yourcaryourway.backend.model.SupportConversation;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between SupportConversation entity and its DTOs.
 */
@Component
public class SupportConversationMapper {

    private final ModelMapper modelMapper;
    private final SupportMessageMapper supportMessageMapper;

    public SupportConversationMapper(ModelMapper modelMapper, SupportMessageMapper supportMessageMapper) {
        this.modelMapper = modelMapper;
        this.supportMessageMapper = supportMessageMapper;
    }

    // convert a SupportConversation entity to a SupportConversationResponseDTO
    public SupportConversationResponseDTO toSupportConversationResponseDTO(SupportConversation conversation) {
        return modelMapper.map(conversation, SupportConversationResponseDTO.class);
    }

    // convert a SupportConversation entity to a SupportConversationDetailDTO with messages
    public SupportConversationDetailDTO toSupportConversationDetailDTO(SupportConversation conversation) {
        SupportConversationDetailDTO detailDTO = modelMapper.map(conversation, SupportConversationDetailDTO.class);
        if (conversation.getSupportMessages() != null) {
            // map and set the list of support messages
            detailDTO.setMessages(supportMessageMapper.toSupportMessageList(conversation.getSupportMessages()));
        }
        return detailDTO;
    }

    // convert a list of SupportConversation entities to a list of SupportConversationResponseDTOs
    public List<SupportConversationResponseDTO> toSupportConversationResponseDTOList(List<SupportConversation> conversations) {
        return conversations.stream()
                .map(this::toSupportConversationResponseDTO)
                .collect(Collectors.toList());
    }

    // convert a SupportConversationRequestDTO to a SupportConversation entity
    public SupportConversation toSupportConversationEntity(SupportConversationRequestDTO requestDTO) {
        return modelMapper.map(requestDTO, SupportConversation.class);
    }

}