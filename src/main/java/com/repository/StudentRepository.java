package com.repository;

import com.exception.SystemException;
import com.model.user.Student;
import com.model.user.UserStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class StudentRepository extends BaseRepository<Student, Long> {

    public StudentRepository() {
        super(Student.class);
    }

    @Override
    public List<Student> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT s FROM Student s ORDER BY s.studentID", Student.class)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn danh sách học viên: " + e.getMessage(), e);
        }
    }

    public List<Student> searchByName(String keyword) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT s FROM Student s WHERE LOWER(s.fullName) LIKE :kw ORDER BY s.studentID",
                            Student.class)
                    .setParameter("kw", "%" + keyword.toLowerCase() + "%")
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi tìm kiếm học viên: " + e.getMessage(), e);
        }
    }

    public List<Student> findActive() {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT s FROM Student s WHERE s.status = :st ORDER BY s.studentID",
                            Student.class)
                    .setParameter("st", UserStatus.ACTIVE)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn học viên active: " + e.getMessage(), e);
        }
    }

    /** Soft-delete: set status to INACTIVE instead of physically deleting. */
    public void softDelete(Long studentId) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Student s = em.find(Student.class, studentId);
            if (s != null) {
                s.setStatus(UserStatus.INACTIVE);
                em.merge(s);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new SystemException("Lỗi xóa mềm học viên: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
