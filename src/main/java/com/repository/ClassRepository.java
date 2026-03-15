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
                    "SELECT c FROM Class c " +
                            "LEFT JOIN FETCH c.course " +
                            "LEFT JOIN FETCH c.teacher " +
                            "LEFT JOIN FETCH c.room " +
                            "ORDER BY c.className",
                    Class.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn lớp học: " + e.getMessage(), e);
        }
    }

    public List<Class> searchByExactName(String name) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT c FROM Class c WHERE LOWER(c.className) LIKE :kw ORDER BY c.className",
                            Class.class)
                    .setParameter("kw", name.toLowerCase())
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi tìm kiếm lớp học: " + e.getMessage(), e);
        }
    }

    public Long countByCourse(Long courseId) {
        try (EntityManager em = em()) {
            Long count = em.createQuery(
                            "SELECT COUNT(c) " +
                                    "FROM Class c " +
                                    "WHERE c.course.courseID = :courseId", Long.class)
                    .setParameter("courseId", courseId)
                    .getSingleResult();
            return count == null ? 0 : count;
        } catch (Exception e) {
            throw new SystemException("Lỗi đếm số lớp theo khóa học: " + e.getMessage(), e);
        }
    }
}
