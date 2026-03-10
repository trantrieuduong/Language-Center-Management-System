package com.stream;

import com.model.academic.EnrollmentStatus;
import com.model.operation.Schedule;

import java.util.List;

public class ScheduleStreamQueries {

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
}
