package com.repository;

import com.exception.SystemException;
import com.model.academic.Course;
import jakarta.persistence.EntityManager;

import java.util.List;

public class CourseRepository extends BaseRepository<Course, Long> {

    public CourseRepository() {
        super(Course.class);
    }

    @Override
    public List<Course> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT c FROM Course c ORDER BY c.courseName", Course.class)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn khóa học: " + e.getMessage(), e);
        }
    }

    public List<Course> searchByName(String keyword) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT c FROM Course c WHERE LOWER(c.courseName) LIKE :kw ORDER BY c.courseName",
                            Course.class)
                    .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi tìm kiếm khóa học: " + e.getMessage(), e);
        }
    }
}
