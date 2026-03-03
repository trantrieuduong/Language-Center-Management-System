package com.service.impl;

import com.model.user.Staff;
import com.repository.StaffRepository;
import com.security.PermissionChecker;

import java.util.List;

public class StaffServiceImpl {
    private final StaffRepository repo = new StaffRepository();

    public List<Staff> findAll() {
        PermissionChecker.requireAdmin();
        return repo.findAll();
    }

    public Staff save(Staff staff) {
        PermissionChecker.requireAdmin();
        if (staff.getFullName() == null || staff.getFullName().isBlank())
            throw new com.exception.ValidationException("Tên nhân viên không được để trống.");
        return repo.save(staff);
    }

    public Staff update(Staff staff) {
        PermissionChecker.requireAdmin();
        return repo.update(staff);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }
}
