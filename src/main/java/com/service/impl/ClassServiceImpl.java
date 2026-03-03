package com.service.impl;

import com.exception.BusinessException;
import com.model.academic.Class;
import com.model.academic.Enrollment;
import com.model.user.UserRole;
import com.repository.ClassRepository;
import com.repository.EnrollmentRepository;
import com.security.PermissionChecker;

import java.util.List;

public class ClassServiceImpl {
    private final ClassRepository classRepo = new ClassRepository();
    private final EnrollmentRepository enrollmentRepo = new EnrollmentRepository();

    public List<Class> findAll() {
        PermissionChecker.requireAuthenticated();
        var u = com.security.SecurityContext.get();
        if (u != null && u.isTeacher()) {
            return classRepo.findByTeacher(u.relatedId());
        }
        return classRepo.findAll();
    }

    public Class findById(Long id) {
        PermissionChecker.requireAuthenticated();
        return classRepo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lớp học."));
    }

    public Class save(Class aClass) {
        PermissionChecker.requireAdminOrAnyStaff();
        if (aClass.getClassName() == null || aClass.getClassName().isBlank())
            throw new com.exception.ValidationException("Tên lớp không được để trống.");
        return classRepo.save(aClass);
    }

    public Class update(Class aClass) {
        PermissionChecker.requireAdminOrAnyStaff();
        return classRepo.update(aClass);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        classRepo.delete(id);
    }

    /** Enroll a student into a class, enforcing maxStudent limit. */
    public Enrollment enroll(Enrollment enrollment) {
        PermissionChecker.requireAdminOrAnyStaff();
        Long classId = enrollment.getAclass().getClassID();
        Class aClass = classRepo.findById(classId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lớp học."));
        long current = enrollmentRepo.countByClass(classId);
        if (aClass.getMaxStudent() > 0 && current >= aClass.getMaxStudent()) {
            throw new BusinessException(
                    "Lớp học đã đủ số học viên tối đa (" + aClass.getMaxStudent() + " người).");
        }
        return enrollmentRepo.save(enrollment);
    }
}
