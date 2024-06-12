package org.example.securityproject.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.securityproject.auth.TokenGenerator;
import org.example.securityproject.dto.AccessRefreshTokenResponseDto;
import org.example.securityproject.model.LoginToken;
import org.example.securityproject.repository.LoginTokenRepository;
import org.example.securityproject.service.LoginService;
import org.example.securityproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.example.securityproject.util.TokenUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@RestController
@RequestMapping("/api/login")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final LoginService loginService;
    private final UserService userService;
    @Autowired
    private LoginTokenRepository loginTokenRepository;
    private TokenUtils tokenUtils;



    @Autowired
    public LoginController(LoginService loginService, UserService userService, TokenUtils tokenUtils) {
        this.loginService = loginService;
        this.userService = userService;
        this.tokenUtils = tokenUtils;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody String email) throws NoSuchAlgorithmException, InvalidKeyException {
        logger.info("Sending email for email: {}", email);
        if (!userService.checkIfExists(email)) {
            String errorMessage = "User with the provided email address was not found.";
            logger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with the provided email address was not found.");
        }

        if (!userService.checkServicePackage(email)) {
            String errorMessage = "You do not have permission for passwordless login due to your service package type.";
            logger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You do not have permission for passwordless login due to your service package type.");
        }

        if(!userService.checkRole(email))
        {
            String errorMessage = "You do not have permission for passwordless login due to your role.";
            logger.error(errorMessage);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You do not have permission for paswordless login due to you role.");
        }

        loginService.sendEmail(email);
        logger.info("Email sent successfully to email: {}", email);
        return ResponseEntity.ok().build();
    }

    private boolean verifyHmac(String data, String key, String hmacToVerify) throws NoSuchAlgorithmException, InvalidKeyException {
        String generatedHmac = TokenGenerator.generateHmac(data, key);
        return hmacToVerify.equals(generatedHmac);
    }

    @GetMapping("/verify")
    public ResponseEntity<String> handleLoginRequest(@RequestParam("token") String token) throws NoSuchAlgorithmException, InvalidKeyException {
        logger.info("Handling login request for token: {}", token);
        LoginToken loginToken = loginTokenRepository.findByToken(token);
        String email = loginToken.getUsername();

        if (loginToken == null) {
            logger.error("Token not found for token: {}", token);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Token not found");
        }
        if (loginToken != null && !verifyHmac(token, "milica123", loginToken.getHmac())) {
            logger.warn("Impaired integrity for token: {}", token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Impaired integrity");
        }
        LocalDateTime expirationTime = loginToken.getExpirationTime();
        if (expirationTime.isBefore(LocalDateTime.now())) {
            logger.warn("Token expired for token: {}", token);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token expired");
        }

        loginTokenRepository.delete(loginToken);
        String clientAppUrl = "https://localhost:4200/client-homepage/" + email;
        logger.info("Redirecting to client homepage URL: {}", clientAppUrl);
        URI redirectUri = UriComponentsBuilder.fromUriString(clientAppUrl).build().toUri();
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();

    }

    @GetMapping("/tokens/{email}")
    public ResponseEntity<AccessRefreshTokenResponseDto> getTokens(@PathVariable("email") String email) throws NoSuchAlgorithmException, InvalidKeyException {
        logger.info("Generating tokens for email: {}", email);
        String accessToken = tokenUtils.generateAccessToken(email);
        String refreshToken = tokenUtils.generateRefreshToken(email);

        int accessExpiresIn = tokenUtils.getAccessExpiresIn();
        int refreshExpiresIn = tokenUtils.getRefreshExpiresIn();

        System.out.println("Access token generated: " + accessToken);
        System.out.println("Refresh token: " + refreshToken);

        logger.debug("Access token generated: {}", accessToken);
        logger.debug("Refresh token generated: {}", refreshToken);

        AccessRefreshTokenResponseDto tokensResponse = new AccessRefreshTokenResponseDto(accessToken, accessExpiresIn, refreshToken, refreshExpiresIn);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", accessToken);
        headers.add("Refresh-Token", refreshToken);

        System.out.println("Zaglavlje: " + headers);

        return ResponseEntity.ok().headers(headers).body(tokensResponse);
    }

}
