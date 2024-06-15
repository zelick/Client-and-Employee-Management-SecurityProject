package org.example.securityproject.service;

import org.example.securityproject.enums.ServicesPackage;
import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final Map<ServicesPackage, Bucket> buckets = new ConcurrentHashMap<>();

    public RateLimiterService() {
        initializeBuckets();
    }

    private void initializeBuckets() {
        Bandwidth basicBandwidth = Bandwidth.simple(10, Duration.ofMinutes(1));
        Bandwidth standardBandwidth = Bandwidth.simple(100, Duration.ofMinutes(1));
        Bandwidth goldenBandwidth = Bandwidth.simple(10000, Duration.ofMinutes(1));

        buckets.put(ServicesPackage.BASIC, Bucket4j.builder().addLimit(basicBandwidth).build());
        buckets.put(ServicesPackage.STANDARD, Bucket4j.builder().addLimit(standardBandwidth).build());
        buckets.put(ServicesPackage.GOLDEN, Bucket4j.builder().addLimit(goldenBandwidth).build());
    }

    public boolean allowVisit(ServicesPackage servicesPackage) {
        Bucket bucket = buckets.get(servicesPackage);
        if (bucket != null) {
            synchronized (bucket) { // Ensure thread safety at bucket level
                ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
                boolean isConsumed = probe.isConsumed();
                long remainingTokens = probe.getRemainingTokens();
                System.out.println("RateLimiterService: Package: " + servicesPackage + ", Consumed: " + isConsumed + ", Remaining Tokens: " + remainingTokens);
                return isConsumed;
            }
        }
        return false;
    }
}
