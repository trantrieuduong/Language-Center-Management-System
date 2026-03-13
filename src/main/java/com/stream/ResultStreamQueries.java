package com.stream;

import com.model.academic.Result;
import com.model.user.UserRole;
import com.repository.ResultRepository;

import java.util.Comparator;
import java.util.List;

public class ResultStreamQueries {
    private final ResultRepository resultRepository = new ResultRepository();

    public List<Result> findByClassAndUser(Long classId, Long userId, UserRole userRole) {
        return resultRepository.findAll().stream()
                .filter(r-> classId == null || r.getAClass().getClassID().equals(classId))
                .filter(r->{
                    if (userRole == UserRole.STUDENT) {
                        return r.getStudent().getStudentID().equals(userId);
                    } else if (userRole == UserRole.TEACHER) {
                        return r.getAClass().getTeacher().getTeacherID().equals(userId);
                    }
                    return true;
                })
                .sorted(Comparator.comparing((Result r)->r.getAClass().getClassID())
                        .thenComparing(r->r.getStudent().getStudentID()))
                .toList();
    }
}
