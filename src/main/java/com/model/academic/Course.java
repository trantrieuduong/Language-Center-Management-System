package com.model.academic;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    Long courseID;

    @Column(name = "course_name", nullable = false)
    String courseName;

    @Column(name = "description")
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "level")
    Level level;

    @Column(name = "duration", nullable = false)
    Integer duration; // hours / week

    @Column(name = "fee", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    BigDecimal fee = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    CourseStatus status = CourseStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Override
    public String toString() {
        return this.courseName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Course)) return false;
        Course course = (Course) o;
        return Objects.equals(courseID, course.courseID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseID);
    }
}
