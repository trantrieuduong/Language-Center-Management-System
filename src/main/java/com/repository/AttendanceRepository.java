package com.repository;

import com.exception.SystemException;
import com.model.operation.Attendance;
import com.model.user.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

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
                            "ORDER BY s.date, s.startTime ",
                    Attendance.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn điểm danh: " + e.getMessage(), e);
        }
    }

    public List<Attendance> findByClassAndUser(Long classId, LocalDate attendanceDate, Long userId, UserRole userRole) {
        try (EntityManager em = em()) {
            String jpql = "SELECT a FROM Attendance a " +
                    "LEFT JOIN FETCH a.schedule s " +
                    "WHERE (:cid IS NULL OR a.schedule.aClass.classID = :cid) " +
                    "AND (:attendanceDate IS NULL OR s.date = :attendanceDate) ";
            boolean hasUid = false;
            if (userRole == UserRole.STUDENT) {
                jpql += "AND a.student.studentID = :uid ";
                hasUid = true;
            } else if (userRole == UserRole.TEACHER) {
                jpql += "AND a.schedule.aClass.teacher.teacherID = :uid ";
                hasUid = true;
            }
            jpql += "ORDER BY s.date, s.startTime ";

            TypedQuery<Attendance> typedQuery = em.createQuery(jpql, Attendance.class)
                    .setParameter("cid", classId)
                    .setParameter("attendanceDate", attendanceDate);
            if (hasUid) {
                typedQuery.setParameter("uid", userId);
                // do role khác teacher và student thì hàm này không có uid (không có giới hạn tìm kiếm)
            }
            return typedQuery.getResultList();
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
}
