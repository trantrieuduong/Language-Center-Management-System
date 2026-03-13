package com.stream;

import com.model.academic.Class;
import com.repository.ClassRepository;

import java.util.List;

public class ClassStreamQueries {
    private final ClassRepository classRepository = new ClassRepository();
    public List<Class> findClassByTeacher(Long teacherId) {
        return classRepository.findAll().stream()
                .filter(c -> c.getTeacher().getTeacherID().equals(teacherId)).toList();
    }
    public List<Class> findClassByStudent(Long StudentId) {
        return classRepository.findAll().stream()
                .filter(c -> c.getEnrollments().stream()
                        .anyMatch(e -> e.getStudent().getStudentID().equals(StudentId))).toList();
    }
}
