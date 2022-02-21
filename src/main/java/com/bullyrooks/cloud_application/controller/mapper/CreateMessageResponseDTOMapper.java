package com.bullyrooks.cloud_application.controller.mapper;

import com.bullyrooks.cloud_application.controller.dto.CreateMessageResponseDTO;
import com.bullyrooks.cloud_application.service.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateMessageResponseDTOMapper {
    CreateMessageResponseDTOMapper INSTANCE = Mappers.getMapper(CreateMessageResponseDTOMapper.class);

    CreateMessageResponseDTO modelToDTO(Message message);

}
