package org.example.securityproject.controller;

import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.example.securityproject.dto.*;
import org.example.securityproject.model.ConfirmationToken;
import org.example.securityproject.model.User;
import org.example.securityproject.repository.ConfirmationTokenRepository;
import org.example.securityproject.repository.UserRepository;
import org.example.securityproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/users")
public class UserController {
    @Autowired
    private UserService userService;
    private ConfirmationTokenRepository confirmationTokenRepository;
    private UserRepository userRepository;

    @PostMapping("/registerUser")
    public ResponseEntity<ResponseDto> registerUser(@RequestBody UserDto userDto) {
        ResponseDto response = new ResponseDto();
        response.setResponseMessage(userService.registerUser(userDto));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getAllRegistrationRequests")
    public ResponseEntity<List<UserDto>> getAllRegistrationRequests() {
        try {
            List<UserDto> userDtos = userService.getAllRegistrationRequests()
                    .stream()
                    .map(user -> new UserDto(user))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/processRegistrationRequest")
    public ResponseEntity<Map<String, String>> processRegistrationRequest(@RequestBody RegistrationRequestResponseDto responseData) {
        try {
            userService.processRegistrationRequest(responseData);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Registration request successfully processed");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to process registration request: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/confirm-account")
    public String confirm(@RequestParam("token") String token) throws NoSuchAlgorithmException, InvalidKeyException {
        return userService.confirmToken(token);
    }

    //OVO CEMO IZMENITI KADA BUDEMO IMALI JWT
    @GetMapping("/getUserData")
    public ResponseEntity<UserDto> getUserData() {
        UserDto userDto = new UserDto(userService.getUserData());
        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/updatePassword")
    public ResponseEntity<ResponseDto> updateUserPassword (@RequestBody PasswordDataDto passwordDataDto) throws NoSuchAlgorithmException {
        ResponseDto response = new ResponseDto();
        response.setResponseMessage(userService.updateUserPassword(passwordDataDto));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/updateUserData")
    public ResponseEntity<ResponseDto> updateUserData (@RequestBody EditAdminDto adminDto) {
        ResponseDto response = new ResponseDto();
        response.setResponseMessage(userService.updateUserData(adminDto));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getAllEmployees")
    public ResponseEntity<List<UserDto>> getAllEmployees() {
        try {
            List<UserDto> userDtos = userService.getAllEmployees()
                    .stream()
                    .map(user -> new UserDto(user))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAllClients")
    public ResponseEntity<List<UserDto>> getAllClients() {
        try {
            List<UserDto> userDtos = userService.getAllClients()
                    .stream()
                    .map(user -> new UserDto(user))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
