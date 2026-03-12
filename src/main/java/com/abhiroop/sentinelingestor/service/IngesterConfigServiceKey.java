package com.abhiroop.sentinelingestor.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IngesterConfigServiceKey {
    PRINT_LOG("printLog"),
    SAVE_TO_DB("saveToDb");

    private final String key;
}
