package com.yourcaryourway.backend.mapper;

import com.yourcaryourway.backend.dto.support.SupportMessageDTO;
import com.yourcaryourway.backend.model.SupportMessage;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between SupportMessage entity and SupportMessageDTO.
 */
@Component
public class SupportMessageMapper {

    private final ModelMapper modelMapper;

    public SupportMessageMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    // convert a SupportMessage entity to a SupportMessageDTO
    public SupportMessageDTO toSupportMessageDTO(SupportMessage supportMessage) {
        return modelMapper.map(supportMessage, SupportMessageDTO.class);
    }

    // convert a list of SupportMessage entities to a list of SupportMessageDTOs
    public List<SupportMessageDTO> toSupportMessageList(List<SupportMessage> supportMessages) {
        return supportMessages.stream()
                .map(this::toSupportMessageDTO)
                .collect(Collectors.toList());
    }

}
