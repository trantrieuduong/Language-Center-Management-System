package com.model.operation;

import com.model.academic.Class;
import com.model.user.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long attendanceID;
    @ManyToOne
            @JoinColumn(name = "student_id", nullable = false)
    Student student;
    @ManyToOne
            @JoinColumn(name = "class_id", nullable = false)
    Class aClass;
    @CreationTimestamp
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    AttendanceStatus status;
}
