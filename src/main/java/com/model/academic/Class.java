package com.model.academic;

import com.model.operation.Room;
import com.model.user.Teacher;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.List;

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
    @Column(name = "class_id")
    Long classID;

    @Column(name = "class_name")
    String className;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    Teacher teacher;

    @Column(name = "start_date", nullable = false)
    LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    LocalDate endDate;

    @Column(name = "max_student", nullable = false)
    @Builder.Default
    Integer maxStudent = 0;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    Room room;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    ClassStatus status = ClassStatus.ACTIVE;

    @OneToMany(mappedBy = "aclass")
    List<Enrollment> enrollments;
}
