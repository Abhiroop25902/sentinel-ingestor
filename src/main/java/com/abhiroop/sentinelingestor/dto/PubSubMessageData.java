package com.abhiroop.sentinelingestor.dto;

import lombok.Builder;

@Builder
public record PubSubMessageData<T>(String type, T data) {
}
