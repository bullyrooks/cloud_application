package com.bullyrooks.cloud_application.messaging.mapper;

import com.bullyrooks.cloud_application.messaging.dto.MessageEvent;
import com.bullyrooks.cloud_application.service.model.MessageModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageEventMapper {
    MessageEventMapper INSTANCE = Mappers.getMapper(MessageEventMapper.class);

    MessageEvent modelToEvent(MessageModel model);

    MessageModel eventToModel(MessageEvent returnEvent);
}
