package org.example.securityproject;

import org.example.securityproject.enums.ServicesPackage;
import org.example.securityproject.model.Ad;
import org.example.securityproject.model.User;
import org.example.securityproject.service.AdService;
import org.example.securityproject.service.RateLimiterService;
import org.example.securityproject.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateException;

@SpringBootTest
public class VisitAdSimulationTest {

    private static final String BASE_URL = "https://localhost:443/api/ads/visit-ad";
    private static final Integer adId = 11;

    @Autowired
    private AdService adService;

    @Autowired
    private UserService userService;

    @Autowired
    private RateLimiterService rateLimiterService;

    private RestTemplate restTemplate = createRestTemplate();

    private RestTemplate createRestTemplate() {
        try {
            SSLContext sslContext = SSLContext.getInstance("TLS");

            sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((s, sslSession) -> true);

            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            return new RestTemplate(factory);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void simulateAdVisits() {
        Ad ad = adService.findAdById(adId);
        int numberOfSimulations = 10000;
        long delay = 60 * 1000 / numberOfSimulations; // Delay in milliseconds between each request
        AtomicInteger successfulVisits = new AtomicInteger();
        AtomicInteger failedVisits = new AtomicInteger();
        AtomicInteger rateLimitExceeded = new AtomicInteger();

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);

        for (int i = 0; i < numberOfSimulations; i++) {
            if (!rateLimiterService.allowVisit(ad.getUser().getServicesPackage())) {
                rateLimitExceeded.incrementAndGet();
                System.out.println("Rate limit exceeded for package: " + ad.getUser().getServicesPackage());
                continue; // Preskoči zakazivanje izvršavanja ako je rate limit premašen
            }

            executorService.schedule(() -> {
                simulateAdVisit(adId, successfulVisits, failedVisits);
            }, i * delay, TimeUnit.MILLISECONDS);
        }

        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Total successful ad visits: " + successfulVisits.get());
        System.out.println("Total rate limit exceeded: " + rateLimitExceeded.get());
    }

    private void simulateAdVisit(Integer adId, AtomicInteger successfulVisits, AtomicInteger failedVisits) {
        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("adId", adId)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                successfulVisits.incrementAndGet();
            } else {
                failedVisits.incrementAndGet();
            }
            System.out.println("Response: " + response.getStatusCode() + " - " + response.getBody());
        } catch (Exception e) {
            failedVisits.incrementAndGet();
            System.out.println("Request failed: " + e.getMessage());
        }
    }

}
