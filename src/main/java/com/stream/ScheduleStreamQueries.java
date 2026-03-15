package com.stream;

import com.model.academic.EnrollmentStatus;
import com.model.operation.Schedule;
import com.repository.ScheduleRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

public class ScheduleStreamQueries {
    private final ScheduleRepository scheduleRepository = new ScheduleRepository();

    /**
     * Lọc schedule mà student đã được ACCEPT (đã thanh toán).
     * Chỉ hiển thị lịch học của những lớp mà enrollment.status = ACCEPT.
     */
    public List<Schedule> filterSchedulesByStudentID(List<Schedule> schedules, Long id) {
        return schedules.stream()
                .filter(s -> s.getAClass() != null
                        && s.getAClass().getEnrollments() != null
                        && s.getAClass().getEnrollments().stream()
                        .anyMatch(e -> e.getStudent() != null
                                && e.getStudent().getStudentID()
                                .equals(id)
                                && e.getStatus() == EnrollmentStatus.ACCEPT))
                .toList();
    }

    public List<Schedule> filterSchedulesByTeacherID(List<Schedule> schedules, Long id) {
        return schedules.stream()
                .filter(s -> s.getAClass() != null
                        && s.getAClass().getTeacher() != null
                        && s.getAClass().getTeacher().getTeacherID().equals(id))
                .toList();
    }

    public List<Schedule> filterSchedulesByRangeAndClassName(List<Schedule> schedules, LocalDate start, LocalDate end, String classKeyword) {
            return schedules.stream()
                    .filter(s-> !s.getDate().isBefore(start)
                            && !s.getDate().isAfter(end)
                            && s.getAClass().getClassName().toLowerCase().contains(classKeyword == null ? "" : classKeyword.trim()))
                    .sorted(Comparator.comparing(Schedule::getDate).thenComparing(Schedule::getStartTime))
                    .toList();
    }

    /**
     * Dùng khi sửa lịch học (có schedule Id)
     */
    public List<Schedule> findConflictSchedules(
            List<Schedule> schedules,
            Long classId,
            Long currentScheduleID,
            Long roomId,
            LocalDate date,
            LocalTime startTime) {
        return schedules.stream()
            .filter(s -> s.getAClass().getClassID().equals(classId)
                    && s.getDate().equals(date)
                    && s.getStartTime().equals(startTime)
                    && !s.getScheduleID().equals(currentScheduleID)// Không phải chính nó
                    && !s.getRoom().getRoomID().equals(roomId))// NHƯNG lại khác phòng
            .toList();
    }

    /**
     * Dùng khi thêm lịch học (không có schedule Id)
     * 2 lớp cùng lịch học khi 1 lớp cùng phòng, cùng giờ, cùng ngày với lớp kia
     */
    public List<Schedule> findOverlappingSchedules(
            Long roomId,
            LocalDate date,
            LocalTime startTime) {

            return scheduleRepository.findAll().stream()
                    .filter(s->s.getRoom().getRoomID().equals(roomId)
                    && s.getDate().equals(date)
                    && s.getStartTime().equals(startTime)).toList();
    }

    /**
     * Kiểm tra xem một LỚP cụ thể có bị trùng lịch vào thời điểm này không
     * (Bất kể là phòng nào)
     */
    public List<Schedule> findClassConflict(List<Schedule> allSchedules, Long classId, LocalDate date, LocalTime startTime) {
        return allSchedules.stream()
                .filter(s -> s.getAClass().getClassID().equals(classId)
                        && s.getDate().equals(date)
                        && s.getStartTime().equals(startTime))
                .toList();
    }
}
