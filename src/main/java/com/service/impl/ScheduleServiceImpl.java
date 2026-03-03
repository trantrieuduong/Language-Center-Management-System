package com.service.impl;

import com.exception.BusinessException;
import com.model.operation.Schedule;
import com.repository.ScheduleRepository;
import com.security.PermissionChecker;

import java.util.List;

public class ScheduleServiceImpl {
    private final ScheduleRepository repo = new ScheduleRepository();

    public List<Schedule> findAll() {
        var u = PermissionChecker.requireAuthenticated();
        if (u.isTeacher()) {
            // return schedules for classes this teacher teaches
            return repo.findAll().stream()
                    .filter(s -> s.getAClass() != null
                            && s.getAClass().getTeacher() != null
                            && s.getAClass().getTeacher().getTeacherID().equals(u.relatedId()))
                    .toList();
        }
        if (u.isStudent()) {
            // schedules shown to student; further scoping done via enrollment
            // cross-reference
            return repo.findAll();
        }
        return repo.findAll();
    }

    public Schedule save(Schedule schedule) {
        PermissionChecker.requireAdminOrAnyStaff();
        // Overlap validation
        if (schedule.getRoom() != null && schedule.getDate() != null
                && schedule.getStartTime() != null && schedule.getEndTime() != null) {
            List<Schedule> conflicts = repo.findOverlapping(
                    schedule.getRoom().getRoomID(),
                    schedule.getDate(),
                    schedule.getStartTime(),
                    schedule.getEndTime());
            if (!conflicts.isEmpty()) {
                throw new BusinessException("Phòng học đã có lịch trùng với thời gian này.");
            }
        }
        return repo.save(schedule);
    }

    public Schedule update(Schedule schedule) {
        PermissionChecker.requireAdminOrAnyStaff();
        return repo.update(schedule);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }
}
