package com.bullyrooks.cloud_application.repository.mapper;

import com.bullyrooks.cloud_application.repository.document.MessageDocument;
import com.bullyrooks.cloud_application.service.model.Message;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MessageDocumentMapper {
    MessageDocumentMapper INSTANCE = Mappers.getMapper(MessageDocumentMapper.class);

    MessageDocument modelToDocument(Message model);

    Message documentToModel(MessageDocument returnDoc);
}

