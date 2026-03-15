package com.repository;

import com.exception.SystemException;
import com.model.operation.Schedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.time.LocalDate;

import java.util.List;

public class ScheduleRepository extends BaseRepository<Schedule, Long> {

    public ScheduleRepository() {
        super(Schedule.class);
    }

    @Override
    public List<Schedule> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT s FROM Schedule s " +
                            "LEFT JOIN FETCH s.aClass c " +
                            "LEFT JOIN FETCH s.room " +
                            "LEFT JOIN FETCH c.enrollments e " +
                            "LEFT JOIN FETCH e.student " +
                            "ORDER BY s.date, s.startTime",
                    Schedule.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn lịch học: " + e.getMessage(), e);
        }
    }

    public List<Schedule> findByClass(Long classId) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT s FROM Schedule s WHERE s.aClass.classID = :cid ORDER BY s.date, s.startTime",
                            Schedule.class)
                    .setParameter("cid", classId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn lịch học theo lớp: " + e.getMessage(), e);
        }
    }

    public void deleteFutureSchedulesByClassId(Long id, LocalDate day) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.createQuery("DELETE FROM Schedule s " +
                            "WHERE s.aClass.classID = :id " +
                            "AND s.date >= :day")
                    .setParameter("id", id)
                    .setParameter("day", day)
                    .executeUpdate();
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new SystemException("Lỗi xóa lịch học tương lai theo lớp: " + "Không thể xóa các lịch học cũ vì đã có dữ liệu điểm danh liên quan! Hủy thao tác!", e);
        } finally {
            em.close();
        }
    }
}
