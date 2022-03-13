package com.bullyrooks.cloud_application.controller.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class CreateMessageResponseDTO {
    private String messageId;
    private String firstName;
    private String lastName;
    private String message;
    private Instant generatedDate;
    private String source;
}
