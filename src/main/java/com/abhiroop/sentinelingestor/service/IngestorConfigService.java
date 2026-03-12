package com.abhiroop.sentinelingestor.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.ServerTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
@Slf4j
@DependsOn("firebaseConfig")
@RequiredArgsConstructor
public class IngestorConfigService {
    private final Map<String, String> configMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("starting refresh config");
        refreshConfig()
                .doOnSuccess(v -> log.info("Sentinel: Initial config loaded successfully."))
                .doOnError(e -> log.error("Sentinel: Initial config load failed: {}", e.getMessage()))
                .subscribe();
    }

    public <T> T getConfig(IngesterConfigServiceKey ingesterConfigServiceKey, T defaultValue, Function<String, T> mapper) {
        if (!configMap.containsKey(ingesterConfigServiceKey.getKey())) {
            return defaultValue;
        }
        String value = configMap.get(ingesterConfigServiceKey.getKey());
        return mapper.apply(value);
    }

    @Scheduled(fixedRate = 60000)
    public Mono<Void> refreshConfig() {
        return Mono.<ServerTemplate>create(sink -> {
                    final var serverTemplateFuture = FirebaseRemoteConfig.getInstance().getServerTemplateAsync();

                    ApiFutures.addCallback(serverTemplateFuture, new ApiFutureCallback<>() {
                        @Override
                        public void onFailure(Throwable t) {
                            sink.error(t);
                        }

                        @Override
                        public void onSuccess(ServerTemplate template) {
                            sink.success(template);
                        }
                    }, MoreExecutors.directExecutor());
                })
                .map(ServerTemplate::toJson)
                .flatMap(json -> Mono.fromCallable(() -> objectMapper.readTree(json)))
                .doOnError(e -> log.error("objectMapper.readTree error: {}", e.getMessage()))
                .doOnNext(jsonNode -> {
                    final JsonNode parameters = jsonNode.path("parameters");
                    parameters.fieldNames().forEachRemaining(fieldName ->
                            configMap.put(fieldName, parameters.path(fieldName).path("defaultValue").path("value").asText())
                    );
                }).then(Mono.fromRunnable(() -> log.info("Successfully refreshed config: {}", configMap)));
    }
}