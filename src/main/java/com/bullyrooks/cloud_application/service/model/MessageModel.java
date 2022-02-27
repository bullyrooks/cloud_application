package com.bullyrooks.cloud_application.service.model;

import lombok.Data;

@Data
public class MessageModel {
    private String messageId;
    private String firstName;
    private String lastName;
    private String message;
}
