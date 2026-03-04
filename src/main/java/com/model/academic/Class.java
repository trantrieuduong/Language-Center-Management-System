package com.model.academic;

import com.model.operation.Room;
import com.model.operation.RoomStatus;
import com.model.user.Teacher;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "class")
public class Class {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long classID;

    String className;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    Teacher teacher;

    @Column(nullable = false)
    LocalDate startDate;

    @Column(nullable = false)
    LocalDate endDate;

    @Column(nullable = false)
    @Builder.Default
    Integer maxStudent = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    Room room;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    ClassStatus status = ClassStatus.ACTIVE;
}
