package com.repository;

import com.exception.SystemException;
import com.model.academic.Result;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ResultRepository extends BaseRepository<Result, Long> {

    public ResultRepository() {
        super(Result.class);
    }

    @Override
    public List<Result> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT r FROM Result r LEFT JOIN FETCH r.student LEFT JOIN FETCH r.aClass",
                    Result.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn kết quả: " + e.getMessage(), e);
        }
    }
}
