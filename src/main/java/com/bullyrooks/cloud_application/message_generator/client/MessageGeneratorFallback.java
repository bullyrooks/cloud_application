package com.bullyrooks.cloud_application.message_generator.client;

import com.bullyrooks.cloud_application.service.model.MessageModel;

public class MessageGeneratorFallback {

    public static final String FAILURE_MESSAGE = "Failure isn't fatal, but failure to change might be";

    public static MessageModel getMessage(){
        return MessageModel.builder()
                .message(FAILURE_MESSAGE).build();
    }
}
