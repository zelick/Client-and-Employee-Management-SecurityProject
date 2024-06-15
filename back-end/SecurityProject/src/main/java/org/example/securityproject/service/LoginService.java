package org.example.securityproject.service;

import org.example.securityproject.model.LoginToken;
import org.example.securityproject.repository.LoginTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Service
public class LoginService {

    @Autowired
    private EmailService emailService;

    public void sendEmail(String email) throws NoSuchAlgorithmException, InvalidKeyException {
        emailService.sendPasswordlessMail(email);
    }

    public void sendResetPasswordMail(String email) throws NoSuchAlgorithmException, InvalidKeyException {
        emailService.sendResetPasswordMail(email);
    }

    private String generateToken() {
        return "random-token"; // Dummy token za testiranje
    }
}
