package com.bullyrooks.cloud_application.controller.dto;

import lombok.Data;

@Data
public class CreateMessageRequestDTO {
    private String firstName;
    private String lastName;
    private String message;
}
