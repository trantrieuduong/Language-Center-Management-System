package com.repository;

import com.exception.SystemException;
import com.model.user.Teacher;
import jakarta.persistence.EntityManager;

import java.util.List;

public class TeacherRepository extends BaseRepository<Teacher, Long> {

    public TeacherRepository() {
        super(Teacher.class);
    }

    @Override
    public List<Teacher> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT t FROM Teacher t ORDER BY t.fullName", Teacher.class)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn giáo viên: " + e.getMessage(), e);
        }
    }

    public List<Teacher> searchByName(String keyword) {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT t FROM Teacher t WHERE LOWER(t.fullName) LIKE :kw ORDER BY t.fullName", Teacher.class)
                    .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi tìm kiếm giáo viên: " + e.getMessage(), e);
        }
    }
}
