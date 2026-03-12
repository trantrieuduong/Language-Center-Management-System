package com.service.impl;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.exception.BusinessException;
import com.model.user.*;
import com.repository.UserAccountRepository;
import com.security.PermissionChecker;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserAccountServiceImpl {
    private final UserAccountRepository repo = new UserAccountRepository();

    public List<UserAccount> findAll() {
        PermissionChecker.requireAdmin();
        return repo.findAll();
    }

    public UserAccount findById(UUID id){
        PermissionChecker.requireAdminOrAnyStaff();
        Optional<UserAccount> user = repo.findById(id);
        if (user.isPresent())
            return user.get();
        throw new BusinessException("Không tìm thấy user");
    }

    public UserAccount update(UserAccount account) {
        PermissionChecker.requireAdmin();
        return repo.update(account);
    }

    public UserAccount save(String username, String password, UserRole role, Object relatedObject) {
        PermissionChecker.requireAdmin();

        UserAccount account = UserAccount.builder()
                .username(username.trim())
                .passwordHash(BCrypt.withDefaults().hashToString(12, password.toCharArray()))
                .role(role)
                .build();

        switch (role) {
            case TEACHER -> account.setTeacher((Teacher) relatedObject);
            case STUDENT -> account.setStudent((Student) relatedObject);
            case STAFF -> account.setStaff((Staff) relatedObject);
        }

        return repo.update(account);
    }
}
