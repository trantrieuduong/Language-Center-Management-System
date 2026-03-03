package com.repository;

import com.exception.SystemException;
import com.model.academic.Enrollment;
import jakarta.persistence.EntityManager;

import java.util.List;

public class EnrollmentRepository extends BaseRepository<Enrollment, Long> {

    public EnrollmentRepository() {
        super(Enrollment.class);
    }

    @Override
    public List<Enrollment> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT e FROM Enrollment e LEFT JOIN FETCH e.student LEFT JOIN FETCH e.aclass ORDER BY e.enrolledAt DESC",
                    Enrollment.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn đăng ký: " + e.getMessage(), e);
        }
    }

    public List<Enrollment> findByStudent(Long studentId) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT e FROM Enrollment e WHERE e.student.studentID = :sid ORDER BY e.enrolledAt DESC",
                            Enrollment.class)
                    .setParameter("sid", studentId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn đăng ký theo học viên: " + e.getMessage(), e);
        }
    }

    public List<Enrollment> findByClass(Long classId) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT e FROM Enrollment e WHERE e.aclass.classID = :cid",
                            Enrollment.class)
                    .setParameter("cid", classId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn đăng ký theo lớp: " + e.getMessage(), e);
        }
    }

    public long countByClass(Long classId) {
        try (EntityManager em = em()) {
            Long c = em.createQuery(
                            "SELECT COUNT(e) FROM Enrollment e WHERE e.aclass.classID = :cid", Long.class)
                    .setParameter("cid", classId)
                    .getSingleResult();
            return c == null ? 0 : c;
        } catch (Exception e) {
            throw new SystemException("Lỗi đếm số đăng ký: " + e.getMessage(), e);
        }
    }
}
