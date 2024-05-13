package org.example.securityproject.controllers;

import org.example.securityproject.service.LoginService;
import org.example.securityproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final LoginService loginService;
    private final UserService userService;

    @Autowired
    public LoginController(LoginService loginService, UserService userService) {
        this.loginService = loginService;
        this.userService = userService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody String email) {
        if (!userService.checkIfExists(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with the provided email address was not found.");
        }

        if (!userService.checkServicePackage(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You do not have permission for passwordless login due to your service package type.");
        }

        loginService.sendEmail(email);
        return ResponseEntity.ok().build();
    }

}
