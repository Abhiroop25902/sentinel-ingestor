package com.abhiroop.sentinelingestor.controller;

import io.cloudevents.CloudEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@RestController
public class EventController {
    @PostMapping("/event")
    public ResponseEntity<CloudEvent> createEvent(@RequestBody CloudEvent event) {
        log.info("Event Id: {}", event.getId());
        log.info(new String(Objects.requireNonNull(event.getData()).toBytes(), StandardCharsets.UTF_8));

        return ResponseEntity.ok().body(event);
    }
}
