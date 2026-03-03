package com.security;

import com.exception.BusinessException;
import com.model.user.StaffRole;
import com.model.user.UserRole;

public class PermissionChecker {

    private PermissionChecker() {
    }

    public static CurrentUser requireAuthenticated() {
        CurrentUser u = SecurityContext.get();
        if (u == null)
            throw new BusinessException("Bạn chưa đăng nhập.");
        return u;
    }

    public static void requireAdmin() {
        CurrentUser u = requireAuthenticated();
        if (u.role() != UserRole.ADMIN) {
            throw new BusinessException("Chỉ Admin mới có quyền thực hiện thao tác này.");
        }
    }

    public static void requireAnyRole(UserRole... roles) {
        CurrentUser u = requireAuthenticated();
        for (UserRole r : roles) {
            if (u.role() == r)
                return;
        }
        throw new BusinessException("Bạn không có quyền thực hiện thao tác này.");
    }

    public static void requireStaffRole(StaffRole required) {
        CurrentUser u = requireAuthenticated();
        if (u.role() != UserRole.STAFF || u.staffRole() != required) {
            throw new BusinessException("Chỉ nhân viên " + required + " mới có quyền thực hiện thao tác này.");
        }
    }

    public static void requireAdminOrStaff(StaffRole staffRole) {
        CurrentUser u = requireAuthenticated();
        if (u.role() == UserRole.ADMIN)
            return;
        if (u.role() == UserRole.STAFF && u.staffRole() == staffRole)
            return;
        throw new BusinessException("Bạn không có quyền thực hiện thao tác này.");
    }

    public static void requireAdminOrAnyStaff() {
        CurrentUser u = requireAuthenticated();
        if (u.role() == UserRole.ADMIN || u.role() == UserRole.STAFF)
            return;
        throw new BusinessException("Bạn không có quyền thực hiện thao tác này.");
    }
}
