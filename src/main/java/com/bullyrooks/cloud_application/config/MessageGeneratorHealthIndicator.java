package com.bullyrooks.cloud_application.config;

import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorHealthClient;
import com.bullyrooks.cloud_application.message_generator.client.dto.HealthCheckDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.actuate.availability.AvailabilityStateHealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.availability.ApplicationAvailability;
import org.springframework.boot.availability.AvailabilityState;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MessageGeneratorHealthIndicator extends AvailabilityStateHealthIndicator {

    MessageGeneratorHealthClient messageClient;

    public MessageGeneratorHealthIndicator(ApplicationAvailability availability, MessageGeneratorHealthClient messageClient) {
        super(availability, ReadinessState.class, (statusMappings) -> {
            statusMappings.add(ReadinessState.ACCEPTING_TRAFFIC, Status.UP);
            statusMappings.add(ReadinessState.REFUSING_TRAFFIC, Status.OUT_OF_SERVICE);
        });
        this.messageClient = messageClient;
    }

    @Override
    protected AvailabilityState getState(ApplicationAvailability applicationAvailability) {

        AvailabilityState status = ReadinessState.ACCEPTING_TRAFFIC;
        try {
            ResponseEntity<HealthCheckDTO> healthCheckDTO = messageClient.getHealth();
            log.info("health check response: {}\n{}", healthCheckDTO.getStatusCode(), healthCheckDTO.getBody());
            if (!StringUtils.equals("UP", healthCheckDTO.getBody().getStatus())) {
                status = ReadinessState.REFUSING_TRAFFIC;
            }
        }catch (Exception e){
            log.error("error trying to get message generator health: {}", e.getMessage(),e);
            status = ReadinessState.REFUSING_TRAFFIC;
        }
        return status;
    }
}
