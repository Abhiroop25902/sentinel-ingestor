package com.abhiroop.sentinelingestor.service;

import com.abhiroop.sentinelingestor.entity.LoginHistoryEntity;
import com.abhiroop.sentinelingestor.repository.LoginHistoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class LoginHistoryService {
    private final LoginHistoryRepository loginHistoryRepository;

    public Mono<LoginHistoryEntity> save(LoginHistoryEntity loginHistoryEntity) {
        return loginHistoryRepository.save(loginHistoryEntity)
                .map(savedLoginHistoryEntity -> savedLoginHistoryEntity);
    }
}
