package com.bullyrooks.cloud_application.repository.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("messages")
@Data
public class MessageDocument {
    @Id
    private String messageId;
    private String firstName;
    private String lastName;
    private String message;
}
