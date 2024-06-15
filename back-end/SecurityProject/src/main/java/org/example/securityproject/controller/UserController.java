package org.example.securityproject.controller;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.*;
import org.example.securityproject.enums.Permission;
import org.example.securityproject.enums.UserRole;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.ConfirmationTokenRepository;
import org.example.securityproject.repository.UserRepository;
import org.example.securityproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private UserRepository userRepository;


    //TEST DA LI ROLE MOGU DA SE MENJAJU IZ KODA

    @PostMapping("/editUserRole")
    public String editUserRole() {
       User user = userRepository.findByEmail("pmilica990@gmail.com");
       int userId = user.getId();

       List<UserRole> roles = new ArrayList<>();
       roles.add(UserRole.ADMINISTRATOR);

       roles.remove(UserRole.CLIENT);

       user.setRoles(roles);

       userRepository.save(user);
       return "VEOMA USPESNO JEES";
    }

    @PostMapping("/editUserPermission")
    public String addPermissionToUser() {
        // Pronađi korisnika
        User user = userRepository.findByEmail("anaa.radovanovic2001@gmail.com");

        List<UserRole> userRoles = user.getRoles();

        UserRole userRole = UserRole.ADMINISTRATOR; // Na primer, uzimamo ADMINISTRATOR rolu

        userRole.addPermission(Permission.ADMIN_READ); // Dodajemo permisiju ADMIN_READ

        userRole.removePermission(Permission.ADMIN_DELETE); // Uklanjamo permisiju ADMIN_DELETE

        userRepository.save(user);

        return "PERMISIJA USPEŠNO DODATA KORISNIČKOJ ROLI";
    }

    @PostMapping("/tryLogin")
    public ResponseEntity<LoginReponseDto> loginUser(@RequestBody UserLoginData loginData)  {
        return new ResponseEntity<>(userService.loginUser(loginData), HttpStatus.OK);
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<LoginReponseDto> resetPassword(@RequestBody UserLoginData loginData)  {
        return new ResponseEntity<>(userService.resetPassword(loginData), HttpStatus.OK);
    }

    @PostMapping("/registerUser")
    public ResponseEntity<ResponseDto> registerUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.OK);
    }

    @GetMapping("/confirm-account")
    public String confirm(@RequestParam("token") String token) throws NoSuchAlgorithmException, InvalidKeyException {
        return userService.confirmToken(token);
    }

    @GetMapping("/httpsMessage")
    public String httpMessage() {
        return "USPESNO!";
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<ResponseDto> updateUserPassword (@RequestBody PasswordDataDto passwordDataDto) throws NoSuchAlgorithmException {
        ResponseDto response = new ResponseDto();
        response.setResponseMessage(userService.updateUserPassword(passwordDataDto));
        logger.info("Password update response: {}", response.getResponseMessage());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/deleteUserData/{email}")
    public ResponseEntity<ResponseDto> deleteUserData(@PathVariable String email) {
        try {
            userService.deleteUserDataByEmail(email);
            ResponseDto response = new ResponseDto();
            response.setResponseMessage("You have successfully delete all data.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            ResponseDto response = new ResponseDto();
            response.setResponseMessage("Failed to delete all data.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/getLoggedInUser")
    public ResponseEntity<UserDto> getLogegdInUser() {
        logger.info("Fetching logged in user details.");
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            logger.warn("No logged in user found.");
            return ResponseEntity.notFound().build(); // Vrati 404 Not Found ako korisnik nije prijavljen
        }
        logger.info("Logged in user found: {}", loggedInUser.getEmail());
        UserDto userDto = new UserDto(loggedInUser);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/updateClient")
    public ResponseEntity<String> updateClient(@RequestBody UserDto userDto) {
        userService.updateUser(userDto);
        return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
    }

    @GetMapping("/findUserByEmail/{email}")
    public ResponseEntity<UserDto> findUserByEmail(@PathVariable String email) {
        logger.debug("Fetching user details by email: {}", email);
        User user = userRepository.findByEmail(email);
        if (user != null) {
            logger.info("User found with email {}: {}", email, user);
            UserDto userDto = new UserDto(user);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } else {
            logger.warn("User not found with email: {}", email);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
