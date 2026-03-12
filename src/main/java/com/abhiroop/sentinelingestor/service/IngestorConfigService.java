package com.abhiroop.sentinelingestor.service;

import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.ServerTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class IngestorConfigService {
    private final Map<String, Object> configMap = new ConcurrentHashMap<>();

    public IngestorConfigService() {
        configMap.put(IngesterConfigServiceKey.SAVE_TO_DB.getKey(), false);
        configMap.put(IngesterConfigServiceKey.PRINT_LOG.getKey(), false);
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

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void refreshConfig() {
        log.error("starting refresh config");
        Mono.<ServerTemplate>create(sink -> {
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
        }).doOnNext(template -> {
            String jsonString = template.toJson();
            log.error("jsonString: {}", jsonString);
        }).subscribe();
    }
}