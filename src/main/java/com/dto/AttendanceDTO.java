package com.dto;

import com.model.operation.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDTO {
    Long attendanceID;
    Long studentID;
    LocalDate date;
    LocalTime startTime;
    AttendanceStatus status;
}
