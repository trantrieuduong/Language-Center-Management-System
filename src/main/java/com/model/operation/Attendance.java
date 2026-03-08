package com.model.operation;

import com.model.academic.Class;
import com.model.user.Student;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "attendance", uniqueConstraints = {
        @UniqueConstraint(
                name = "unq_attendance",
                columnNames = {"schedule_id", "student_id"}
        )
})
public class Attendance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    Long attendanceID;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    Student student;

    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "class_id", nullable = false)
    Class aClass;

    @CreationTimestamp
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    AttendanceStatus status = AttendanceStatus.PRESENT;
}
