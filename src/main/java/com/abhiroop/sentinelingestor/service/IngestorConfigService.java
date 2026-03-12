package com.abhiroop.sentinelingestor.service;

import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.ServerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@DependsOn("firebaseConfig")
public class IngestorConfigService {
    private final Map<String, Object> configMap = new ConcurrentHashMap<>();

    public IngestorConfigService() {
        configMap.put(IngesterConfigServiceKey.SAVE_TO_DB.getKey(), false);
        configMap.put(IngesterConfigServiceKey.PRINT_LOG.getKey(), false);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onStartup() {
        log.info("starting refresh config");
        refreshConfig()
                .doOnNext(template -> {
                    String jsonString = template.toJson();
                    log.info("jsonString: {}", jsonString);
                })
                .block(Duration.ofSeconds(10));
    }

    public <T> T getConfig(IngesterConfigServiceKey ingesterConfigServiceKey, T defaultValue, Class<T> clazz) {
        if (!configMap.containsKey(ingesterConfigServiceKey.getKey())) {
            return defaultValue;
        }
        Object value = configMap.get(ingesterConfigServiceKey.getKey());
        if (clazz.isInstance(value)) {
            return clazz.cast(value);
        }
        return defaultValue;
    }

    @Scheduled(fixedRate = 60000)
    public Mono<ServerTemplate> refreshConfig() {
        return Mono.create(sink -> {
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
        });
    }
}