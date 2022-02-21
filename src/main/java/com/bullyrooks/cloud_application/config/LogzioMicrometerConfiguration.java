package com.bullyrooks.cloud_application.config;

import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.logzio.LogzioConfig;
import io.micrometer.logzio.LogzioMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.export.simple.SimpleMetricsExportAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
public class LogzioMicrometerConfiguration {

    @Value("${logzio.metrics.url:http://test.com}")
    String logzioMetricsUrl;
    @Value("${logzio.metrics.token:TEST}")
    String logzioMetricsToken;

    /* real service instances connect to logzio */
    @Bean
    @ConditionalOnProperty(name="logzio.metrics.registry.mock", havingValue="false")
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
    @ConditionalOnProperty(name="logzio.metrics.registry.mock", havingValue="false")
    public MeterRegistry logzioMeterRegistry(LogzioConfig config) {
        LogzioMeterRegistry logzioMeterRegistry =
                new LogzioMeterRegistry(config, Clock.SYSTEM);
        ArrayList<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("env", "dev"));
        tags.add(Tag.of("service", "cloud-application"));
        return logzioMeterRegistry;
    }

    /* mock configuration used locally and in tests */

    @Bean
    @ConditionalOnProperty(name="logzio.metrics.registry.mock", havingValue="true")
    public MeterRegistry testRegistry() {
        return new SimpleMeterRegistry();
    }
    @Bean
    @ConditionalOnProperty(name="logzio.metrics.registry.mock", havingValue="true")
    public LogzioConfig logzioConfig(){
            //this is not needed
            return null;
        }
}
