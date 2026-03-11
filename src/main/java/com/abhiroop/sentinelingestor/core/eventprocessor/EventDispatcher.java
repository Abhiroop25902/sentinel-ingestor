package com.abhiroop.sentinelingestor.core.eventprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class EventDispatcher {
    private final Map<String, EventProcessorStrategy> strategyMap;

    @Autowired
    public EventDispatcher(List<EventProcessorStrategy> eventProcessorStrategies) {
        strategyMap = eventProcessorStrategies.stream().collect(Collectors.toMap(EventProcessorStrategy::getStrategyKey, strategy -> strategy));
    }

    public Mono<?> dispatch(String type, JsonNode data) {
        return Mono.justOrEmpty(strategyMap.get(type))
                .switchIfEmpty(Mono.error(new UnsupportedOperationException("Type not supported: " + type)))
                .flatMap(strategy -> strategy.process(data))
                .doOnError(e -> log.error("Dispatch failed for type {}: {}", type, e.getMessage()));

    }
}
