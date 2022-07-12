package com.bullyrooks.cloud_application.message_generator.client;

import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

@Component
@Slf4j
public class MessageGeneratorClientFallback implements MessageGeneratorClient{

    @Override
    public MessageResponseDTO getMessage(){
        log.warn("In fallback getMessage");
        return MessageResponseDTO.builder()
                .message(MessageGeneratorFallback.FAILURE_MESSAGE)
                .build();
    }

}
