package com.service.impl;

import com.model.operation.Room;
import com.repository.RoomRepository;
import com.security.PermissionChecker;

import java.util.List;

public class RoomServiceImpl {
    private final RoomRepository repo = new RoomRepository();

    public List<Room> findAll() {
        PermissionChecker.requireAuthenticated();
        return repo.findAll();
    }

    public Room save(Room room) {
        PermissionChecker.requireAdminOrAnyStaff();
        if (room.getRoomName() == null || room.getRoomName().isBlank())
            throw new com.exception.ValidationException("Tên phòng không được để trống.");
        return repo.save(room);
    }

    public Room update(Room room) {
        PermissionChecker.requireAdminOrAnyStaff();
        return repo.update(room);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }
}
