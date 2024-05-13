package org.example.securityproject.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.example.securityproject.model.LoginToken;
import org.example.securityproject.repository.LoginTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

public class TokenGenerator {
    public static LoginToken generateToken() {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
        Date expirationDate = Date.from(expirationTime.atZone(ZoneId.systemDefault()).toInstant());

        String token = Jwts.builder()
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, "milica")
                .compact();
        LoginToken loginToken = new LoginToken(null, token, expirationTime, false);
        saveTokenToDatabase(token, expirationTime);
        return loginToken;
    }

    private static void saveTokenToDatabase(String token, LocalDateTime expirationTime) {
        System.out.println("Token: " + token + " saved with expiration time: " + expirationTime);
    }

    public static void main(String[] args) {
        LoginToken token = generateToken();
        System.out.println("Generated token: " + token);
    }
}
