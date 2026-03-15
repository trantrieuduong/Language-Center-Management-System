package com.stream;

import com.model.academic.Class;
import com.repository.ClassRepository;

import java.util.Comparator;
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

    public List<Class> filterByName(List<Class> classes, String keyword) {
        return classes.stream()
                .filter(c->c.getClassName().toLowerCase().contains(keyword))
                .sorted(Comparator.comparing(Class::getClassName))
                .toList();
    }

    public List<Class> filterByTeacher(List<Class> classes, Long id){
        return classes.stream()
                .filter(c-> c.getTeacher().getTeacherID().equals(id))
                .toList();
    }

    public List<Class> filterByStudent(List<Class> classes, Long id){
        return classes.stream()
                .filter(c->c.getEnrollments().stream().
                        anyMatch(e->e.getStudent().getStudentID().equals(id)))
                .toList();
    }
}
