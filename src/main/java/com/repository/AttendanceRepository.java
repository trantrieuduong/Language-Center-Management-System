package com.repository;

import com.exception.SystemException;
import com.model.operation.Attendance;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class AttendanceRepository extends BaseRepository<Attendance, Long> {

    public AttendanceRepository() {
        super(Attendance.class);
    }

    @Override
    public List<Attendance> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT a FROM Attendance a " +
                            "LEFT JOIN FETCH a.student " +
                            "LEFT JOIN FETCH a.schedule s " +
                            "ORDER BY s.date, s.startTime DESC",
                    Attendance.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn điểm danh: " + e.getMessage(), e);
        }
    }

    public List<Attendance> findByClass(Long classId) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT a FROM Attendance a WHERE a.aClass.classID = :cid ORDER BY a.createdAt DESC",
                            Attendance.class)
                    .setParameter("cid", classId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn điểm danh theo lớp: " + e.getMessage(), e);
        }
    }

    public List<Attendance> findByStudent(Long studentId) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT a FROM Attendance a WHERE a.student.studentID = :sid ORDER BY a.createdAt DESC",
                            Attendance.class)
                    .setParameter("sid", studentId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn điểm danh theo học viên: " + e.getMessage(), e);
        }
    }

    public List<Attendance> findOverlapping(Long classId, Long studentId, LocalDate date) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT a FROM Attendance a " +
                                    "WHERE a.aClass.classID = :cid " +
                                    "AND a.student.studentID = :sid " +
                                    "AND a.date = :date",
                            Attendance.class)
                    .setParameter("cid", classId)
                    .setParameter("sid", studentId)
                    .setParameter("date", date)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi kiểm tra trùng điểm danh: " + e.getMessage(), e);
        }
    }
}
