package com.nyp.backend.enums;

public enum UserRole {
    ADMIN("Yönetici"),
    REGULAR("Normal Kullanıcı");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
