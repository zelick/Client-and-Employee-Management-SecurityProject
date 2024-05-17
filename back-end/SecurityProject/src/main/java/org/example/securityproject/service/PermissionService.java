package org.example.securityproject.service;

import lombok.AllArgsConstructor;
import org.example.securityproject.dto.ResponseDto;
import org.example.securityproject.enums.Permission;
import org.example.securityproject.enums.UserRole;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PermissionService {

    public ResponseDto removePermissionFromRole (Permission permission, UserRole role) {
        ResponseDto response = new ResponseDto();

        role.removePermission(permission);

        response.setResponseMessage("Successfully deleted the permission from the role.");
        response.setFlag(true);

        return response;
    }

    public ResponseDto addPermissionToRole (Permission permission, UserRole role) {
        ResponseDto response = new ResponseDto();

        role.addPermission(permission);

        response.setResponseMessage("Successfully added the permission to the role.");
        response.setFlag(true);

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

        return missingPermissions;

    }
}
