package com.abhiroop.sentinelingestor.repository;

import com.abhiroop.sentinelingestor.entity.LoginHistoryEntity;
import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginHistoryRepository extends FirestoreReactiveRepository<LoginHistoryEntity> {
}
