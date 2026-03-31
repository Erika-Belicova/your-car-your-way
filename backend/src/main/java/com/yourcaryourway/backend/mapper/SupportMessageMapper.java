package com.yourcaryourway.backend.mapper;

import com.yourcaryourway.backend.dto.support.message.SupportMessageResponseDTO;
import com.yourcaryourway.backend.model.SupportMessage;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between SupportMessage entity and SupportMessageResponseDTO.
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

}
