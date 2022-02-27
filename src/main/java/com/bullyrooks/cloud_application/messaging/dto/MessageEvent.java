package com.bullyrooks.cloud_application.messaging.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageEvent implements Serializable {
    private String messageId;
    private String firstName;
    private String lastName;
    private String message;
}
