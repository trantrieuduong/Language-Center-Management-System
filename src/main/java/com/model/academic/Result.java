package com.model.academic;

import com.model.user.Student;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long resultID;
    @ManyToOne
            @JoinColumn(name = "student_id", nullable = false)
    Student student;
    @ManyToOne
            @JoinColumn(name = "class_id", nullable = false)
    Class aClass;
    Double score;
    String comment;
}
