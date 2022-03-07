package com.bullyrooks.cloud_application.message_generator.mapper;

import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import com.bullyrooks.cloud_application.service.model.MessageModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageGeneratorMapper {
    MessageGeneratorMapper INSTANCE = Mappers.getMapper(MessageGeneratorMapper.class);

    MessageModel messageResponseToMessage(@MappingTarget MessageModel in, MessageResponseDTO dto);
}
