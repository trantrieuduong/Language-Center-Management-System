package com.repository;

import com.exception.SystemException;
import com.model.academic.Class;
import jakarta.persistence.EntityManager;

import java.util.List;

public class ClassRepository extends BaseRepository<Class, Long> {

    public ClassRepository() {
        super(Class.class);
    }

    @Override
    public List<Class> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT c FROM Class c LEFT JOIN FETCH c.course LEFT JOIN FETCH c.teacher ORDER BY c.className",
                    Class.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn lớp học: " + e.getMessage(), e);
        }
    }

    public List<Class> findByTeacher(Long teacherId) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT c FROM Class c WHERE c.teacher.teacherID = :tid ORDER BY c.className",
                            Class.class)
                    .setParameter("tid", teacherId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn lớp học theo giáo viên: " + e.getMessage(), e);
        }
    }

    public long countEnrollments(Long classId) {
        try (EntityManager em = em()) {
            Long count = em.createQuery(
                            "SELECT COUNT(e) FROM Enrollment e WHERE e.aclass.classID = :cid", Long.class)
                    .setParameter("cid", classId)
                    .getSingleResult();
            return count == null ? 0 : count;
        } catch (Exception e) {
            throw new SystemException("Lỗi đếm số học viên lớp: " + e.getMessage(), e);
        }
    }
}
