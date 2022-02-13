package com.bullyrooks.cloud_application.message_generator.client;

import com.bullyrooks.cloud_application.message_generator.client.dto.HealthCheckDTO;
import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(contextId = "healthClient", name = "message-generator")
public interface MessageGeneratorHealthClient {

    @GetMapping(value = "/actuator/health",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    ResponseEntity<HealthCheckDTO> getHealth();
}
