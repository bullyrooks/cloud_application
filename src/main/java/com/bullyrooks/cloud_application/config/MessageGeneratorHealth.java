package com.bullyrooks.cloud_application.config;

import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorClient;
import com.bullyrooks.cloud_application.message_generator.client.dto.HealthCheckDTO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class MessageGeneratorHealth implements HealthIndicator {

    @Autowired
    MessageGeneratorClient messageClient;

    public Health health() {
        Health.Builder status = Health.up();
        HealthCheckDTO healthCheckDTO = messageClient.getHealth();
        if (!StringUtils.equals("UP", healthCheckDTO.getStatus())) {
            status = Health.down();
        }
        return status.build();
    }
}
