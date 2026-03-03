package com.service.impl;

import com.exception.BusinessException;
import com.model.academic.Course;
import com.repository.CourseRepository;
import com.security.PermissionChecker;
import com.service.CourseService;

import java.util.List;

public class CourseServiceImpl implements CourseService {
    private final CourseRepository repo = new CourseRepository();

    @Override
    public List<Course> findAll() {
        PermissionChecker.requireAuthenticated();
        return repo.findAll();
    }

    @Override
    public List<Course> search(String keyword) {
        PermissionChecker.requireAuthenticated();
        return keyword == null || keyword.isBlank() ? repo.findAll() : repo.searchByName(keyword);
    }

    @Override
    public Course findById(Long id) {
        PermissionChecker.requireAuthenticated();
        return repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy khóa học."));
    }

    @Override
    public Course save(Course course) {
        PermissionChecker.requireAdminOrAnyStaff();
        if (course.getCourseName() == null || course.getCourseName().isBlank())
            throw new com.exception.ValidationException("Tên khóa học không được để trống.");
        return repo.save(course);
    }

    @Override
    public Course update(Course course) {
        PermissionChecker.requireAdminOrAnyStaff();
        return repo.update(course);
    }

    @Override
    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }
}
