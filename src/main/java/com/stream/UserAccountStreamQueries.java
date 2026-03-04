package com.stream;

import com.model.user.UserAccount;
import com.model.user.UserRole;

import java.util.List;

public final class UserAccountStreamQueries {

    public static List<UserAccount> searchByUsername(List<UserAccount> userAccounts, String username) {
        return userAccounts.stream()
                .filter(userAccount -> userAccount.getUsername().toLowerCase().contains(username.toLowerCase()))
                .toList();
    }

    public static List<UserAccount> filterByRole(List<UserAccount> userAccounts, UserRole role) {
        return userAccounts.stream()
                .filter(userAccount -> userAccount.getRole().equals(role))
                .toList();
    }
}
