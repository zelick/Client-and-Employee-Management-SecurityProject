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
    private Map<String, Boolean> alertSentForBlockedUser = new ConcurrentHashMap<>();
    private Map<String, Boolean> alertSentForIncorrectPassword = new ConcurrentHashMap<>();

    public void analyzeLogs(List<String> logs) {
        boolean criticalEventDetected = false;

        if (isRepeatedFailedLogins(logs)) {
            criticalEventDetected = true;
            // emailService.sendCriticalEventAlert("admin@example.com", "Multiple failed login attempts detected");
        }

        if (isBlockedUserAccessAttempt(logs)) {
            criticalEventDetected = true;
        }

        if (isRepeatedIncorrectPasswordAttempts(logs)) {
            criticalEventDetected = true;
        }

        if (criticalEventDetected) {
            System.out.println("Critical event detected in logs");
        }
    }

    private boolean isRepeatedFailedLogins(List<String> logs) {
        Map<String, Integer> failedLoginAttempts = new HashMap<>();

        for (String log : logs) {
            if (log.contains("User login failed: User not found for email") || log.contains("User login failed: Account is not active for email")) {
                String email = extractEmailFromLog(log);
                failedLoginAttempts.put(email, failedLoginAttempts.getOrDefault(email, 0) + 1);
            }
        }

        return failedLoginAttempts.values().stream().anyMatch(attempts -> attempts >= MAX_FAILED_ATTEMPTS);
    }

    private boolean isRepeatedIncorrectPasswordAttempts(List<String> logs) {
        for (String log : logs) {
            if (log.contains("Authentication failed for user email")) {
                String email = extractEmailFromLog(log);

                if (!alertSentForIncorrectPassword.getOrDefault(email, false)) {
                    alertSentForIncorrectPassword.put(email, true);
                    System.out.println("MULTIPLE INCORRECT PASSWORD ATTEMPTS: " + email + " - Sending email alert");
                    // emailService.sendCriticalEventAlert("admin@example.com", "Multiple incorrect password attempts detected for email " + email);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isBlockedUserAccessAttempt(List<String> logs) {
        boolean blockedUserDetected = false;

        for (String log : logs) {
            if (log.contains("User login failed: User is blocked email")) {
                String email = extractEmailFromLog(log);

                if (!alertSentForBlockedUser.getOrDefault(email, false)) {
                    alertSentForBlockedUser.put(email, true);
                    System.out.println("USER IS BLOCKED: " + email + " - Sending email alert");
                    // emailService.sendCriticalEventAlert("admin@example.com", "User login failed: User is blocked email " + email);
                    blockedUserDetected = true;
                }
            }
        }

        return blockedUserDetected;
    }

    private String extractEmailFromLog(String log) {
        int startIndex = log.indexOf("email") + 6;
        int endIndex = log.indexOf(" ", startIndex);
        if (endIndex == -1) {
            endIndex = log.length();
        }
        return log.substring(startIndex, endIndex).trim();
    }

    @Scheduled(fixedRate = 3600000) // Every hour
    public void resetAlertSentMap() {
        alertSentForBlockedUser.clear();
        alertSentForIncorrectPassword.clear(); // Resetovanje mape za poslate alerte za neispravne šifre
    }
}
