package org.example.securityproject.service;

import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.example.securityproject.dto.RegistrationRequestResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    public void sendRegistrationEmail(RegistrationRequestResponseDto responseData) {
        String userEmail = responseData.getEmail();
        String subject = "";
        String text = "";

        if (responseData.isAccepted()) {
            subject = "Confirmation of Registration";
            text = "Your registration request has been accepted.";
        } else {
            subject = "Refusal of registration";
            text = "Your registration request has been rejected.\n" +
                    "Reason: \n" +
                    responseData.getReason();
        }
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("apliakcijemobilnea0gmail.com");
        message.setTo(userEmail);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }
}