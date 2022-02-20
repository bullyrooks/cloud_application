package com.bullyrooks.cloud_application.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.logzio.LogzioConfig;
import io.micrometer.logzio.LogzioMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Hashtable;

@Configuration
@AutoConfigureBefore({
        CompositeMeterRegistryAutoConfiguration.class,
        SimpleMetricsExportAutoConfiguration.class
})
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass(LogzioMeterRegistry.class)
@Profile("!test")
public class LogzioMicrometerConfiguration {

    @Value("${logzio.metrics.url}")
    String logzioMetricsUrl;
    @Value("${logzio.metrics.token}")
    String logzioMetricsToken;
    @Bean
    public LogzioConfig newLogzioConfig() {
        return new LogzioConfig() {
            @Override
            public String get(String key) {
                return null;
            }

            @Override
            public String uri() {
                return logzioMetricsUrl;
                // example:
                // return "https://listener.logz.io:8053";
            }

            @Override
            public String token() {
                return logzioMetricsToken;
            }

            @Override
            public Duration step() {
                return Duration.ofSeconds(10);
                // example:
                // return Duration.ofSeconds(30);
            }

            @Override
            public Hashtable<String, String> includeLabels() {
                return new Hashtable<>();
            }

            @Override
            public Hashtable<String, String> excludeLabels() {
                return new Hashtable<>();
            }
        };
    }

    @Bean
    public MeterRegistry logzioMeterRegistry(LogzioConfig config) {
        LogzioMeterRegistry logzioMeterRegistry =
                new LogzioMeterRegistry(config, Clock.SYSTEM);
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("env", "dev"));
        tags.add(Tag.of("service", "cloud-application"));
        return logzioMeterRegistry;
    }
}
