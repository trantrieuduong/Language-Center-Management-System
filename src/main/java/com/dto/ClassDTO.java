package com.dto;

import com.model.academic.ClassStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClassDTO {
    Long classID;
    String className;
    Long courseID;
    Long teacherID;
    LocalDate startDate;
    LocalDate endDate;
    Integer maxStudent;
    Long roomID;
    ClassStatus status;
}
