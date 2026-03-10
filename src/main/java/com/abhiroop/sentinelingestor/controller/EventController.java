package com.abhiroop.sentinelingestor.controller;

import com.abhiroop.sentinelingestor.dto.LoginHistoryDto;
import com.abhiroop.sentinelingestor.dto.PubSubMessageData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.events.cloud.pubsub.v1.PubsubMessage;
import com.google.protobuf.util.JsonFormat;
import io.cloudevents.CloudEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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

        PubsubMessage.Builder messageBuilder = PubsubMessage.newBuilder();
        // Use Google's JsonFormat to parse the string into the Proto object
        JsonFormat.parser().merge(new String(bytes, StandardCharsets.UTF_8), messageBuilder);
        PubsubMessage message = messageBuilder.build();
        // Now you can get the attributes and the data
        String base64Data = message.getData().toStringUtf8();

        // 2. Decode the Base64 "inner" JSON
        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);

        final PubSubMessageData<LoginHistoryDto> pubSubMessageData = objectMapper.readValue(
                decodedBytes,
                new TypeReference<>() {
                }
        );

        log.info("Event Data: {}", pubSubMessageData);

        return ResponseEntity.ok().body(event);
    }
}
