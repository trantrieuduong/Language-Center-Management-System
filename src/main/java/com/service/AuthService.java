package com.service;

import com.security.CurrentUser;

public interface AuthService {
    CurrentUser login(String username, String password);
}
