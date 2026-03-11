package com.abhiroop.sentinelingestor.core.eventprocessor;

import com.fasterxml.jackson.databind.JsonNode;
import reactor.core.publisher.Mono;

public interface EventProcessorStrategy {
    String getStrategyKey();

    Mono<?> process(JsonNode dataBytes);
}
