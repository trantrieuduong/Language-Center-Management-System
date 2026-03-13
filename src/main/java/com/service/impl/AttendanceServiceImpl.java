package com.service.impl;

import com.dto.AttendanceDTO;
import com.exception.BusinessException;
import com.model.operation.Attendance;
import com.model.user.UserRole;
import com.repository.AttendanceRepository;
import com.security.PermissionChecker;
import com.stream.AttendanceStreamQueries;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AttendanceServiceImpl {
    private final AttendanceRepository repo = new AttendanceRepository();
    private final AttendanceStreamQueries attendanceStreamQueries = new AttendanceStreamQueries();

    public List<Attendance> findAll() {
        var u = PermissionChecker.requireAuthenticated();
        if (u.isTeacher()) {
            Long tid = u.relatedId();
            return attendanceStreamQueries.findAttendanceByTeacherId(repo.findAll(), tid);
        }
        if (u.isStudent()) {
            Long sid = u.relatedId();
            return sid == null ? List.of() : repo.findByStudent(sid);
        }
        return repo.findAll();
    }

    public Attendance update(AttendanceDTO dto) throws Exception {
        var u = PermissionChecker.requireAuthenticated();
        if (!u.isTeacher() && !u.isAdmin())
            throw new BusinessException("Bạn không có quyền sửa điểm danh.");

        Optional<Attendance> old = repo.findById(dto.getAttendanceID());

        if (old.isEmpty())
            throw new BusinessException("Không tìm thấy lịch sử điểm danh.");

        old.get().setStatus(dto.getStatus());

        return repo.update(old.get());
    }

    public List<Attendance> search(Long classId, LocalDate attendanceDate, Long userId, UserRole userRole) {
        PermissionChecker.requireAuthenticated();
        try {
            return repo.findByClassAndUser(classId, attendanceDate, userId, userRole);
        } catch (Exception e) {
            throw new BusinessException("Mã lớp học không hợp lệ!");
        }
    }
}
