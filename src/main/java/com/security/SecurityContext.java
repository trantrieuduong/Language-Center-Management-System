package com.security;

public class SecurityContext {

    /**
     * Static field, NOT ThreadLocal.
     * Rationale: This is a single-user desktop app. The SwingWorker background
     * thread (where login() is called) is different from the EDT and from other
     * worker threads. ThreadLocal keeps the session only on the originating thread,
     * making it invisible elsewhere — causing "Ban chua dang nhap" after login.
     * A shared static volatile field is the correct solution here.
     */
    private static volatile CurrentUser current;

    private SecurityContext() {
    }

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
