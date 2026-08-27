package com.example.app.service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.proxy.ProxyManager;

@Service
public class RateLimitingService {
    private static final int REQUEST_PER_MINUTE = 10;

    private final ProxyManager<String> proxyManager;
    private final Map<String, Bucket> localBuckets = new ConcurrentHashMap<>();

    public RateLimitingService(ObjectProvider<ProxyManager<String>> proxyManagerProvider) {
        this.proxyManager = proxyManagerProvider.getIfAvailable();
    }

    public Bucket resolveBucket(String key) {
        if (proxyManager == null) {
            return localBuckets.computeIfAbsent(key, ignored -> createLocalBucket());
        }

        Supplier<BucketConfiguration> configSupplier = this::getConfig;
        return proxyManager
            .builder()
            .build(key, configSupplier);
    }

    private BucketConfiguration getConfig() {
        return BucketConfiguration.builder()
            .addLimit(createLimit())
            .build();
    }

    private Bucket createLocalBucket() {
        return Bucket.builder().addLimit(createLimit()).build();
    }

    private Bandwidth createLimit() {
        return Bandwidth.builder()
            .capacity(REQUEST_PER_MINUTE)
            .refillIntervally(REQUEST_PER_MINUTE, Duration.ofMinutes(1))
            .build();
    }
}
