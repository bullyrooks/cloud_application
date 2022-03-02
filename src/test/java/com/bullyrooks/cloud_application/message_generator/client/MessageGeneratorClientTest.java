package com.bullyrooks.cloud_application.message_generator.client;

import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import com.bullyrooks.cloud_application.message_generator.client.dto.MessageResponseDTO;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Contract;
import feign.Feign;
import feign.codec.Decoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.support.*;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageDecoder;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.HttpMethod;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
@PactTestFor(providerName = MessageGeneratorClientTest.PROVIDER, port = MessageGeneratorClientTest.PROVIDER_PORT)
@Slf4j
public class MessageGeneratorClientTest {
    final static String PROVIDER = "message-generator";
    final static String PROVIDER_PORT = "8888";
    final static String CONSUMER = "cloud-application";
    final static String JSON_RESPONSE = "message-generator-response.json";

    private MessageGeneratorClient messageGeneratorClient;

    @Autowired
    ApplicationContext context;
    @BeforeEach
    public void init() {
        messageGeneratorClient = Feign.builder()
                .contract(new SpringMvcContract())
                .decoder(feignDecoder())
                .target(MessageGeneratorClient.class, "http://localhost:"+PROVIDER_PORT);
        log.info("messageGeneratorClient: {}", messageGeneratorClient);
    }
    public Decoder feignDecoder() {
        List<HttpMessageConverter<?>> converters = new RestTemplate().getMessageConverters();
        ObjectFactory<HttpMessageConverters> factory = () -> new HttpMessageConverters(converters);
        return new ResponseEntityDecoder(new SpringDecoder(factory, context.getBeanProvider(HttpMessageConverterCustomizer.class)));
    }
    @Pact(provider = MessageGeneratorClientTest.PROVIDER, consumer = MessageGeneratorClientTest.CONSUMER)
    public RequestResponsePact generateMessagePact(PactDslWithProvider builder) throws JSONException, IOException {
        // @formatter:off
        return builder
                .given("generator creates a message")
                .uponReceiving("a request to GET a message")
                .path("/message")
                .method(HttpMethod.GET)
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .matchHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .body(getJsonBody())
                .toPact();
        // @formatter:on
    }

    private JSONObject getJsonBody() throws IOException, JSONException {
        File f = new File("src/test/resources/json/" + JSON_RESPONSE);
        InputStream is = new FileInputStream(f);
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        log.info("test data: {}" + jsonTxt);
        JSONObject json = new JSONObject(jsonTxt);
        return json;
    }

    @Test
    @PactTestFor(pactMethod="generateMessagePact")
    public void generateMessage() {
        MessageResponseDTO response = messageGeneratorClient.getMessage();
        assertTrue(StringUtils.isNotBlank(response.getMessage()));
    }
}
