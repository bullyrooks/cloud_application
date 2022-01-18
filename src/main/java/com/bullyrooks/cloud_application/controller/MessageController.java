package com.bullyrooks.cloud_application.controller;

import com.bullyrooks.cloud_application.controller.dto.CreateMessageRequestDTO;
import com.bullyrooks.cloud_application.controller.dto.CreateMessageResponseDTO;
import com.bullyrooks.cloud_application.controller.dto.HelloWorldResponse;
import com.bullyrooks.cloud_application.controller.mapper.CreateMessageRequestDTOMapper;
import com.bullyrooks.cloud_application.controller.mapper.CreateMessageResponseDTOMapper;
import com.bullyrooks.cloud_application.service.MessageService;
import com.bullyrooks.cloud_application.service.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class MessageController {

    @Autowired
    MessageService messageService;

    @PostMapping("/message")
    public CreateMessageResponseDTO createMessage(@RequestBody CreateMessageRequestDTO request){
        log.info("createMessage : {}", request);
        Message message = CreateMessageRequestDTOMapper.INSTANCE.dtoToModel(request);
        Message response = messageService.saveMessage(message);
        return CreateMessageResponseDTOMapper.INSTANCE.modelToDTO(response);
    }

}