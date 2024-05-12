package org.example.securityproject.controller;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.RegistrationRequestResponseDto;
import org.example.securityproject.dto.UserDto;
import org.example.securityproject.model.User;
import org.example.securityproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.cert.X509Certificate;
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

    @PostMapping("/registerUser")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserDto userDto) {
        try {
            userService.registerUser(userDto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User successfully registered");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to register user: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
            response.put("message", "User successfully rejected");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to reject user: " + e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
