package com.service.impl;

import com.exception.BusinessException;
import com.model.academic.Result;
import com.repository.ResultRepository;
import com.security.PermissionChecker;

import java.util.List;

public class ResultServiceImpl {
    private final ResultRepository repo = new ResultRepository();

    public List<Result> findAll() {
        var u = PermissionChecker.requireAuthenticated();
        if (u.isTeacher()) {
            Long tid = u.relatedId();
            return repo.findAll().stream()
                    .filter(r -> r.getAClass() != null
                            && r.getAClass().getTeacher() != null
                            && r.getAClass().getTeacher().getTeacherID().equals(tid))
                    .toList();
        }
        if (u.isStudent()) {
            Long sid = u.relatedId();
            return sid == null ? List.of() : repo.findByStudent(sid);
        }
        return repo.findAll();
    }

    public Result save(Result result) {
        var u = PermissionChecker.requireAuthenticated();
        if (!u.isAdmin() && !u.isTeacher())
            throw new BusinessException("Chỉ giáo viên hoặc admin mới có thể nhập điểm.");
        if (u.isTeacher()) {
            Long tid = u.relatedId();
            if (result.getAClass() == null || result.getAClass().getTeacher() == null
                    || !result.getAClass().getTeacher().getTeacherID().equals(tid))
                throw new BusinessException("Bạn chỉ được nhập điểm cho lớp mình dạy.");
        }
        return repo.save(result);
    }

    public Result update(Result result) {
        var u = PermissionChecker.requireAuthenticated();
        if (!u.isAdmin() && !u.isTeacher())
            throw new BusinessException("Chỉ giáo viên hoặc admin mới có thể sửa điểm.");
        return repo.update(result);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }
}
