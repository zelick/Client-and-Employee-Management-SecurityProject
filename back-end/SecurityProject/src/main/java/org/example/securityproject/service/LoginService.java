package org.example.securityproject.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class LoginService {

    public String sendEmail(String email) {
       return "s";
    }

    private String generateToken() {
        // Implementacija generisanja tokena
        // Ovde ćete implementirati kod za generisanje jednokratnog tokena
        // Na primer, možete koristiti neku od biblioteka za generisanje JWT tokena
        return "random-token"; // Dummy token za testiranje
    }
}
