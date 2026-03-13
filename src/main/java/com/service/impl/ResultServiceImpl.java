package com.service.impl;

import com.dto.ResultDTO;
import com.exception.BusinessException;
import com.model.academic.Result;
import com.model.user.UserRole;
import com.repository.ResultRepository;
import com.security.PermissionChecker;
import com.stream.ResultStreamQueries;

import java.util.List;
import java.util.Optional;

public class ResultServiceImpl {
    private final ResultRepository repo = new ResultRepository();
    private final ResultStreamQueries resultStreamQueries = new ResultStreamQueries();

    public List<Result> findAll() {
        return repo.findAll();
    }

    public Result update(ResultDTO dto) throws Exception {
        var u = PermissionChecker.requireAuthenticated();
        if (!u.isAdmin() && !u.isTeacher())
            throw new BusinessException("Chỉ giáo viên hoặc admin mới có thể sửa điểm.");
        Optional<Result> result = repo.findById(dto.getResultID());
        if(result.isEmpty())
            throw new  BusinessException("Không tìm thấy lịch sử chấm điểm!");
        if (u.isTeacher()) {
            Long tid = u.relatedId();
            if (result.get().getAClass() == null || result.get().getAClass().getTeacher() == null
                    || !result.get().getAClass().getTeacher().getTeacherID().equals(tid))
                throw new BusinessException("Bạn chỉ được nhập điểm cho lớp mình dạy.");
        }
        Result r = result.get();
        r.setScore(dto.getScore());
        r.setComment(dto.getComment());
        return repo.update(r);
    }

    public List<Result> search(Long classId, Long userId, UserRole userRole) {
        PermissionChecker.requireAuthenticated();
        try {
            return resultStreamQueries.findByClassAndUser(classId, userId, userRole);
        } catch (Exception e) {
            throw new BusinessException("Mã lớp học không hợp lệ!");
        }
    }
}
