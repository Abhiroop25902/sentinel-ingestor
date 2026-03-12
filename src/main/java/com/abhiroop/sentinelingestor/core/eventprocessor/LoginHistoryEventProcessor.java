package com.abhiroop.sentinelingestor.core.eventprocessor;

import com.abhiroop.sentinelingestor.dto.LoginHistoryDto;
import com.abhiroop.sentinelingestor.entity.LoginHistoryEntity;
import com.abhiroop.sentinelingestor.mapper.LoginHistoryMapper;
import com.abhiroop.sentinelingestor.repository.LoginHistoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
class LoginHistoryEventProcessor implements EventProcessorStrategy {

    private final ObjectMapper objectMapper;
    private final LoginHistoryMapper loginHistoryMapper;
    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    public String getStrategyKey() {
        return "login-history";
    }

    @Override
    public Mono<LoginHistoryEntity> process(JsonNode dataNode) {
        return Mono.fromCallable(() -> objectMapper.treeToValue(dataNode, LoginHistoryDto.class))
                //TODO: remove this doOnNext after debug
                .doOnNext(loginHistoryDto -> log.info("loginHistoryDto: {}", loginHistoryDto))
                .doOnError(e -> log.error("objectMapper.treeToValue error", e))
                .map(loginHistoryMapper::toEntity)
                //TODO: remove this doOnNext after debug
                .doOnNext(loginHistoryEntity -> log.info("loginHistoryEntity: {}", loginHistoryEntity));

//                .flatMap(loginHistoryRepository::save);
    }
}
