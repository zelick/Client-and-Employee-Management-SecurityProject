package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.ResponseDto;
import org.example.securityproject.enums.Permission;
import org.example.securityproject.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@AllArgsConstructor
public class PermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    public ResponseDto removePermissionFromRole (Permission permission, UserRole role) {
        ResponseDto response = new ResponseDto();

//        role.removePermission(permission);
//        response.setResponseMessage("Successfully deleted the permission from the role.");
//        response.setFlag(true);
        try {
            role.removePermission(permission);

            response.setResponseMessage("Successfully deleted the permission from the role.");
            response.setFlag(true);
            logger.info("Permission '{}' successfully removed from role '{}'.", permission, role);
        } catch (Exception e) {
            response.setResponseMessage("Failed to delete the permission from the role: " + e.getMessage());
            response.setFlag(false);
            logger.error("Error while removing permission '{}' from role '{}': {}", permission, role, e.getMessage(), e);
        }

        return response;
    }

    public ResponseDto addPermissionToRole (Permission permission, UserRole role) {
        ResponseDto response = new ResponseDto();

//        role.addPermission(permission);
//
//        response.setResponseMessage("Successfully added the permission to the role.");
//        response.setFlag(true);
        try {
            role.addPermission(permission);

            response.setResponseMessage("Successfully added the permission to the role.");
            response.setFlag(true);
            logger.info("Permission '{}' successfully added to role '{}'.", permission, role);
        } catch (Exception e) {
            response.setResponseMessage("Failed to add the permission to the role: " + e.getMessage());
            response.setFlag(false);
            logger.error("Error while adding permission '{}' to role '{}': {}", permission, role, e.getMessage(), e);
        }

        return response;
    }

    public Set<Permission> findMissingPermissions(UserRole role) {
        Set<Permission> allPermissions = new HashSet<>(Arrays.asList(Permission.values()));

        Set<String> rolePermissions = role.getPermissions().stream()
                .map(Permission::getPermission)
                .collect(Collectors.toSet());

        Set<Permission> missingPermissions = allPermissions.stream()
                .filter(permission -> !rolePermissions.contains(permission.getPermission()))
                .collect(Collectors.toSet());

        logger.info("Found missing permissions for role '{}': {}", role, missingPermissions);
        return missingPermissions;

    }
}
