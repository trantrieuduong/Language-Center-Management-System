package com.model.academic;

import com.model.user.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long enrollmentID;
    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "student_id", nullable = false)
    Student student;
    @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "class_id", nullable = false)
    Class aclass;
    @CreationTimestamp
    LocalDateTime enrolledAt;
    LocalDateTime updatedAt;
}
