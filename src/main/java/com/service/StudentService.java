package com.service;

import com.dto.StudentDTO;
import com.model.user.Student;

import java.util.List;

public interface StudentService {
    List<Student> findAll();

    List<Student> search(String keyword);

    Student findById(Long id);

    Student save(StudentDTO dto);

    Student update(Long id, StudentDTO dto);

    void softDelete(Long id);
}
