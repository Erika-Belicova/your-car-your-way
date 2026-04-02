package com.yourcaryourway.backend.mapper;

import com.yourcaryourway.backend.dto.support.message.SupportMessageResponseDTO;
import com.yourcaryourway.backend.dto.websocket.ChatMessageResponseDTO;
import com.yourcaryourway.backend.model.SupportMessage;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between SupportMessage entity and its DTOs.
 * Handles mapping to SupportMessageResponseDTO for REST
 * and ChatMessageResponseDTO for WebSocket broadcasting.
 */
@Component
public class SupportMessageMapper {

    private final ModelMapper modelMapper;

    public SupportMessageMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // convert a SupportMessage entity to a SupportMessageResponseDTO
    public SupportMessageResponseDTO toSupportMessageResponseDTO(SupportMessage supportMessage) {
        return modelMapper.map(supportMessage, SupportMessageResponseDTO.class);
    }

    // convert a list of SupportMessage entities to a list of SupportMessageResponseDTOs
    public List<SupportMessageResponseDTO> toSupportMessageList(List<SupportMessage> supportMessages) {
        return supportMessages.stream()
                .map(this::toSupportMessageResponseDTO)
                .collect(Collectors.toList());
    }

    // convert a SupportMessage entity to a ChatMessageResponseDTO for WebSocket broadcasting
    public ChatMessageResponseDTO toChatMessageResponseDTO(SupportMessage message) {
        ChatMessageResponseDTO responseDTO = modelMapper.map(message, ChatMessageResponseDTO.class);
        // chatSessionId comes from the associated conversation
        responseDTO.setChatSessionId(message.getSupportConversation().getChatSessionId());
        return responseDTO;
    }

}
