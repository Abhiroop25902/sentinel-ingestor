package com.abhiroop.sentinelingestor.controller;

import com.abhiroop.sentinelingestor.core.eventprocessor.EventDispatcher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cloudevents.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Objects;

@Slf4j
@RestController
@AllArgsConstructor
public class EventController {
    private final ObjectMapper objectMapper;
    private final EventDispatcher eventDispatcher;

    @PostMapping("/event")
    public Mono<?> createEvent(@RequestBody CloudEvent event) {
        return Mono.fromCallable(() -> {
                    // 1. Extract from CloudEvent
                    final var bytes = Objects.requireNonNull(event.getData()).toBytes();
                    JsonNode root = objectMapper.readTree(bytes);
                    // 2. Decode the Base64 "inner" JSON
                    String base64Data = root.path("message").path("data").asText();
                    if (base64Data.isEmpty()) throw new IllegalArgumentException("Empty Pub/Sub data");
                    return Base64.getDecoder().decode(base64Data);
                })
                .flatMap(decodedBytes -> Mono.fromCallable(() -> objectMapper.readTree(decodedBytes)))
                .flatMap(pubSubMessageData -> {
                            // pubSubMessageData is of type PubSubMessageData<?>
                            // ? is determined by the type, and each type is associated with its own processing
                            // Hence: Strategy Pattern can be used
                            String type = pubSubMessageData.path("type").asText();
                            JsonNode data = pubSubMessageData.path("data");

                            if (type.isEmpty() || data.isMissingNode()) {
                                return Mono.error(new IllegalArgumentException("Invalid pubSubMessageData format: {}"));
                            }

                            // PubSub assign unique id inside a topic, it can be used as firestore documentId
                            final String messageId = event.getId();
                            if (data instanceof ObjectNode objectNode) {
                                objectNode.put("id", messageId);
                            }

                            return eventDispatcher.dispatch(type, data);
                        }
                )
                .doOnError(e -> log.error("Ingestion failed: {}", e.getMessage()))
                .then();
    }
}
