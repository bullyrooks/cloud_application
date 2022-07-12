package com.bullyrooks.cloud_application.controller;

import com.bullyrooks.cloud_application.controller.dto.CreateMessageRequestDTO;
import com.bullyrooks.cloud_application.controller.dto.CreateMessageResponseDTO;
import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorFallback;
import com.bullyrooks.cloud_application.service.MessageService;
import com.bullyrooks.cloud_application.service.model.MessageModel;
import com.github.javafaker.Faker;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Instant;

import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static java.nio.charset.Charset.defaultCharset;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.util.StreamUtils.copyToString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@AutoConfigureWireMock(port = 9561)
@Import(TestChannelBinderConfiguration.class)
public class MessageControllerResiliencyTest {

    @Autowired
    MessageService messageService;
    @LocalServerPort
    int randomServerPort;
    private final String MESSAGE_PATH = "/message";
    Faker faker = new Faker();
    @Autowired
    OutputDestination outputDestination;

    @Test
    public void whenGetMessage_thenMessageShouldBeReturned() throws IOException {
        Long userId = 1l;

        //given
        Instant testStart = Instant.now();
        CreateMessageRequestDTO request = CreateMessageRequestDTO
                .builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .message(faker.gameOfThrones().quote())
                .build();

        stubFor(WireMock.get(WireMock.urlEqualTo("/message"))
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withFixedDelay(100)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                copyToString(
                                        MessageControllerResiliencyTest.class
                                                .getClassLoader()
                                                .getResourceAsStream("json/message-generator-response.json"),
                                        defaultCharset()))));

        //when
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + randomServerPort + MESSAGE_PATH;
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .build();

        ResponseEntity<CreateMessageResponseDTO> result = restTemplate.postForEntity(
                builder.toUri(), request, CreateMessageResponseDTO.class);

        assertFalse(result.getBody().getMessage().isEmpty());
    }

    @Test
    public void whenGetMessageIsSlow_thenRequestShouldBeRetried() throws IOException {
        Long userId = 1l;

        //given
        Instant testStart = Instant.now();
        CreateMessageRequestDTO request = CreateMessageRequestDTO
                .builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();

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

        //when
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + randomServerPort + MESSAGE_PATH;
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .build();

        ResponseEntity<CreateMessageResponseDTO> result = restTemplate.postForEntity(
                builder.toUri(), request, CreateMessageResponseDTO.class);

        assertEquals(MessageGeneratorFallback.FAILURE_MESSAGE, result.getBody().getMessage());
    }
}
