package com.kingston.koratuwa.entity.user.enums;

public class RoleUtils {
    public static Role getRoleFromInt(int value) {
        switch (value) {
            case 0:
                return Role.ADMIN;
            case 1:
                return Role.USER;
            default:
                throw new IllegalArgumentException("Invalid integer value for Role enum");
        }
    }
}
