package com.bullyrooks.cloud_application.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageModel {
    private String messageId;
    private String firstName;
    private String lastName;
    private String message;
    private Instant generatedDate;
}
