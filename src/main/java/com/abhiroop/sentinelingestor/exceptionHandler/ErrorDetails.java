package com.abhiroop.sentinelingestor.exceptionHandler;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorDetails(
        String message,
        String details,
        Instant timestamp
) {
}
