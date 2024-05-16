package org.example.securityproject.controller;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.UserDto;
import org.example.securityproject.repository.ConfirmationTokenRepository;
import org.example.securityproject.repository.UserRepository;
import org.example.securityproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/admins")
public class AdminController {
    @Autowired
    private UserService userService;

    @GetMapping("/getAllEmployees")
    //@PreAuthorize("hasAuthority('ADMINISTRATOR')")
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

}
