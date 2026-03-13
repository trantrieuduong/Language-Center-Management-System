package com.stream;

import com.model.operation.Attendance;
import com.model.user.UserRole;
import com.repository.AttendanceRepository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public class AttendanceStreamQueries {
    AttendanceRepository attendanceRepository = new AttendanceRepository();

    public List<Attendance> findByClassAndUser(Long classId, LocalDate attendanceDate, Long userId, UserRole userRole) {
        return attendanceRepository.findAll().stream()
                // Lọc theo Lớp và Ngày (Xử lý null an toàn)
                .filter(a -> {
                    if (a.getSchedule() == null || a.getSchedule().getAClass() == null) {
                        return false;
                    }
                    boolean classMatches = classId == null || a.getSchedule().getAClass().getClassID().equals(classId);
                    boolean dateMatches = attendanceDate == null || a.getSchedule().getDate().equals(attendanceDate);
                    return classMatches && dateMatches;
                })
                // Lọc theo Vai trò
                .filter(a->{
                    if (a.getStudent() == null) {
                        return false;
                    }
                    if (userRole == UserRole.STUDENT) {
                        return a.getStudent().getStudentID().equals(userId);
                    } else if (userRole == UserRole.TEACHER) {
                        if (a.getSchedule() == null || a.getSchedule().getAClass() == null 
                            || a.getSchedule().getAClass().getTeacher() == null) {
                            return false;
                        }
                        return a.getSchedule().getAClass().getTeacher().getTeacherID().equals(userId);
                    }
                    return true; // Admin hoặc Role khác thấy hết
                })
                // Sắp xếp: Ngày tăng dần -> Giờ tăng dần
                .sorted(Comparator.comparing((Attendance a) -> a.getSchedule().getDate())
                        .thenComparing(a -> a.getSchedule().getStartTime()))
                .toList();
    }
}
