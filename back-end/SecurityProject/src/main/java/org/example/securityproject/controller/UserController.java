package org.example.securityproject.controller;

import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.example.securityproject.dto.*;
import org.example.securityproject.enums.Permission;
import org.example.securityproject.enums.UserRole;
import org.example.securityproject.model.ConfirmationToken;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.ConfirmationTokenRepository;
import org.example.securityproject.repository.UserRepository;
import org.example.securityproject.service.UserService;
import org.example.securityproject.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/users")
public class UserController {
    @Autowired
    private UserService userService;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private UserRepository userRepository;

    //TEST DA LI ROLE MOGU DA SE MENJAJU IZ KODA

    @PostMapping("/editUserRole")
    public String editUserRole() {
       User user = userRepository.findByEmail("anaa.radovanovic2001@gmail.com");
       int userId = user.getId();

       List<UserRole> roles = user.getRoles();

       roles.add(UserRole.CLIENT);

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
    public ResponseEntity<LoginReponseDto> loginUser(@RequestBody UserLoginData loginData) {
        return new ResponseEntity<>(userService.loginUser(loginData), HttpStatus.OK);
    }

    @PostMapping("/registerUser")
    public ResponseEntity<ResponseDto> registerUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.registerUser(userDto), HttpStatus.OK);
    }

    @GetMapping("/confirm-account")
    public String confirm(@RequestParam("token") String token) throws NoSuchAlgorithmException, InvalidKeyException {
        return userService.confirmToken(token);
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

    @PutMapping("/updatePassword")
    public ResponseEntity<ResponseDto> updateUserPassword (@RequestBody PasswordDataDto passwordDataDto) throws NoSuchAlgorithmException {
        ResponseDto response = new ResponseDto();
        response.setResponseMessage(userService.updateUserPassword(passwordDataDto));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/updateAdminPassword")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public ResponseEntity<ResponseDto> updateAdminPassword (@RequestBody PasswordDataDto passwordDataDto) throws NoSuchAlgorithmException {
        ResponseDto response = new ResponseDto();
        response.setResponseMessage(userService.updateUserPassword(passwordDataDto));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/findUserByEmail/{email}")
    public ResponseEntity<UserDto> findUserByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            UserDto userDto = new UserDto(user);
            return new ResponseEntity<>(userDto, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/updateClient")
    public ResponseEntity<String> updateClient(@RequestBody UserDto userDto) {
        userService.updateUser(userDto);
        return new ResponseEntity<>("User updated successfully", HttpStatus.OK);
    }

}
