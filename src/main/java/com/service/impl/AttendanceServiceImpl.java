package com.service.impl;

import com.exception.BusinessException;
import com.model.operation.Attendance;
import com.model.user.UserRole;
import com.repository.AttendanceRepository;
import com.security.PermissionChecker;

import java.util.List;

public class AttendanceServiceImpl {
    private final AttendanceRepository repo = new AttendanceRepository();

    public List<Attendance> findAll() {
        var u = PermissionChecker.requireAuthenticated();
        if (u.isTeacher()) {
            Long tid = u.relatedId();
            return repo.findAll().stream()
                    .filter(a -> a.getAClass() != null
                            && a.getAClass().getTeacher() != null
                            && a.getAClass().getTeacher().getTeacherID().equals(tid)
                    )
                    .toList();
        }
        if (u.isStudent()) {
            Long sid = u.relatedId();
            return sid == null ? List.of() : repo.findByStudent(sid);
        }
        return repo.findAll();
    }

    public Attendance save(Attendance attendance) {
        var u = PermissionChecker.requireAuthenticated();
        if (!u.isTeacher() && !u.isAdmin() && !u.isStaff())
            throw new BusinessException("Bạn không có quyền tạo điểm danh.");

        if (u.isTeacher()) {
            Long tid = u.relatedId();
            if (attendance.getAClass() == null || attendance.getAClass().getTeacher() == null
                    || !attendance.getAClass().getTeacher().getTeacherID().equals(tid))
                throw new BusinessException("Bạn chỉ được điểm danh lớp mình dạy.");
        }
        return repo.save(attendance);
    }

    public Attendance update(Attendance attendance) {
        var u = PermissionChecker.requireAuthenticated();
        if (!u.isTeacher() && !u.isAdmin() && !u.isStaff())
            throw new BusinessException("Bạn không có quyền sửa điểm danh.");
        return repo.update(attendance);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }
}
