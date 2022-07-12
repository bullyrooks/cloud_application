package com.bullyrooks.cloud_application.message_generator.client;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageGeneratorClientConfig {
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(100, 2000, 3);
    }

}
