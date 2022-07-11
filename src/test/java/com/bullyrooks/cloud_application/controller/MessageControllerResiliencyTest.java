package com.bullyrooks.cloud_application.controller;

import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorClient;
import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorFallback;
import com.bullyrooks.cloud_application.service.MessageService;
import com.bullyrooks.cloud_application.service.model.MessageModel;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureWireMock(port = 9561)
public class MessageControllerResiliencyTest {

    @Autowired
    MessageGeneratorClient messageGeneratorClient;

    @Autowired
    MessageService messageService;

    @Test
    public void whenGetMessage_thenMessageShouldBeReturned() throws IOException {
        stubFor(WireMock.get(WireMock.urlEqualTo("/message"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        MessageControllerResiliencyTest.class
                                                .getClassLoader()
                                                .getResourceAsStream("json/message-generator-response.json"),
                                        defaultCharset()))));
        String response =messageGeneratorClient.getMessage().getMessage();
        assertFalse(response.isEmpty());
    }

    @Test
    public void whenGetMessageIsSlow_thenRequestShouldBeRetried() throws IOException {
        stubFor(WireMock.get(WireMock.urlEqualTo("/message"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withFixedDelay(5000)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        MessageControllerResiliencyTest.class
                                                .getClassLoader()
                                                .getResourceAsStream("json/message-generator-response.json"),
                                        defaultCharset()))));
        String response =messageService.getMessageFromMessageGeneratorService(
                MessageModel.builder()
                        .build()).getMessage();
        assertEquals(MessageGeneratorFallback.FAILURE_MESSAGE, response);
    }
}
