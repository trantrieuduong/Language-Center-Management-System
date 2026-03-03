package com.security;

public class SecurityContext {

    private static CurrentUser current;

    private SecurityContext() {}

    public static void login(CurrentUser user) {
        current = user;
    }

    public static void logout() {
        current = null;
    }

    public static CurrentUser get() {
        return current;
    }

    public static boolean isAuthenticated() {
        return current != null;
    }
}
