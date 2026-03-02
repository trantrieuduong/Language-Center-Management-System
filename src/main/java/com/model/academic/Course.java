package com.model.academic;

import com.model.user.UserStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long courseID;
    @Column(nullable = false)
    String courseName;
    String description;
    @Enumerated(EnumType.STRING)
    Level level;
    Integer duration; // hours / week
    @Column(precision = 18, scale = 2)
            @Builder.Default
    BigDecimal fee = BigDecimal.ZERO;
    @Enumerated(EnumType.STRING)
            @Builder.Default
    CourseStatus status = CourseStatus.ACTIVE;
    @CreationTimestamp
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
