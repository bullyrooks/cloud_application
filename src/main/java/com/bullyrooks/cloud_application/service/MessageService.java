package com.bullyrooks.cloud_application.service;

import com.bullyrooks.cloud_application.config.LoggingEnabled;
import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorClient;
import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import com.bullyrooks.cloud_application.repository.MessageRepository;
import com.bullyrooks.cloud_application.repository.document.MessageDocument;
import com.bullyrooks.cloud_application.repository.mapper.MessageDocumentMapper;
import com.bullyrooks.cloud_application.service.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@LoggingEnabled
public class MessageService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    MessageGeneratorClient messageGeneratorClient;

    public Message saveMessage(Message message){
        if (StringUtils.isEmpty(message.getMessage())){
            log.info("No message, retrieve from message generator");
            MessageResponseDTO dto = messageGeneratorClient.getMessage();
            message.setMessage(dto.getMessage());
            log.info("retrieved message: {}", message.getMessage());
        }

        MessageDocument msgDoc = MessageDocumentMapper.INSTANCE.modelToDocument(message);

        log.info("saving document: {}", msgDoc);
        MessageDocument returnDoc = messageRepository.save(msgDoc);
        return MessageDocumentMapper.INSTANCE.documentToModel(returnDoc);
    }
}
