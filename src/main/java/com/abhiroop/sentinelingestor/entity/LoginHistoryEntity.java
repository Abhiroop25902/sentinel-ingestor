package com.abhiroop.sentinelingestor.entity;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.spring.data.firestore.Document;
import lombok.Builder;

@Builder(toBuilder = true)
@Document(collectionName = "login_history")
public record LoginHistoryEntity(
        @DocumentId
        String id,
        String email,
        boolean success,
        Timestamp timestamp,
        Timestamp expireAt
) {
}
