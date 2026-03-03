package com.repository;

import com.exception.SystemException;
import com.model.operation.Schedule;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;

import java.time.LocalTime;
import java.util.List;

public class ScheduleRepository extends BaseRepository<Schedule, Long> {

    public ScheduleRepository() {
        super(Schedule.class);
    }

    @Override
    public List<Schedule> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery(
                    "SELECT s FROM Schedule s LEFT JOIN FETCH s.aClass LEFT JOIN FETCH s.room ORDER BY s.date, s.startTime",
                    Schedule.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn lịch học: " + e.getMessage(), e);
        }
    }

    /**
     * Finds any schedule for a given room on a given date that overlaps with
     * [startTime, endTime].
     * Two intervals [a1,a2] and [b1,b2] overlap when a1 < b2 AND b1 < a2.
     */
    public List<Schedule> findOverlapping(Long roomId, LocalDate date,
            LocalTime startTime, LocalTime endTime) {
        try (EntityManager em = em()) {
            return em.createQuery(
                            "SELECT s FROM Schedule s WHERE s.room.roomID = :rid " +
                                    "AND s.date = :date " +
                                    "AND s.startTime < :end AND s.endTime > :start",
                            Schedule.class)
                    .setParameter("rid", roomId)
                    .setParameter("date", date)
                    .setParameter("start", startTime)
                    .setParameter("end", endTime)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi kiểm tra trùng lịch: " + e.getMessage(), e);
        }
    }

    public List<Schedule> findByClass(Long classId) {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT s FROM Schedule s WHERE s.aClass.classID = :cid ORDER BY s.date, s.startTime",
                    Schedule.class)
                    .setParameter("cid", classId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn lịch học theo lớp: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
