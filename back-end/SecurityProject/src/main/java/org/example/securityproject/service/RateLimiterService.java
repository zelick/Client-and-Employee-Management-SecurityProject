package org.example.securityproject.service;

import org.example.securityproject.enums.ServicesPackage;
import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.ConsumptionProbe;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class RateLimiterService {
    private Map<ServicesPackage, Bucket> buckets = new HashMap<>();

    public RateLimiterService() {
        // Inicijalizacija Bucket-a za svaki paket
        initializeBuckets();
    }

    private void initializeBuckets() {
        // Definisanje ograničenja po paketima
        Bandwidth basicBandwidth = Bandwidth.simple(10, Duration.ofMinutes(1));
        Bandwidth standardBandwidth = Bandwidth.simple(100, Duration.ofMinutes(1));
        Bandwidth goldenBandwidth = Bandwidth.simple(10000, Duration.ofMinutes(1));

        // Inicijalizacija Bucket-a za svaki paket
        buckets.put(ServicesPackage.BASIC, Bucket4j.builder().addLimit(basicBandwidth).build());
        buckets.put(ServicesPackage.STANDARD, Bucket4j.builder().addLimit(standardBandwidth).build());
        buckets.put(ServicesPackage.GOLDEN, Bucket4j.builder().addLimit(goldenBandwidth).build());
    }

    public boolean allowVisit(ServicesPackage servicesPackage) {
        Bucket bucket = buckets.get(servicesPackage);
        if (bucket != null) {
            ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
            return probe.isConsumed();
        }
        return false;
    }
}
