package org.example.securityproject.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.example.securityproject.enums.Permission.*;

@RequiredArgsConstructor
public enum UserRole {
    CLIENT(Collections.emptySet()),
    EMPLOYEE(
            Set.of(
                    EMPLOYEE_READ,
                    EMPLOYEE_CREATE,
                    EMPLOYEE_UPDATE,
                    EMPLOYEE_DELETE
            )
    ),
    ADMINISTRATOR(
            Set.of(
                    ADMIN_READ,
                    ADMIN_CREATE,
                    ADMIN_UPDATE,
                    ADMIN_DELETE,
                    EMPLOYEE_READ,
                    EMPLOYEE_CREATE,
                    EMPLOYEE_UPDATE,
                    EMPLOYEE_DELETE
            )
    )
    ;

    @Getter
    private final Set<Permission> permissions;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.name()))
                .collect(Collectors.toList());

        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
