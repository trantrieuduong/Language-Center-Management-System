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
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT r FROM Result r LEFT JOIN FETCH r.student LEFT JOIN FETCH r.aClass",
                    Result.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn kết quả: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Result> findByClass(Long classId) {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT r FROM Result r WHERE r.aClass.classID = :cid",
                    Result.class)
                    .setParameter("cid", classId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn kết quả theo lớp: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Result> findByStudent(Long studentId) {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT r FROM Result r WHERE r.student.studentID = :sid",
                    Result.class)
                    .setParameter("sid", studentId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn kết quả theo học viên: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
