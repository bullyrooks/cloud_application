package com.bullyrooks.cloud_application.config;

import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorClient;
import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorHealthClient;
import com.bullyrooks.cloud_application.message_generator.client.dto.HealthCheckDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageGeneratorHealth implements HealthIndicator {

    @Autowired
    MessageGeneratorHealthClient messageClient;

    public Health health() {
        Health.Builder status = Health.up();
        try {
            ResponseEntity<HealthCheckDTO> healthCheckDTO = messageClient.getHealth();
            log.info("health check response: {}\n{}", healthCheckDTO.getStatusCode(), healthCheckDTO.getBody());
            if (!StringUtils.equals("UP", healthCheckDTO.getBody().getStatus())) {
                status = Health.down();
            }
        }catch (Exception e){
            log.error("error trying to get message generator health: {}", e.getMessage(),e);
            status = Health.down();
        }
        return status.build();
    }
}
