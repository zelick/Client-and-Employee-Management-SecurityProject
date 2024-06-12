package org.example.securityproject.controller;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.*;
import org.example.securityproject.enums.Permission;
import org.example.securityproject.enums.UserRole;
import org.example.securityproject.repository.ConfirmationTokenRepository;
import org.example.securityproject.repository.UserRepository;
import org.example.securityproject.service.PermissionService;
import org.example.securityproject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/admins")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private UserService userService;
    private PermissionService permissionService;


    @GetMapping("/getAllEmployees")
    public ResponseEntity<List<UserDto>> getAllEmployees() {
        logger.debug("Fetching all employees.");
        try {
            List<UserDto> userDtos = userService.getAllEmployees()
                    .stream()
                    .map(user -> new UserDto(user))
                    .collect(Collectors.toList());
            logger.info("Retrieved {} employees successfully.", userDtos.size());
            return new ResponseEntity<>(userDtos, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch all employees: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getAdminData")
    public ResponseEntity<UserDto> getUserData() {
        UserDto userDto = new UserDto(userService.getUserData());
        return ResponseEntity.ok(userDto);
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

    @GetMapping("/getAllRegistrationRequests")
    public ResponseEntity<List<UserDto>> getAllRegistrationRequests() {
        logger.debug("Fetching all registration requests.");
        try {
            List<UserDto> userDtos = userService.getAllRegistrationRequests()
                    .stream()
                    .map(user -> new UserDto(user))
                    .collect(Collectors.toList());
            logger.info("Retrieved {} registration requests successfully.", userDtos.size());
            return new ResponseEntity<>(userDtos, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Failed to fetch all registration requests: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/updateAdminData")
    public ResponseEntity<ResponseDto> updateUserData (@RequestBody EditAdminDto adminDto) {
        ResponseDto response = new ResponseDto();
        response.setResponseMessage(userService.updateUserData(adminDto));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getAllRoles")
    public List<UserRole> getAllRoles() {
        logger.debug("Fetching all roles.");
       List<UserRole> roles = new ArrayList<>();

       roles.add(UserRole.ADMINISTRATOR);
       roles.add(UserRole.EMPLOYEE);
       roles.add(UserRole.CLIENT);

       return roles;
    }

    @GetMapping("/getAllPermissionsForRole/{role}")
    public ResponseEntity<Set<Permission>> getAllPermissionsForRole(@PathVariable UserRole role) {
        logger.debug("Fetching permissions for role: {}", role);
        Set<Permission> permissions = role.getPermissions();

        if (permissions == null) {
            logger.warn("Permissions not found for role: {}", role);
            return ResponseEntity.notFound().build();
        }

        logger.info("Returning {} permissions for role: {}", permissions.size(), role);
        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/removePermission")
    public ResponseEntity<ResponseDto> removePermission(@RequestBody PermissionRoleDto data) {
        logger.debug("Removing permission '{}' from role '{}'", data.getPermission(), data.getRole());
        Permission permission = data.getPermission();
        UserRole role = data.getRole();

        ResponseDto response = permissionService.removePermissionFromRole(permission, role);

        //proveri ovo nisam sigurna sa getResponseMessage - kristina loggovi
        if (response != null && response.getResponseMessage() != null) {
            logger.info(response.getResponseMessage());
        } else {
            logger.error("Failed to remove permission '{}' from role '{}'", data.getPermission(), data.getRole());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllCanBeAddedPermissions/{role}")
    public ResponseEntity<Set<Permission>> getAllCanBeAddedPermissions(@PathVariable UserRole role) {
        return ResponseEntity.ok(permissionService.findMissingPermissions(role));
    }

    @PutMapping("/addPermission")
    public ResponseEntity<ResponseDto> addPermission(@RequestBody PermissionRoleDto data) {
        logger.debug("Adding permission '{}' to role '{}'", data.getPermission(), data.getRole());
        Permission permission = data.getPermission();
        UserRole role = data.getRole();

        ResponseDto response = permissionService.addPermissionToRole(permission, role);

        //proveri ovo nisam sigurna sa getResponseMessage - kristina loggovi
        if (response != null && response.getResponseMessage() != null) {
            logger.info(response.getResponseMessage());
        } else {
            logger.error("Failed to add permission '{}' to role '{}'", data.getPermission(), data.getRole());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateAdminPassword")
    public ResponseEntity<ResponseDto> updateAdminPassword (@RequestBody PasswordDataDto passwordDataDto) throws NoSuchAlgorithmException {
        logger.debug("Updating password for admin with email '{}'", passwordDataDto.getEmail());

        ResponseDto response = new ResponseDto();
        //try catch - log
        try {
            response.setResponseMessage(userService.updateUserPassword(passwordDataDto));
            logger.info("Password updated successfully for admin with email '{}'", passwordDataDto.getEmail());
            return ResponseEntity.ok(response);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Failed to update password for admin with email '{}': {}", passwordDataDto.getEmail(), e.getMessage());
            response.setResponseMessage("Failed to update password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
