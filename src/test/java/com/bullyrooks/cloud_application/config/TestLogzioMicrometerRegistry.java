package com.bullyrooks.cloud_application.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestLogzioMicrometerRegistry {

    @Bean
    public MeterRegistry logzioMeterRegistry() {
        return new SimpleMeterRegistry();
    }
}
