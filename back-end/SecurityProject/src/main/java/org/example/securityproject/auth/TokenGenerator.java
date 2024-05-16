package org.example.securityproject.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.naming.factory.SendMailFactory;
import org.example.securityproject.model.LoginToken;
import org.example.securityproject.repository.LoginTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

public class TokenGenerator {
    public static LoginToken generateToken(String email) throws NoSuchAlgorithmException, InvalidKeyException {
        UUID uuid = UUID.randomUUID();
        String token = uuid.toString();
        LocalDateTime expirationTime = LocalDateTime.now().plus(10, ChronoUnit.MINUTES);
        String hmac = generateHmac(token, "milica123");
        LoginToken loginToken = new LoginToken(null, token, expirationTime, false, hmac, email);
        saveTokenToDatabase(token, expirationTime);
        return loginToken;
    }

    private static void saveTokenToDatabase(String token, LocalDateTime expirationTime) {
        System.out.println("Token: " + token + " saved with expiration time: " + expirationTime);
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {
        //LoginToken token = generateToken(email);
        //System.out.println("Generated token: " + token);
    }

    public static String generateHmac(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hmacData = sha256Hmac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacData);
    }
}
