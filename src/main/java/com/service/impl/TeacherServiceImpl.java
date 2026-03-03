package com.service.impl;

import com.exception.BusinessException;
import com.model.user.Teacher;
import com.repository.TeacherRepository;
import com.security.PermissionChecker;
import com.service.TeacherService;

import java.util.List;

public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository repo = new TeacherRepository();

    @Override
    public List<Teacher> findAll() {
        PermissionChecker.requireAuthenticated();
        return repo.findAll();
    }

    @Override
    public List<Teacher> search(String keyword) {
        PermissionChecker.requireAuthenticated();
        return keyword == null || keyword.isBlank() ? repo.findAll() : repo.searchByName(keyword);
    }

    @Override
    public Teacher findById(Long id) {
        PermissionChecker.requireAuthenticated();
        return repo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy giáo viên."));
    }

    @Override
    public Teacher save(Teacher teacher) {
        PermissionChecker.requireAdminOrAnyStaff();
        if (teacher.getFullName() == null || teacher.getFullName().isBlank())
            throw new com.exception.ValidationException("Tên giáo viên không được để trống.");
        return repo.save(teacher);
    }

    @Override
    public Teacher update(Teacher teacher) {
        PermissionChecker.requireAdminOrAnyStaff();
        return repo.update(teacher);
    }

    @Override
    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }
}
