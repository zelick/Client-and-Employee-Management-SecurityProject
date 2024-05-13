package org.example.securityproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class LoginService {

    @Autowired
    private EmailService emailService;

    public void sendEmail(String email) {
        // Ovde pozovite sendPasswordlessMail metodu iz EmailService
        emailService.sendPasswordlessMail(email);
    }

    private String generateToken() {
        // Implementacija generisanja tokena
        // Ovde ćete implementirati kod za generisanje jednokratnog tokena
        // Na primer, možete koristiti neku od biblioteka za generisanje JWT tokena
        return "random-token"; // Dummy token za testiranje
    }
}
