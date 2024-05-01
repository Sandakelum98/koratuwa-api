package com.kingston.koratuwa.enums;

public class RoleUtils {
    public static Role getRoleFromInt(int value) {
        switch (value) {
            case 0:
                return Role.ADMIN;
            case 1:
                return Role.FARMER;
            case 2:
                return Role.CUSTOMER;
            default:
                throw new IllegalArgumentException("Invalid integer value for Role enum");
        }
    }
}
