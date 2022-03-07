package com.bullyrooks.cloud_application.service;

import com.bullyrooks.cloud_application.config.LoggingEnabled;
import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorClient;
import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import com.bullyrooks.cloud_application.message_generator.mapper.MessageGeneratorMapper;
import com.bullyrooks.cloud_application.messaging.mapper.MessageEventMapper;
import com.bullyrooks.cloud_application.service.model.MessageModel;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@Slf4j
@LoggingEnabled
public class MessageService {

    MessageGeneratorClient messageGeneratorClient;
    MeterRegistry logzioMeterRegistry;
    StreamBridge streamBridge;

    Counter msgCount;
    Counter genMsgCount;
    Counter genMsgSuccess;
    Counter messageSaved;

    @Autowired
    public MessageService(MessageGeneratorClient messageGeneratorClient,
                          StreamBridge streamBridge,
                          MeterRegistry logzioMeterRegistry){
        this.messageGeneratorClient = messageGeneratorClient;
        this.streamBridge = streamBridge;
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


    public MessageModel saveMessage(MessageModel messageModel) {

        msgCount.increment();
        if (StringUtils.isEmpty(messageModel.getMessage())) {

            genMsgCount.increment();
            log.info("No message, retrieve from message generator");
            MessageResponseDTO dto = messageGeneratorClient.getMessage();
            messageModel = MessageGeneratorMapper.INSTANCE.messageResponseToMessage(messageModel, dto);
            genMsgSuccess.increment();
            log.info("retrieved message: {}", messageModel.getMessage());
        } else {
            //if they provided a message, return now
            messageModel.setGeneratedDate(Instant.now());
        }

        log.info("publishing event: {}", messageModel);
        streamBridge.send("message.created",
                MessageEventMapper.INSTANCE.modelToEvent(messageModel));
        return messageModel;
    }
}
