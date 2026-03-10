package com.abhiroop.sentinelingestor.configuration;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.time.Duration;

@Component
public class RateLimiterBucketConfig {
    @Bean
    public Bucket getRateLimiterBucket() {
        // 50 per second -> 1500 in the stress test duration
        return Bucket.builder()
                .addLimit(
                        Bandwidth.builder()
                                // max concurr 300 in google cloud per instance, bucket limit to 280
                                // 20 as buffer for /actuator/**
                                .capacity(280)
                                .refillGreedy(280, Duration.ofSeconds(1))
                                .build())
                .build();
    }

    @Bean
    public AntPathMatcher getPathMatcher() {
        return new AntPathMatcher();
    }
}
