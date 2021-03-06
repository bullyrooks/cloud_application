package com.bullyrooks.cloud_application.message_generator.client;

import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "message-generator",
        configuration = MessageGeneratorClientConfig.class,
        url = "${message-generator.client.url:#{null}}",
        fallback = MessageGeneratorClientFallback.class)
public interface MessageGeneratorClient {

    String MESSAGE_GENERATOR_SERVICE = "message-generator";


    @GetMapping(value = "/message",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    MessageResponseDTO getMessage();

}
