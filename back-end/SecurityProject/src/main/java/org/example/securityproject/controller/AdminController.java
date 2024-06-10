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

@RestController
@AllArgsConstructor
@RequestMapping(value = "api/admins")
public class AdminController {
    @Autowired
    private UserService userService;
    private PermissionService permissionService;

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

    @GetMapping("/getAllUsers")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        try {
            List<UserDto> userDtos = userService.getAllUsers()
                    .stream()
                    .map(user -> new UserDto(user))
                    .collect(Collectors.toList());
            return new ResponseEntity<>(userDtos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/blockUser/{email}")
    public ResponseEntity<ResponseDto> blockUser(@PathVariable String email) {
        ResponseDto response = userService.blockUser(email);
        return new ResponseEntity<>(response, HttpStatus.OK);
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

    @PutMapping("/updateAdminData")
    public ResponseEntity<ResponseDto> updateUserData (@RequestBody EditAdminDto adminDto) {
        ResponseDto response = new ResponseDto();
        response.setResponseMessage(userService.updateUserData(adminDto));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/getAllRoles")
    public List<UserRole> getAllRoles() {
       List<UserRole> roles = new ArrayList<>();

       roles.add(UserRole.ADMINISTRATOR);
       roles.add(UserRole.EMPLOYEE);
       roles.add(UserRole.CLIENT);

       return roles;
    }

    @GetMapping("/getAllPermissionsForRole/{role}")
    public ResponseEntity<Set<Permission>> getAllPermissionsForRole(@PathVariable UserRole role) {
        Set<Permission> permissions = role.getPermissions();

        if (permissions == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(permissions);
    }

    @PutMapping("/removePermission")
    public ResponseEntity<ResponseDto> removePermission(@RequestBody PermissionRoleDto data) {
        Permission permission = data.getPermission();
        UserRole role = data.getRole();

        ResponseDto response = permissionService.removePermissionFromRole(permission, role);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/getAllCanBeAddedPermissions/{role}")
    public ResponseEntity<Set<Permission>> getAllCanBeAddedPermissions(@PathVariable UserRole role) {
        return ResponseEntity.ok(permissionService.findMissingPermissions(role));
    }

    @PutMapping("/addPermission")
    public ResponseEntity<ResponseDto> addPermission(@RequestBody PermissionRoleDto data) {
        Permission permission = data.getPermission();
        UserRole role = data.getRole();

        ResponseDto response = permissionService.addPermissionToRole(permission, role);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/updateAdminPassword")
    public ResponseEntity<ResponseDto> updateAdminPassword (@RequestBody PasswordDataDto passwordDataDto) throws NoSuchAlgorithmException {
        ResponseDto response = new ResponseDto();
        response.setResponseMessage(userService.updateUserPassword(passwordDataDto));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
