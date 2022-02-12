package com.bullyrooks.cloud_application.message_generator.client;

import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "message-generator")
public interface MessageGeneratorClient {
    @GetMapping(value = "/message",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    MessageResponseDTO getMessage();
}
