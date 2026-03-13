package com.stream;

import com.model.operation.Attendance;

import java.util.List;

public class AttendanceStreamQueries {
    public List<Attendance> findAttendanceByTeacherId(List<Attendance> attendances, Long teacherId){
        return attendances.stream()
                .filter(a -> a.getSchedule().getAClass() != null
                        && a.getSchedule().getAClass().getTeacher() != null
                        && a.getSchedule().getAClass().getTeacher().getTeacherID().equals(teacherId)
                )
                .toList();
    }
}
