package com.bullyrooks.cloud_application.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.logzio.LogzioConfig;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestLogzioMicrometerRegistry {

    @Bean
    public MeterRegistry testRegistry() {
        return new SimpleMeterRegistry();
    }
    @MockBean
    LogzioMicrometerConfiguration logzioMicrometerConfiguration;
    @MockBean
    LogzioConfig logzioConfig;
}
