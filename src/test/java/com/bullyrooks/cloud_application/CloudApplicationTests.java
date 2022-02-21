package com.bullyrooks.cloud_application;

import com.bullyrooks.cloud_application.config.LogzioMicrometerConfiguration;
import com.bullyrooks.cloud_application.config.TestLogzioMicrometerRegistry;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.logzio.LogzioConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestLogzioMicrometerRegistry.class)
class CloudApplicationTests {

    @Test
    void contextLoads() {
    }

}
