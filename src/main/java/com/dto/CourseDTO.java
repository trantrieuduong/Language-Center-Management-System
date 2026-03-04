package com.dto;

import com.model.academic.CourseStatus;
import com.model.academic.Level;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CourseDTO {
    Long courseID;
    String courseName;
    String description;
    Level level;
    Integer duration; // hours / week
    BigDecimal fee;
    CourseStatus status;
}
