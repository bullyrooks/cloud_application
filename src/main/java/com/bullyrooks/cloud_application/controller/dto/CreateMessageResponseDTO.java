package com.bullyrooks.cloud_application.controller.dto;

import lombok.Data;

@Data
public class CreateMessageResponseDTO {
    private String messageId;
    private String firstName;
    private String lastName;
    private String message;
}
