package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.PasswordDataDto;
import org.example.securityproject.dto.RegistrationRequestResponseDto;
import org.example.securityproject.dto.ResponseDto;
import org.example.securityproject.dto.UserDto;
import org.example.securityproject.enums.RegistrationStatus;
import org.example.securityproject.model.ConfirmationToken;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.ConfirmationTokenRepository;
import org.example.securityproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private ConfirmationTokenRepository confirmationTokenRepository;

    public String registerUser (UserDto userDto) {
        if (!validatePassword(userDto.getPassword())) {
           return "The password does not meet the requirements.";
        }

        User existingUser = userRepository.findByEmailAndRegistrationStatusIn(userDto.getEmail(), Arrays.asList(RegistrationStatus.PENDING, RegistrationStatus.ACCEPTED));
        if (existingUser != null) {
           return "A user with this email is already registered.";
        }

        User existingRejectedUser = userRepository.findByEmailAndRegistrationStatus(userDto.getEmail(), RegistrationStatus.REJECTED);

        if (existingRejectedUser != null && existingRejectedUser.getRequestProcessingDate() != null) {
            Date requestProcessedDate = existingRejectedUser.getRequestProcessingDate();
            LocalDateTime requestProcessedTime = LocalDateTime.ofInstant(requestProcessedDate.toInstant(), ZoneId.systemDefault());
            LocalDateTime currentTime = LocalDateTime.now();
            long minutesPassed = ChronoUnit.MINUTES.between(requestProcessedTime, currentTime);

            if (minutesPassed < 5) {
                return "It is not possible to register - your request was recently rejected.";
            }
            else {
               userRepository.delete(existingRejectedUser);
            }
        }

        User user = new User();

        user.setEmail(userDto.getEmail());
        user.setAddress(userDto.getAddress());
        user.setCity(userDto.getCity());
        user.setCountry(userDto.getCountry());
        user.setName(userDto.getName());
        user.setSurname(userDto.getSurname());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setRegistrationStatus(RegistrationStatus.PENDING);
        user.setClientType(userDto.getClientType());
        user.setRole(userDto.getRole());
        user.setServicesPackage(userDto.getServicesPackage());
        user.setRequestProcessingDate(null);

        String salt = BCrypt.gensalt();
        String hashedPassword = passwordEncoder.encode(userDto.getPassword() + salt);

        user.setPassword(hashedPassword);
        user.setSalt(salt);

        userRepository.save(user);
        return "You have successfully registered.";
    }

    private boolean validatePassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&*!]).{12,}$";
        return password.matches(passwordRegex);
    }

    public List<User> getAllRegistrationRequests() {
        return userRepository.findByRegistrationStatus(RegistrationStatus.PENDING);
    }

    public void processRegistrationRequest(RegistrationRequestResponseDto responseData) throws NoSuchAlgorithmException, InvalidKeyException {
        User user = userRepository.findByEmail(responseData.getEmail());
        if (!responseData.isAccepted()) {
            user.setRegistrationStatus(RegistrationStatus.REJECTED);
        }
        else {
            user.setRegistrationStatus(RegistrationStatus.WAITING);
        }
        user.setRequestProcessingDate(new Date());
        userRepository.save(user);
        emailService.sendRegistrationEmail(responseData);
    }

    public String confirmToken(String token) throws NoSuchAlgorithmException, InvalidKeyException {
        ConfirmationToken confirmationToken = confirmationTokenRepository.findByToken(token);
        String htmlResponse = "";

        if (confirmationToken != null) {
            boolean hmacValid = verifyHmac(token, "ana123", confirmationToken.getHmac());

            if (!hmacValid) {
                return "HMAC signature invalid";
            }

            Date createdDate = confirmationToken.getCreatedDate();
            int duration = confirmationToken.getDuration();
            Date expiryDate = calculateExpiryDate(createdDate, duration);
            User user = confirmationToken.getUser();

            if (new Date().after(expiryDate)) {
                htmlResponse = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Token Expired</title></head><body><h1>Token Expired</h1><p>The token has expired. Please register again.</p></body></html>";
                confirmationTokenRepository.delete(confirmationToken);
                userRepository.delete(user);
            }
            else {
                user.setRegistrationStatus(RegistrationStatus.ACCEPTED);
                user.setActive(true);
                userRepository.save(user);
                confirmationTokenRepository.delete(confirmationToken);
                htmlResponse = "<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"><title>Confirmation Success</title></head><body><h1>Congratulations!</h1><p>You have successfully confirmed your registration. You can now log in.</p></body></html>";
            }
            return htmlResponse;
        } else {
            return"Token not found";
        }
    }
    private boolean verifyHmac(String data, String key, String hmacToVerify) throws NoSuchAlgorithmException, InvalidKeyException {
        String generatedHmac = generateHmac(data, key);
        return hmacToVerify.equals(generatedHmac);
    }

    private String generateHmac(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hmacData = sha256Hmac.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(hmacData);
    }

    private Date calculateExpiryDate(Date createdDate, int duration) {
        long expiryTimeMillis = createdDate.getTime() + (duration * 60 * 1000);
        return new Date(expiryTimeMillis);
    }

    public User getUserData() {
        return userRepository.findByEmail("anaa.radovanovic2001@gmail.com");
    }

    public String updateUserPassword(PasswordDataDto passwordData) {
        if (!checkOldPassword(passwordData.getOldPassword())) {
            return "You have not entered a good current password.";
        }
        if (!passwordData.getNewPassword().equals(passwordData.getConfirmedNewPassword())) {
            return "The new password and confirm password do not match.";
        }
        if (!validatePassword(passwordData.getNewPassword())) {
            return "The password does not meet the requirements.";
        }

        //ovde bi trebalo da znam koji user je ulogovan i njega da izvucem iz baze
        //za sad zakucam sa emailom, pa cemo videti kad budemo dobavljali ulogovanog usera
        User user = userRepository.findByEmail("anaa.radovanovic2001@gmail.com");

        String salt = BCrypt.gensalt();
        String hashedNewPassword = passwordEncoder.encode(passwordData.getNewPassword() + salt);

        user.setPassword(hashedNewPassword);
        user.setSalt(salt);

        userRepository.save(user);

        return "Password successfully changed.";
    }

    private boolean checkOldPassword(String oldPassword) {
        //ovde bi trebalo da znam koji user je ulogovan i njega da izvucem iz baze
        //za sad zakucam sa emailom, pa cemo videti kad budemo dobavljali ulogovanog usera
        User user = userRepository.findByEmail("anaa.radovanovic2001@gmail.com");
        String salt = user.getSalt();
        String hashedOldPassword = passwordEncoder.encode(oldPassword + salt);
        return passwordEncoder.matches(hashedOldPassword, user.getPassword());
    }
}
//kada hocu da proverim da li mi je korisnik uneo dobru lozinku
//onda uzmem njehovu lozinku, uzmem salt koji imam u bazi spojim ih HESIRAM i poredim onda HESOVE


//PREPORUKE ZA LOZINKE:
//-minimum 12 karaktera
//-bar jedno malo slovo
//-bar jedno veliko slovo
//-bar jedan specijalan karakter
//-bar jedan broj