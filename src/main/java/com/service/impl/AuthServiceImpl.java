package com.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.exception.BusinessException;
import com.exception.ValidationException;
import com.model.user.*;
import com.repository.UserAccountRepository;
import com.security.CurrentUser;
import com.security.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthServiceImpl implements com.service.AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);
    private final UserAccountRepository userAccountRepo = new UserAccountRepository();

    @Override
    public CurrentUser login(String username, String password) {
        if (username == null || username.isBlank())
            throw new ValidationException("Tên đăng nhập không được để trống.");
        if (password == null || password.isBlank())
            throw new ValidationException("Mật khẩu không được để trống.");

        UserAccount account = userAccountRepo.findByUsername(username.trim())
                .orElseThrow(() -> new BusinessException("Tên đăng nhập hoặc mật khẩu không đúng."));

        if (!BCrypt.verifyer().verify(password.toCharArray(), account.getPasswordHash().toCharArray()).verified) {
            throw new BusinessException("Tên đăng nhập hoặc mật khẩu không đúng.");
        }

        UserRole role = account.getRole();
        StaffRole staffRole = null;
        Long relatedId = null;
        UserStatus userStatus = null;

        if (role == UserRole.STUDENT && account.getStudent() != null) {
            relatedId = account.getStudent().getStudentID();
            userStatus = account.getStudent().getStatus();
        } else if (role == UserRole.TEACHER && account.getTeacher() != null) {
            relatedId = account.getTeacher().getTeacherID();
            userStatus = account.getTeacher().getStatus();
        } else if (role == UserRole.STAFF && account.getStaff() != null) {
            staffRole = account.getStaff().getRole();
            relatedId = account.getStaff().getStaffID();
            userStatus = account.getStaff().getStatus();
        }

        if (userStatus != null && userStatus.equals(UserStatus.INACTIVE))
            throw new BusinessException("Tài khoản đã bị vô hiệu hóa");

        CurrentUser current = new CurrentUser(account.getUserID(), account.getUsername(),
                role, staffRole, relatedId);
        SecurityContext.login(current);
        log.info("User logged in: {}", current);
        return current;
    }
}
