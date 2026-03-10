package com.abhiroop.sentinelingestor.controller;

import com.abhiroop.sentinelingestor.dto.LoginHistoryDto;
import com.abhiroop.sentinelingestor.dto.PubSubMessageData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@RestController
@AllArgsConstructor
public class EventController {
    private final ObjectMapper objectMapper;

    @PostMapping("/event")
    public ResponseEntity<CloudEvent> createEvent(@RequestBody CloudEvent event) throws IOException {
        log.info("Event Id: {}", event.getId());

        final var bytes = Objects.requireNonNull(event.getData()).toBytes();
        final var jsonString = new String(bytes, StandardCharsets.UTF_8);

        log.info("Event Data String: {}", jsonString);

        final PubSubMessageData<LoginHistoryDto> pubSubMessageData = objectMapper.readValue(
                bytes,
                new TypeReference<>() {
                }
        );

        log.info("Event Data: {}", pubSubMessageData);

        return ResponseEntity.ok().body(event);
    }
}
