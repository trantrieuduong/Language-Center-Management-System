package com.repository;

import com.exception.SystemException;
import com.model.operation.Room;
import jakarta.persistence.EntityManager;

import java.util.List;

public class RoomRepository extends BaseRepository<Room, Long> {

    public RoomRepository() {
        super(Room.class);
    }

    @Override
    public List<Room> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT r FROM Room r ORDER BY r.roomName", Room.class)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn phòng học: " + e.getMessage(), e);
        }
    }
}
