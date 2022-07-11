package com.bullyrooks.cloud_application.message_generator.client;

import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "message-generator", url = "${message-generator.client.url}")
public interface MessageGeneratorClient {

    String MESSAGE_GENERATOR_SERVICE = "message-generator";


    @GetMapping(value = "/message",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    MessageResponseDTO getMessage();

}
