package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.RegistrationRequestResponseDto;
import org.example.securityproject.dto.UserDto;
import org.example.securityproject.enums.RegistrationStatus;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private UserRepository userRepository;
    private EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    public void registerUser (UserDto userDto) {
        if (!validatePassword(userDto.getPassword())) {
            throw new IllegalArgumentException("The password does not meet the requirements.");
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

        String salt = BCrypt.gensalt();
        String hashedPassword = passwordEncoder.encode(userDto.getPassword() + salt);

        user.setPassword(hashedPassword);
        user.setSalt(salt);

        userRepository.save(user);
    }

    private boolean validatePassword(String password) {
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&*!]).{12,}$";
        return password.matches(passwordRegex);
    }

    public List<User> getAllRegistrationRequests() {
        return userRepository.findByRegistrationStatus(RegistrationStatus.PENDING);
    }

    public void processRegistrationRequest(RegistrationRequestResponseDto responseData) {
        User user = userRepository.findByEmail(responseData.getEmail());
        if (!responseData.isAccepted()) {
           user.setRegistrationStatus(RegistrationStatus.REJECTED);
        }
        else {
            user.setRegistrationStatus(RegistrationStatus.ACCEPTED);
        }
        userRepository.save(user);
        emailService.sendRegistrationEmail(responseData);
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