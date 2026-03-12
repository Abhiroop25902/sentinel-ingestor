package com.abhiroop.sentinelingestor.core.eventprocessor;

import com.abhiroop.sentinelingestor.dto.LoginHistoryDto;
import com.abhiroop.sentinelingestor.entity.LoginHistoryEntity;
import com.abhiroop.sentinelingestor.mapper.LoginHistoryMapper;
import com.abhiroop.sentinelingestor.repository.LoginHistoryRepository;
import com.abhiroop.sentinelingestor.service.IngesterConfigServiceKey;
import com.abhiroop.sentinelingestor.service.IngestorConfigService;
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
    private final IngestorConfigService ingestorConfigService;
    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    public String getStrategyKey() {
        return "login-history";
    }

    @Override
    public Mono<LoginHistoryEntity> process(JsonNode dataNode) {
        return Mono.fromCallable(() -> objectMapper.treeToValue(dataNode, LoginHistoryDto.class))
                .doOnError(e -> log.error("objectMapper.treeToValue error", e))
                .doOnNext(loginHistoryDto -> {
                    if (ingestorConfigService.getConfig(IngesterConfigServiceKey.PRINT_LOG, false, Boolean.class)) {
                        log.info("loginHistoryDto: {}", loginHistoryDto);
                    }
                })
                .map(loginHistoryMapper::toEntity)
                .doOnNext(loginHistoryEntity -> {
                    if (ingestorConfigService.getConfig(IngesterConfigServiceKey.PRINT_LOG, false, Boolean.class)) {
                        log.info("loginHistoryEntity: {}", loginHistoryEntity);
                    }
                })
                .flatMap(loginHistoryEntity -> {
                    if (ingestorConfigService.getConfig(IngesterConfigServiceKey.SAVE_TO_DB, false, Boolean.class)) {
                        return loginHistoryRepository.save(loginHistoryEntity);
                    }
                    return Mono.just(loginHistoryEntity);
                })
                .doOnError(e -> log.error("loginHistoryRepository.saveToDb error", e));
    }
}
