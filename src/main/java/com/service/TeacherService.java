package com.service;

import com.model.user.Teacher;
import java.util.List;

public interface TeacherService {
    List<Teacher> findAll();

    List<Teacher> search(String keyword);

    Teacher findById(Long id);

    Teacher save(Teacher teacher);

    Teacher update(Teacher teacher);

    void delete(Long id);
}
