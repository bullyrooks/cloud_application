package com.bullyrooks.cloud_application.controller;

import com.bullyrooks.cloud_application.controller.dto.CreateMessageRequestDTO;
import com.bullyrooks.cloud_application.controller.dto.CreateMessageResponseDTO;
import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorClient;
import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import com.bullyrooks.cloud_application.messaging.dto.MessageEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@AutoConfigureMockMvc
@Import(TestChannelBinderConfiguration.class)
public class MessageControllerTest {

    private final String MESSAGE_PATH = "/message";

    @LocalServerPort
    int randomServerPort;

    @MockBean
    MessageGeneratorClient messageGeneratorClient;

    @Autowired
    OutputDestination outputDestination;

    Faker faker = new Faker();

    @AfterEach
    void cleanup(){
        //cleanup
        outputDestination.clear("message.created");
    }

    @Test
    void testSaveMessage() throws IOException {
        Long userId = 1l;

        //given
        Instant testStart = Instant.now();
        CreateMessageRequestDTO request = CreateMessageRequestDTO
                .builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .message(faker.gameOfThrones().quote())
                .build();

        //when
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + randomServerPort + MESSAGE_PATH;
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .build();

        ResponseEntity<CreateMessageResponseDTO> result = restTemplate.postForEntity(
                builder.toUri(), request, CreateMessageResponseDTO.class);

        //then
        CreateMessageResponseDTO dto = result.getBody();
        assertEquals(request.getFirstName(), dto.getFirstName());
        assertEquals(request.getLastName(), dto.getLastName());
        assertEquals(request.getMessage(), dto.getMessage());

        Message<byte[]> receievedMessage = outputDestination.receive(1000,"message.created");
        String messageStr = new String(receievedMessage.getPayload(), StandardCharsets.UTF_8);
        log.info("message string: {}", messageStr);
        ObjectMapper mapper = new ObjectMapper();
        MessageEvent event = mapper.reader().readValue(messageStr, MessageEvent.class);
        assertEquals(request.getFirstName(), event.getFirstName());
        assertEquals(request.getLastName(), event.getLastName());
        assertEquals(request.getMessage(), event.getMessage());


    }
    @Test
    void testGetReturnsMessageIfMissing() throws InterruptedException, IOException {
        Long userId = 1l;

        //given
        Instant testStart = Instant.now();
        CreateMessageRequestDTO request = CreateMessageRequestDTO
                .builder()
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .build();

        when(messageGeneratorClient.getMessage()).thenReturn(
                MessageResponseDTO.builder()
                .message(faker.gameOfThrones().quote())
                .build());

        //when
        RestTemplate restTemplate = new RestTemplate();
        String baseUrl = "http://localhost:" + randomServerPort + MESSAGE_PATH;
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .build();

        ResponseEntity<CreateMessageResponseDTO> result = restTemplate.postForEntity(
                builder.toUri(), request, CreateMessageResponseDTO.class);

        //then
        CreateMessageResponseDTO dto = result.getBody();
        assertEquals(request.getFirstName(), dto.getFirstName());
        assertEquals(request.getLastName(), dto.getLastName());
        assertTrue(StringUtils.isNotBlank(dto.getMessage()));

        Message<byte[]> receievedMessage = outputDestination.receive(1000,"message.created");
        String messageStr = new String(receievedMessage.getPayload(), StandardCharsets.UTF_8);
        log.info("message string: {}", messageStr);
        ObjectMapper mapper = new ObjectMapper();
        MessageEvent event = mapper.reader().readValue(messageStr, MessageEvent.class);
        assertEquals(request.getFirstName(), event.getFirstName());
        assertEquals(request.getLastName(), event.getLastName());
        assertTrue(StringUtils.isNotBlank(event.getMessage()));

    }

}
