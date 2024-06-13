package org.example.securityproject.services;

import org.example.securityproject.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LogAnalyzerService {

    @Autowired
    private EmailService emailService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int MAX_CLICK_ATTEMPTS = 100;

    private Map<String, Integer> failedLoginAttempts = new ConcurrentHashMap<>();
    private Map<String, Integer> incorrectPasswordAttempts = new ConcurrentHashMap<>();
    private Map<String, Integer> blockedUserAttempts = new ConcurrentHashMap<>();
    private Map<String, Integer> invalidTokenAttempts = new ConcurrentHashMap<>();
    private Map<String, Integer> unauthorizedAccessAttempts = new ConcurrentHashMap<>();
    private Map<String, Integer> endpointClickAttempts = new ConcurrentHashMap<>();

    private Map<String, Boolean> alertSentForBlockedUser = new ConcurrentHashMap<>();
    private Map<String, Boolean> alertSentForIncorrectPassword = new ConcurrentHashMap<>();
    private Map<String, Boolean> alertSentForTokenInvalid = new ConcurrentHashMap<>();
    private Map<String, Boolean> alertSentForUnauthorizedCriticalEvent = new ConcurrentHashMap<>();
    private Map<String, Boolean> alertSentForEndpointClicks = new ConcurrentHashMap<>();

    private String adminEmail = "zelickika@gmail.com";

    public void analyzeLogs(List<String> logs) {
        boolean criticalEventDetected = false;

        if (isRepeatedFailedLogins(logs)) {
            criticalEventDetected = true;
        }

        if (isBlockedUserAccessAttempt(logs)) {
            criticalEventDetected = true;
        }

        if (isRepeatedIncorrectPasswordAttempts(logs)) {
            criticalEventDetected = true;
        }

        if (isTokenInvalidDetected(logs)) {
            criticalEventDetected = true;
        }

        if (isUnauthorizedAccessAttempt(logs)) {
            criticalEventDetected = true;
        }

        if (isExcessiveEndpointClicks(logs)) {
            criticalEventDetected = true;
        }

        if (criticalEventDetected) {
            System.out.println("Critical event detected in logs");
        }
    }

    private boolean isRepeatedFailedLogins(List<String> logs) {
        for (String log : logs) {
            if (log.contains("User login failed: User not found for email") || log.contains("User login failed: Account is not active for email")) {
                String email = extractEmailFromLog(log);
                failedLoginAttempts.put(email, failedLoginAttempts.getOrDefault(email, 0) + 1);

                if (failedLoginAttempts.get(email) >= MAX_FAILED_ATTEMPTS && !alertSentForIncorrectPassword.getOrDefault(email, false)) {
                    alertSentForIncorrectPassword.put(email, true);
                    System.out.println("MULTIPLE INCORRECT USERNAME ATTEMPTS: " + email + " - Sending email alert");
                    emailService.sendCriticalEventAlert(adminEmail, "Multiple failed login attempts detected", "Email: " + email);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isRepeatedIncorrectPasswordAttempts(List<String> logs) {
        for (String log : logs) {
            if (log.contains("Authentication failed for user email")) {
                String email = extractEmailFromLog(log);
                incorrectPasswordAttempts.put(email, incorrectPasswordAttempts.getOrDefault(email, 0) + 1);

                if (incorrectPasswordAttempts.get(email) >= MAX_FAILED_ATTEMPTS && !alertSentForIncorrectPassword.getOrDefault(email, false)) {
                    alertSentForIncorrectPassword.put(email, true);
                    System.out.println("MULTIPLE INCORRECT PASSWORD ATTEMPTS: " + email + " - Sending email alert");
                    emailService.sendCriticalEventAlert(adminEmail, "Multiple incorrect password attempts detected", "Email: " + email);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBlockedUserAccessAttempt(List<String> logs) {
        for (String log : logs) {
            if (log.contains("User login failed: User is blocked email")) {
                String email = extractEmailFromLog(log);
                blockedUserAttempts.put(email, blockedUserAttempts.getOrDefault(email, 0) + 1);

                if (blockedUserAttempts.get(email) >= MAX_FAILED_ATTEMPTS && !alertSentForBlockedUser.getOrDefault(email, false)) {
                    alertSentForBlockedUser.put(email, true);
                    System.out.println("USER IS BLOCKED: " + email + " - Sending email alert");
                    emailService.sendCriticalEventAlert(adminEmail, "Blocked user access attempt", "Email: " + email);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isTokenInvalidDetected(List<String> logs) {
        for (String log : logs) {
            if (log.contains("Invalid or expired JWT token detected for user email")) {
                String email = extractEmailFromLog(log);
                invalidTokenAttempts.put(email, invalidTokenAttempts.getOrDefault(email, 0) + 1);

                if (invalidTokenAttempts.get(email) >= MAX_FAILED_ATTEMPTS && !alertSentForTokenInvalid.getOrDefault(email, false)) {
                    alertSentForTokenInvalid.put(email, true);
                    System.out.println("EXPIRED OR INVALID JWT TOKEN DETECTED: " + email + " - Sending email alert");
                    emailService.sendCriticalEventAlert(adminEmail, "Expired JWT token detected", "Email: " + email);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isUnauthorizedAccessAttempt(List<String> logs) {
        for (String log : logs) {
            if (log.contains("status=401") && log.contains("path=")) {
                String path = extractPathFromLog(log);
                unauthorizedAccessAttempts.put(path, unauthorizedAccessAttempts.getOrDefault(path, 0) + 1);

                if (unauthorizedAccessAttempts.get(path) >= MAX_FAILED_ATTEMPTS && !alertSentForUnauthorizedCriticalEvent.getOrDefault(path, false)) {
                    alertSentForUnauthorizedCriticalEvent.put(path, true);
                    System.out.println("UNAUTHORIZED ACCESS DETECTED: " + path + " - Sending email alert");
                    emailService.sendCriticalEventAlert(adminEmail, "Unauthorized access detected", "Path: " + path);
                    return true;
                }
            }
        }
        return false;
    }

    //nisam tetsirala
    private boolean isExcessiveEndpointClicks(List<String> logs) {
        for (String log : logs) {
            if (log.contains("Path: ")) {
                String path = extractPathFromLog(log);
                endpointClickAttempts.put(path, endpointClickAttempts.getOrDefault(path, 0) + 1);

                if (endpointClickAttempts.get(path) >= MAX_CLICK_ATTEMPTS && !alertSentForEndpointClicks.getOrDefault(path, false)) {
                    alertSentForEndpointClicks.put(path, true);
                    System.out.println("EXCESSIVE CLICKS DETECTED: " + path + " - Sending email alert");
                    emailService.sendCriticalEventAlert(adminEmail, "Excessive clicks on endpoint detected", "Path: " + path);
                    return true;
                }
            }
        }
        return false;
    }

    private String extractEmailFromLog(String log) {
        int startIndex = log.indexOf("email") + 6;
        int endIndex = log.indexOf(" ", startIndex);
        if (endIndex == -1) {
            endIndex = log.length();
        }
        return log.substring(startIndex, endIndex).trim();
    }

    private String extractPathFromLog(String log) {
        int startIndex = log.indexOf("path=") + 5;
        int endIndex = log.indexOf(" ", startIndex);
        if (endIndex == -1) {
            endIndex = log.length();
        }
        return log.substring(startIndex, endIndex).trim();
    }

    @Scheduled(fixedRate = 3600000) // Every hour   -- mozes smanjiti
    public void resetAlertSentMap() {
        failedLoginAttempts.clear();        //da ne bi slao stalno mejlove za obavestenje
        incorrectPasswordAttempts.clear();
        blockedUserAttempts.clear();
        invalidTokenAttempts.clear();
        unauthorizedAccessAttempts.clear();
        endpointClickAttempts.clear();
        alertSentForBlockedUser.clear();
        alertSentForIncorrectPassword.clear();
        alertSentForTokenInvalid.clear();
        alertSentForUnauthorizedCriticalEvent.clear();
        alertSentForEndpointClicks.clear();
    }
}
