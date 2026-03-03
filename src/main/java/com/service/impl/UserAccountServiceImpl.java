package com.service.impl;

import com.model.user.UserAccount;
import com.repository.UserAccountRepository;
import com.security.PermissionChecker;

import java.util.List;
import java.util.UUID;

public class UserAccountServiceImpl {
    private final UserAccountRepository repo = new UserAccountRepository();

    public List<UserAccount> findAll() {
        PermissionChecker.requireAdmin();
        return repo.findAll();
    }

    public UserAccount findById(UUID id) {
        PermissionChecker.requireAdmin();
        return repo.findById(id)
                .orElseThrow(() -> new com.exception.BusinessException("Không tìm thấy tài khoản."));
    }

    public UserAccount save(UserAccount account) {
        PermissionChecker.requireAdmin();
        if (account.getUsername() == null || account.getUsername().isBlank()
                || account.getPasswordHash() == null || account.getPasswordHash().isBlank())
            throw new com.exception.ValidationException("Tên đăng nhập và mật khẩu không được để trống.");

        return repo.save(account);
    }

    public UserAccount update(UserAccount account) {
        PermissionChecker.requireAdmin();
        return repo.update(account);
    }

    public void delete(UUID id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }
}
