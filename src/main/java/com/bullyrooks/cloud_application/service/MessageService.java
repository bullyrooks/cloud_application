package com.bullyrooks.cloud_application.service;

import com.bullyrooks.cloud_application.repository.MessageRepository;
import com.bullyrooks.cloud_application.repository.document.MessageDocument;
import com.bullyrooks.cloud_application.repository.mapper.MessageDocumentMapper;
import com.bullyrooks.cloud_application.service.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MessageService {

    @Autowired
    MessageRepository messageRepository;

    public Message saveMessage(Message message){
        MessageDocument msgDoc = MessageDocumentMapper.INSTANCE.modelToDocument(message);
        log.info("saving document: {}", msgDoc);
        MessageDocument returnDoc = messageRepository.save(msgDoc);
        return MessageDocumentMapper.INSTANCE.documentToModel(returnDoc);
    }
}
