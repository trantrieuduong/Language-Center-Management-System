package com.service.impl;

import com.dto.RoomDTO;
import com.exception.BusinessException;
import com.exception.ValidationException;
import com.model.operation.Room;
import com.model.user.StaffRole;
import com.repository.RoomRepository;
import com.security.PermissionChecker;

import java.util.List;
import java.util.Optional;

public class RoomServiceImpl {
    private final RoomRepository repo = new RoomRepository();

    public List<Room> findAll() {
        PermissionChecker.requireAuthenticated();
        return repo.findAll();
    }

    public Room save(RoomDTO dto) throws Exception {
        PermissionChecker.requireAdminOrStaff(StaffRole.CONSULTANT);

        if (dto.getRoomName() == null || dto.getRoomName().isBlank())
            throw new ValidationException("Tên phòng không được để trống.");
        if (dto.getCapacity() <= 0)
            throw new ValidationException("Sức chứa phải lớn hơn 0.");

        Room room = new Room();
        room.setRoomName(dto.getRoomName());
        room.setCapacity(dto.getCapacity());
        room.setLocation(dto.getLocation());
        room.setStatus(dto.getStatus());

        return repo.save(room);
    }

    public Room update(Long id, RoomDTO dto) throws Exception {
        PermissionChecker.requireAdminOrStaff(StaffRole.CONSULTANT);

        Optional<Room> old = repo.findById(id);
        if(old.isEmpty())
            throw new BusinessException("Phòng học không tồn tại");

        old.get().setRoomName(dto.getRoomName());
        old.get().setCapacity(dto.getCapacity());
        old.get().setLocation(dto.getLocation());
        old.get().setStatus(dto.getStatus());
        return repo.update(old.get());
    }

    public List<Room> search(String keyword){
        PermissionChecker.requireAuthenticated();
        return keyword == null || keyword.isBlank() ? repo.findAll(): repo.searchByName(keyword);
    }
}
