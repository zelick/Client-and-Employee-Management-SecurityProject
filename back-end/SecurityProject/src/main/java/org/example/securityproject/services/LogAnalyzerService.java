package org.example.securityproject.services;

import lombok.AllArgsConstructor;
import org.example.securityproject.model.Notification;
import org.example.securityproject.repository.NotificationRepository;
import org.example.securityproject.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LogAnalyzerService {

    @Autowired
    private EmailService emailService;
    @Autowired
    private NotificationRepository notificationRepository;  //sacuvaj notifikaciju

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int MAX_CLICK_ATTEMPTS = 30;
    private static final int MAX_JWT_EXPIRED_ATTEMPTS = 3;

    private Map<String, Integer> failedLoginAttempts = new ConcurrentHashMap<>();
    private Map<String, Integer> incorrectPasswordAttempts = new ConcurrentHashMap<>();
    private Map<String, Integer> blockedUserAttempts = new ConcurrentHashMap<>();
    private Map<String, Integer> invalidTokenAttempts = new ConcurrentHashMap<>();
    private Map<String, Integer> unauthorizedAccessAttempts = new ConcurrentHashMap<>();
    private Map<String, Integer> endpointClickAttempts = new ConcurrentHashMap<>();

    private Map<String, Boolean> alertSentForBlockedUser = new ConcurrentHashMap<>();
    private Map<String, Boolean> alertSentForIncorrectUsername = new ConcurrentHashMap<>();
    private Map<String, Boolean> alertSentForIncorrectPassword = new ConcurrentHashMap<>();
    private Map<String, Boolean> alertSentForTokenInvalid = new ConcurrentHashMap<>();
    private Map<String, Boolean> alertSentForUnauthorizedCriticalEvent = new ConcurrentHashMap<>();
    private Map<String, Boolean> alertSentForEndpointClicks = new ConcurrentHashMap<>();

    private String adminEmail = "zelickika@gmail.com";  //mozda izmena u bazi..

    public void analyzeLogs(List<String> logs) {
        boolean criticalEventDetected = false;

        if (isRepeatedFailedLogins(logs)) {
            criticalEventDetected = true;
        }

        if (isRepeatedIncorrectPasswordAttempts(logs)) {
            criticalEventDetected = true;
        }

        if (isBlockedUserAccessAttempt(logs)) {
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

    //kada vise puta promasi username, ili ako ga admin nije odobrio - 5 puta ili vise se desio log
    private boolean isRepeatedFailedLogins(List<String> logs) {
        for (String log : logs) {
            if (log.contains("User login failed: User not found for email ") || log.contains("User login failed: Account is not active for email ")) {
                String email = extractEmailFromLog(log);
                failedLoginAttempts.put(email, failedLoginAttempts.getOrDefault(email, 0) + 1);     //trazi kljuc u mapi, ne salje vise puta email

                if (failedLoginAttempts.get(email) >= MAX_FAILED_ATTEMPTS && !alertSentForIncorrectUsername.getOrDefault(email, false)) {
                    alertSentForIncorrectUsername.put(email, true);
                    System.out.println("---------MULTIPLE INCORRECT USERNAME ATTEMPTS: " + email + " - Sending email alert------");
                    emailService.sendCriticalEventAlert(adminEmail, "Multiple failed login attempts detected", "Email: " + email);
                    createNotificationAndSave("Multiple failed login attempts detected Email: " + email);
                    return true;
                }
            }
        }
        return false;
    }

    //korisnik vise je 5 ili vise puta promasio lozinku
    private boolean isRepeatedIncorrectPasswordAttempts(List<String> logs) {
        for (String log : logs) {
            if (log.contains("Authentication failed for user email ")) {
                String email = extractEmailFromLog(log);
                incorrectPasswordAttempts.put(email, incorrectPasswordAttempts.getOrDefault(email, 0) + 1);

                if (incorrectPasswordAttempts.get(email) >= MAX_FAILED_ATTEMPTS && !alertSentForIncorrectPassword.getOrDefault(email, false)) {
                    alertSentForIncorrectPassword.put(email, true);
                    System.out.println("MULTIPLE INCORRECT PASSWORD ATTEMPTS: " + email + " - Sending email alert");
                    emailService.sendCriticalEventAlert(adminEmail, "Multiple incorrect password attempts detected", "Email: " + email);
                    createNotificationAndSave("Multiple incorrect password attempts detected Email: " + email);
                    return true;
                }
            }
        }
        return false;
    }

    //ako blokiran korisnik pokusa da se prijavi 5 ili vise puta
    private boolean isBlockedUserAccessAttempt(List<String> logs) {
        for (String log : logs) {
            if (log.contains("User login failed: User is blocked email ")) {
                String email = extractEmailFromLog(log);
                blockedUserAttempts.put(email, blockedUserAttempts.getOrDefault(email, 0) + 1);

                if (blockedUserAttempts.get(email) >= MAX_FAILED_ATTEMPTS && !alertSentForBlockedUser.getOrDefault(email, false)) {
                    alertSentForBlockedUser.put(email, true);
                    System.out.println("USER IS BLOCKED: " + email + " - Sending email alert");
                    emailService.sendCriticalEventAlert(adminEmail, "Blocked user access attempt", "Email: " + email);
                    createNotificationAndSave("Blocked user access attempt Email: " + email);
                    return true;
                }
            }
        }
        return false;
    }

    //pokusaj 3 puta pogadja putanju, korisnik kome je istekao JWT token
    private boolean isTokenInvalidDetected(List<String> logs) {
        for (String log : logs) {
            if (log.contains("Invalid or expired JWT token detected for user email ")) {
                String email = extractEmailFromLog(log);
                invalidTokenAttempts.put(email, invalidTokenAttempts.getOrDefault(email, 0) + 1);

                if (invalidTokenAttempts.get(email) >= MAX_JWT_EXPIRED_ATTEMPTS && !alertSentForTokenInvalid.getOrDefault(email, false)) {
                    alertSentForTokenInvalid.put(email, true);
                    System.out.println("EXPIRED OR INVALID JWT TOKEN DETECTED: " + email + " - Sending email alert");
                    emailService.sendCriticalEventAlert(adminEmail, "Expired JWT token detected", "Email: " + email);
                    createNotificationAndSave("Expired JWT token detected Email: " + email);
                    return true;
                }
            }
        }
        return false;
    }

    //ako neautorizovan koristnik 401 , pokusa da 5 ili vise puta da prisutpi putanji
    private boolean isUnauthorizedAccessAttempt(List<String> logs) {
        for (String log : logs) {
            if (log.contains("status=401") && log.contains("path=")) {
                String path = extractPathFromLog(log);
                unauthorizedAccessAttempts.put(path, unauthorizedAccessAttempts.getOrDefault(path, 0) + 1);
//                System.out.println("------PUTANJA-------------: " + path);
                if (unauthorizedAccessAttempts.get(path) >= MAX_FAILED_ATTEMPTS && !alertSentForUnauthorizedCriticalEvent.getOrDefault(path, false)) {
                    alertSentForUnauthorizedCriticalEvent.put(path, true);
                    System.out.println("UNAUTHORIZED ACCESS DETECTED: " + path + " - Sending email alert");
                    emailService.sendCriticalEventAlert(adminEmail, "Unauthorized access detected", "Path: " + path);
                    createNotificationAndSave("Unauthorized access detected Path: " + path);
                    return true;
                }
            }
        }
        return false;
    }

    //nisam tetsirala
    //lupila sam kriticni dogadjaj 30 puta u minuti klikne nesto - ZA ISTU PUTANJU ZAHTEVA
    //30 PUTA.. tesko testirati
//    private boolean isExcessiveEndpointClicks(List<String> logs) {
//        for (String log : logs) {
//            if (log.contains("path=")) {
//                String path = extractPathFromLog(log);
//                endpointClickAttempts.put(path, endpointClickAttempts.getOrDefault(path, 0) + 1);
//                System.out.println("-----------Brojac:-------------- " + endpointClickAttempts.get(path) );
//                if (endpointClickAttempts.get(path) >= MAX_CLICK_ATTEMPTS && !alertSentForEndpointClicks.getOrDefault(path, false)) {
//                    alertSentForEndpointClicks.put(path, true);
//                    System.out.println("EXCESSIVE CLICKS DETECTED: " + path + " - Sending email alert");
//                    emailService.sendCriticalEventAlert(adminEmail, "Excessive clicks on endpoint detected", "Path: " + path);
//                    createNotificationAndSave("Excessive clicks on endpoint detected Path: " + path);
//                    return true;
//                }
//            }
//        }
//        return false;
//    }


    private String extractPathFromLogEndpoint(String log) {
        // Assuming logs contain path information in a consistent format
        int pathStart = log.indexOf("GET \"") + 5;
        if (pathStart < 5) {
            pathStart = log.indexOf("POST \"") + 6;
        }
        if (pathStart < 6) {
            pathStart = log.indexOf("PUT \"") + 5;
        }
        int pathEnd = log.indexOf("\"", pathStart);
        if (pathStart >= 0 && pathEnd > pathStart) {
            return log.substring(pathStart, pathEnd);
        }
        return "";
    }

    public boolean isExcessiveEndpointClicks(List<String> logs) {
        for (String log : logs) {
            if (log.contains("GET \"") || log.contains("POST \"") || log.contains("PUT \"")) {
                String path = extractPathFromLogEndpoint(log);
                if (!path.isEmpty() && !path.equals("/api/auth/check-token") && !path.equals("/error")) {
                    endpointClickAttempts.put(path, endpointClickAttempts.getOrDefault(path, 0) + 1);
//                    System.out.println("-----------Brojac za " + path + ":-------------- " + endpointClickAttempts.get(path));
                    if (endpointClickAttempts.get(path) >= MAX_CLICK_ATTEMPTS && !alertSentForEndpointClicks.getOrDefault(path, false)) {
                        alertSentForEndpointClicks.put(path, true);
                        System.out.println("EXCESSIVE CLICKS DETECTED: " + path + " - Sending email alert");
                        emailService.sendCriticalEventAlert(adminEmail, "Excessive clicks on endpoint detected", "Path: " + path);
                        createNotificationAndSave("Excessive clicks on endpoint detected Path: " + path);
                        return true;
                    }
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

    //@Scheduled(fixedRate = 20000) // Every 20 seconds
    @Scheduled(fixedRate = 3600000) // Every hour   -- mozes smanjiti
    public void resetAlertSentMap() {
        //da ne bi slao stalno mejlove za obavestenje
        failedLoginAttempts.clear();
        alertSentForIncorrectUsername.clear();
        //
        incorrectPasswordAttempts.clear();
        alertSentForIncorrectPassword.clear();
        //
        blockedUserAttempts.clear();
        alertSentForBlockedUser.clear();
        //
        invalidTokenAttempts.clear();
        alertSentForTokenInvalid.clear();
        //
        unauthorizedAccessAttempts.clear();
        alertSentForUnauthorizedCriticalEvent.clear();
        //
        endpointClickAttempts.clear();
        alertSentForEndpointClicks.clear();
    }

    private void createNotificationAndSave(String content) {
        Notification notification = new Notification();
        notification.setMessage(content);
        try {
            notificationRepository.save(notification);
            System.out.println("Notification saved successfully: " + notification);
        } catch (Exception e) {
            System.err.println("Failed to save notification: " + e.getMessage());
        }
    }
}
