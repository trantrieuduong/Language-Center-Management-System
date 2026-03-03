package com.security;

import com.model.user.StaffRole;
import com.model.user.UserRole;

import java.util.UUID;

public record CurrentUser(UUID userID,
                          String username,
                          UserRole role,
                          StaffRole staffRole,
                          Long relatedId) {
    @Override
    public String toString() {
        return "CurrentUser{username='" + username + "', role=" + role
                + (staffRole != null ? ", staffRole=" + staffRole : "") + "}";
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN;
    }

    public boolean isTeacher() {
        return role == UserRole.TEACHER;
    }

    public boolean isStudent() {
        return role == UserRole.STUDENT;
    }

    public boolean isStaff() {
        return role == UserRole.STAFF;
    }

    public boolean isConsultant() {
        return role == UserRole.STAFF && staffRole == StaffRole.CONSULTANT;
    }

    public boolean isAccountant() {
        return role == UserRole.STAFF && staffRole == StaffRole.ACCOUNTANT;
    }
}
