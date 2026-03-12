package com.model.academic;

import com.model.user.Student;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "enrollment", uniqueConstraints = {
        @UniqueConstraint(
                name = "unq_attendance",
                columnNames = {"student_id", "class_id"}
        )
})
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    Long enrollmentID;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id", nullable = false)
    Class aclass;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    EnrollmentStatus status = EnrollmentStatus.PENDING;

    @CreationTimestamp
    @Column(name = "enrolled_at")
    LocalDateTime enrolledAt;
}
