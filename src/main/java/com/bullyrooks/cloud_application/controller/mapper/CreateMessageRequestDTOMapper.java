package com.bullyrooks.cloud_application.controller.mapper;

import com.bullyrooks.cloud_application.controller.dto.CreateMessageRequestDTO;
import com.bullyrooks.cloud_application.service.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CreateMessageRequestDTOMapper {
    CreateMessageRequestDTOMapper INSTANCE = Mappers.getMapper(CreateMessageRequestDTOMapper.class);

    Message dtoToModel(CreateMessageRequestDTO userAccountEntity);

}
