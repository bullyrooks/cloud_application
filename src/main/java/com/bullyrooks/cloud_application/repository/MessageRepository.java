package com.bullyrooks.cloud_application.repository;

import com.bullyrooks.cloud_application.repository.document.MessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends MongoRepository<MessageDocument, String> {

}
