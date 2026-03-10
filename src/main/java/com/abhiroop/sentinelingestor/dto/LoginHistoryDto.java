package com.abhiroop.sentinelingestor.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record LoginHistoryDto(
        String id,
        String email,
        boolean success,
        long timestamp
) {
}
