package com.abhiroop.sentinelingestor.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RateLimiterBucketConfig {
    @Bean
    public Bucket getRateLimiterBucket() {
        // 50 per second -> 1500 in the stress test duration
        return Bucket.builder()
                .addLimit(
                        Bandwidth.builder()
                                .capacity(50)
                                .refillGreedy(50, Duration.ofMinutes(1))
                                .build())
                .build();
    }
}
