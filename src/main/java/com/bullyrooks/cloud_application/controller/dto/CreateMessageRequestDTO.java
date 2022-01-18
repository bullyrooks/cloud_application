package com.bullyrooks.cloud_application.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateMessageRequestDTO {
    private String firstName;
    private String lastName;
    private String message;
}
