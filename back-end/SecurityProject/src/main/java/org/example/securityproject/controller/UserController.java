package org.example.securityproject.controller;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.*;
import org.example.securityproject.enums.Permission;
import org.example.securityproject.enums.UserRole;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.ConfirmationTokenRepository;
import org.example.securityproject.repository.UserRepository;
import org.example.securityproject.service.UserDataEncryptionService;
import org.example.securityproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/users")
public class UserController {
    @Autowired
    private UserService userService;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private UserRepository userRepository;
    private UserDataEncryptionService userDataEncryptionService;

    //TEST DA LI ROLE MOGU DA SE MENJAJU IZ KODA

    @PostMapping("/editUserRole")
    public String editUserRole() {
       User user = userRepository.findByEmail("HVFOd+SCu8hlKStqkdYEcvwIwlDygx2Bb0FJo3NKlot3YZZLB5TAwpoqcyrOBYBoffwJdVaWvHOzpS8g+BoSuOfq/XL4QrWoymy3+d2CaZY=");
       int userId = user.getId();

       List<UserRole> roles = user.getRoles();

       roles.remove(UserRole.CLIENT);
       //roles.add(UserRole.EMPLOYEE);

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
    public ResponseEntity<LoginReponseDto> loginUser(@RequestBody UserLoginData loginData) throws Exception {
        return new ResponseEntity<>(userService.loginUser(loginData), HttpStatus.OK);
    }

    @PostMapping("/registerUser")
    public ResponseEntity<RegistrationResponseDto> registerUser(@RequestBody UserDto userDto) throws Exception {
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
    public ResponseEntity<ResponseDto> updateUserPassword (@RequestBody PasswordDataDto passwordDataDto) throws Exception {
        ResponseDto response = new ResponseDto();
        response.setResponseMessage(userService.updateUserPassword(passwordDataDto));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getLoggedInUser")
    public ResponseEntity<UserDto> getLogegdInUser() {
        User loggedInUser = userService.getLoggedInUser();
        if (loggedInUser == null) {
            return ResponseEntity.notFound().build(); // Vrati 404 Not Found ako korisnik nije prijavljen
        }

        UserDto userDto = new UserDto(loggedInUser);
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/updateClient")
    public ResponseEntity<String> updateClient(@RequestBody UserDto userDto) throws Exception {
        userService.updateUser(userDto);
        return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
    }


    //NAPOMENA!!!
    @GetMapping("/findUserByEmail/{email}")
    public ResponseEntity<UserDto> findUserByEmail(@PathVariable String email) throws Exception {
        //User user = userRepository.findByEmail(email);
        User user = userDataEncryptionService.findEncryptedUserByEmail(email);

        //OVO AKO SE KORISTI ZA PRIKAZ NEKIH INFORMACIJA POTREBNO JE DEKRIPTOVATI --- VIDETI

        if (user != null) {
            UserDto userDto = new UserDto(user);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ResponseDto> verifyCode(@RequestBody VerificationRequestDto verificationRequest) throws Exception {
        return new ResponseEntity<>(userService.verifyCode(verificationRequest), HttpStatus.OK);
    }

    @PostMapping("/verifyReCaptchaToken")
    public ResponseEntity<ResponseDto> verifyReCaptchaToken(@RequestBody VerificationReCaptchaRequestDto verificationRequest)
    {
        return new ResponseEntity<>(userService.verifyReCaptchaToken(verificationRequest), HttpStatus.OK);
    }
}
