package com.softserve.edu.delivery.domain;

public enum Role {
    ADMIN("Admin"),
    MODERATOR("Moderator"),
    CUSTOMER("Customer"),
    DRIVER("Driver");

    private String roleName;

    Role(String name) {
        roleName = name;
    }

    public String getName() {
        return roleName;
    }
}
