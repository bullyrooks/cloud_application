package com.bullyrooks.cloud_application.controller;

import com.bullyrooks.cloud_application.controller.dto.CreateMessageRequestDTO;
import com.bullyrooks.cloud_application.controller.dto.CreateMessageResponseDTO;
import com.bullyrooks.cloud_application.message_generator.client.MessageGeneratorClient;
import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import com.bullyrooks.cloud_application.repository.MessageRepository;
import com.bullyrooks.cloud_application.repository.document.MessageDocument;
import com.github.javafaker.Faker;
import com.github.javafaker.service.FakeValuesService;
import com.github.javafaker.service.RandomService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MongoDBContainer;

import java.util.List;
import java.util.Locale;

import static com.mongodb.internal.connection.tlschannel.util.Util.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MessageControllerTest {

    static MongoDBContainer mongoDBContainer = new      MongoDBContainer("mongo:4.4.10");
    {
        mongoDBContainer.start();
    }
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
    private final String MESSAGE_PATH = "/message";

    @LocalServerPort
    int randomServerPort;

    @Autowired
    MessageRepository messageRepository;
    @MockBean
    MessageGeneratorClient messageGeneratorClient;

    FakeValuesService fakesvc = new FakeValuesService(
            new Locale("en-US"), new RandomService());
    Faker faker = new Faker();


    @Test
    void testSaveMessage(){
        Long userId = 1l;

        //given
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

        MessageDocument savedDoc = messageRepository.findById(dto.getMessageId()).get();
        assertEquals(request.getFirstName(), savedDoc.getFirstName());
        assertEquals(request.getLastName(), savedDoc.getLastName());
        assertEquals(request.getMessage(), savedDoc.getMessage());
    }
    @Test
    void testGetReturnsMessageIfMissing(){
        Long userId = 1l;

        //given
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

        MessageDocument savedDoc = messageRepository.findById(dto.getMessageId()).get();
        assertEquals(request.getFirstName(), savedDoc.getFirstName());
        assertEquals(request.getLastName(), savedDoc.getLastName());
        assertEquals(dto.getMessage(), savedDoc.getMessage());
    }

}
