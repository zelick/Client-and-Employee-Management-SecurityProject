package org.example.securityproject.dto;

import org.example.securityproject.enums.Permission;
import org.example.securityproject.enums.UserRole;

public class PermissionRoleDto {
    private Permission permission;
    private UserRole role;

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
