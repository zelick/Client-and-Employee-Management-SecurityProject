package org.example.securityproject.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Permission {

    ADMIN_READ("admin:read"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_CREATE("admin:create"),
    ADMIN_DELETE("admin:delete"),
    EMPLOYEE_READ("employee:read"),
    EMPLOYEE_UPDATE("employee:update"),
    EMPLOYEE_CREATE("employee:create"),
    EMPLOYEE_DELETE("employee:delete"),
    ADMIN_SEEPROFILE("admin:seeprofile"),
    CLIENT_READ("client:read"),
    CLIENT_CREATE("client:create"),
    CLIENT_UPDATE("client:update"),
    CLIENT_DELETE("client:delete"),
    CHANGE_PASSWORD("chnage:password")
    ;

    @Getter
    private final String permission;

}
