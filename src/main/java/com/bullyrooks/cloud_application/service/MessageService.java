package com.bullyrooks.cloud_application.service;

import com.bullyrooks.cloud_application.config.LoggingEnabled;
import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorClient;
import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import com.bullyrooks.cloud_application.repository.MessageRepository;
import com.bullyrooks.cloud_application.repository.document.MessageDocument;
import com.bullyrooks.cloud_application.repository.mapper.MessageDocumentMapper;
import com.bullyrooks.cloud_application.service.model.Message;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@LoggingEnabled
public class MessageService {

    MessageRepository messageRepository;
    MessageGeneratorClient messageGeneratorClient;
    MeterRegistry logzioMeterRegistry;

    Counter msgCount;
    Counter genMsgCount;
    Counter genMsgSuccess;
    Counter messageSaved;

    @Autowired
    public MessageService(MessageRepository messageRepository,
                          MessageGeneratorClient messageGeneratorClient,
                          MeterRegistry logzioMeterRegistry){
        this.messageRepository = messageRepository;
        this.messageGeneratorClient = messageGeneratorClient;
        this.logzioMeterRegistry = logzioMeterRegistry;
        initCounters();
    }

    private void initCounters() {
        msgCount= Counter.builder("message.request.count")
                .description("Number of message requests received by the service")
                .register(logzioMeterRegistry);
        genMsgCount = Counter.builder("message.generated.request.count")
                .description("Number of message generated requests to message-generator")
                .register(logzioMeterRegistry);
        genMsgSuccess = Counter.builder("message.generated.request.success")
                .description("Number of message generated success responses from message-generator")
                .register(logzioMeterRegistry);
        messageSaved = Counter.builder("message.stored.count")
                .description("Number of messages successfully stored in the repository")
                .register(logzioMeterRegistry);
    }


    public Message saveMessage(Message message){

        msgCount.increment();
        if (StringUtils.isEmpty(message.getMessage())){

            genMsgCount.increment();
            log.info("No message, retrieve from message generator");
            MessageResponseDTO dto = messageGeneratorClient.getMessage();
            message.setMessage(dto.getMessage());
            genMsgSuccess.increment();
            log.info("retrieved message: {}", message.getMessage());
        }

        MessageDocument msgDoc = MessageDocumentMapper.INSTANCE.modelToDocument(message);

        log.info("saving document: {}", msgDoc);
        MessageDocument returnDoc = messageRepository.save(msgDoc);
        messageSaved.increment();
        return MessageDocumentMapper.INSTANCE.documentToModel(returnDoc);
    }
}
