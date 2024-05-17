package org.example.securityproject.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.securityproject.model.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.ManyToMany;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.securityproject.enums.Permission.*;

@RequiredArgsConstructor
public enum UserRole {
    EMPLOYEE(new HashSet<>(Set.of(
            EMPLOYEE_READ,
            EMPLOYEE_CREATE,
            EMPLOYEE_UPDATE,
            EMPLOYEE_DELETE,
            CHANGE_PASSWORD
    ))),
    ADMINISTRATOR(new HashSet<>(Set.of(
            ADMIN_READ,
            ADMIN_CREATE,
            ADMIN_UPDATE,
            ADMIN_DELETE,
            EMPLOYEE_READ,
            EMPLOYEE_CREATE,
            EMPLOYEE_UPDATE,
            EMPLOYEE_DELETE,
            ADMIN_SEEPROFILE,
            CLIENT_READ,
            CLIENT_CREATE,
            CLIENT_UPDATE,
            CLIENT_DELETE,
            CHANGE_PASSWORD
    ))),
    CLIENT(new HashSet<>(Set.of(
            CLIENT_READ,
            CLIENT_CREATE,
            CLIENT_UPDATE,
            CLIENT_DELETE
    )));

    UserRole(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    //@Getter
    private Set<Permission> permissions = new HashSet<>();

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = permissions.stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }

    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

}