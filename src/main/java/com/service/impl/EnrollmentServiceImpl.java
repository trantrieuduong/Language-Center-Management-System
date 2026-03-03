package com.service.impl;

import com.model.academic.Enrollment;
import com.model.user.UserRole;
import com.repository.EnrollmentRepository;
import com.security.PermissionChecker;

import java.util.List;

public class EnrollmentServiceImpl {
    private final EnrollmentRepository repo = new EnrollmentRepository();

    public List<Enrollment> findAll() {
        var u = PermissionChecker.requireAuthenticated();
        if (u.role() == UserRole.STUDENT) {
            Long sid = u.relatedId();
            return sid == null ? List.of() : repo.findByStudent(sid);
        }
        return repo.findAll();
    }

    public List<Enrollment> findByStudent(Long studentId) {
        PermissionChecker.requireAuthenticated();
        return repo.findByStudent(studentId);
    }

    public Enrollment save(Enrollment e) {
        PermissionChecker.requireAdminOrAnyStaff();
        return repo.save(e);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdminOrAnyStaff();
        repo.delete(id);
    }
}
