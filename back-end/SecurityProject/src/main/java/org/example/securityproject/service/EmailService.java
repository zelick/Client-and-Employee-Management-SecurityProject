package org.example.securityproject.service;

//import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.example.securityproject.auth.TokenGenerator;
import org.example.securityproject.dto.RegistrationRequestResponseDto;
import org.example.securityproject.model.LoginToken;
import org.example.securityproject.repository.LoginTokenRepository;
import org.example.securityproject.model.ConfirmationToken;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.ConfirmationTokenRepository;
import org.example.securityproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
@AllArgsConstructor
public class EmailService {

//    @Value("${security.project.secret}")
//    private String SECRET_KEY;
    @Autowired
    private JavaMailSender javaMailSender;
    private LoginTokenRepository loginTokenRepository;
    private UserRepository userRepository;
    private ConfirmationTokenRepository confirmationTokenRepository;

    public void sendRegistrationEmail(RegistrationRequestResponseDto responseData) throws NoSuchAlgorithmException, InvalidKeyException {
        String userEmail = responseData.getEmail();
        String subject = "";
        String text = "";

        if (responseData.isAccepted()) {
            subject = "Confirmation of Registration";
            text = "Your registration request has been accepted." +
                    "To confirm your account, please click here: " +
                    "https://localhost:443/api/users/confirm-account?token=" + generateToken(userEmail);
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

    public void sendPasswordlessMail(String email) throws NoSuchAlgorithmException, InvalidKeyException {
        LoginToken objectToken = TokenGenerator.generateToken(email);
        loginTokenRepository.save(objectToken);
        String userEmail = email;
        String subject = "Passwordless login";
        String text = "Click on the following link to login: https://localhost:443/api/login/verify?token=" + objectToken.getToken();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("aplikacijemobilnea0gmail.com");
        message.setTo(userEmail);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);
    }
    /*
    private String generateToken(String userEmail) {
        User user = userRepository.findByEmail(userEmail);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);
        confirmationTokenRepository.save(confirmationToken);

        return confirmationToken.getToken();
    }

     */

    private String generateToken(String userEmail) throws NoSuchAlgorithmException, InvalidKeyException {
        User user = userRepository.findByEmail(userEmail);

        ConfirmationToken confirmationToken = new ConfirmationToken(user);

        String token = confirmationToken.getToken();

        String hmac = generateHmac(token, "ana123"); //izmena
        //String hmac = generateHmac(token, SECRET_KEY);
        confirmationToken.setHmac(hmac);
        confirmationTokenRepository.save(confirmationToken);

        return token;
    }

    private String generateHmac(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hmacData = sha256Hmac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacData);
    }
}