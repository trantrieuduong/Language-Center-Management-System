package com.repository;

import com.exception.SystemException;
import com.model.operation.Attendance;
import jakarta.persistence.EntityManager;

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
                            "LEFT JOIN FETCH s.aClass c " +
                            "LEFT JOIN FETCH c.teacher " +
                            "LEFT JOIN FETCH s.room " +
                            "ORDER BY s.date, s.startTime ",
                    Attendance.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn điểm danh: " + e.getMessage(), e);
        }
    }
}
