package com.service.impl;

import com.dto.ScheduleDTO;
import com.exception.BusinessException;
import com.model.academic.Class;
import com.model.academic.ClassStatus;
import com.model.operation.Room;
import com.model.operation.RoomStatus;
import com.model.operation.Schedule;
import com.model.user.StaffRole;
import com.repository.ClassRepository;
import com.repository.RoomRepository;
import com.repository.ScheduleRepository;
import com.security.PermissionChecker;
import com.stream.ScheduleStreamQueries;

import java.util.List;
import java.util.Optional;

public class ScheduleServiceImpl {
    private final ScheduleRepository repo = new ScheduleRepository();
    private final ClassRepository classRepo = new ClassRepository();
    private final RoomRepository roomRepo = new RoomRepository();

    private final ScheduleStreamQueries scheduleStreamQueries = new ScheduleStreamQueries();

    public List<Schedule> findAll() {
        return repo.findAll();
    }

    public Schedule save(ScheduleDTO dto) throws BusinessException {
        PermissionChecker.requireAdminOrStaff(StaffRole.CONSULTANT);

        Optional<Class> aClass = classRepo.findById(dto.getClassID());
        if (aClass.isEmpty())
            throw new BusinessException("Mã lớp học không tồn tại! Hãy nhập một mã lớp học khác!");
        else if (aClass.get().getStatus() != ClassStatus.ACTIVE)
            throw new BusinessException("Lớp học chưa sẵn sàng! Hãy nhập một mã lớp học khác!");

        Optional<Room> room = roomRepo.findById(dto.getRoomID());
        if (room.isEmpty())
            throw new BusinessException("Mã phòng học không tồn tại! Hãy nhập một mã phòng học khác!");
        else if (room.get().getStatus() != RoomStatus.ACTIVE)
            throw new BusinessException("Phòng học chưa sẵn sàng! Hãy nhập một mã phòng học khác!");

        // Overlap validation
        if (dto.getDate() != null
                && dto.getStartTime() != null && dto.getEndTime() != null) {
            List<Schedule> conflicts = scheduleStreamQueries.findOverlappingSchedules(
                    dto.getRoomID(),
                    dto.getDate(),
                    dto.getStartTime());
            if (!conflicts.isEmpty()) {
                throw new BusinessException("Phòng học đã có lịch trùng với thời gian này.");
            }
        }

        Schedule schedule = Schedule.builder()
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .aClass(aClass.get())
                .room(room.get())
                .build();

        return repo.save(schedule);
    }

    public Schedule update(ScheduleDTO dto) throws BusinessException {
        PermissionChecker.requireAdminOrStaff(StaffRole.CONSULTANT);

        Optional<Class> aClass = classRepo.findById(dto.getClassID());

        Optional<Room> room = roomRepo.findById(dto.getRoomID());
        if (room.isEmpty())
            throw new BusinessException("Mã phòng học không tồn tại! Hãy nhập một mã phòng học khác!");
        else if (room.get().getStatus() != RoomStatus.ACTIVE)
            throw new BusinessException("Phòng học chưa sẵn sàng! Hãy nhập một mã phòng học khác!");

        Optional<Schedule> schedule = repo.findById(dto.getScheduleID());
        if (schedule.isEmpty())
            throw new BusinessException("Không tìm thấy lịch học.");

        // Kiểm tra lớp không được học cùng lúc ở hai phòng khác nhau
        if (dto.getDate() != null && dto.getStartTime() != null) {
            if (!scheduleStreamQueries
                    .findConflictSchedules(repo.findAll(),
                            aClass.get().getClassID(),
                            dto.getScheduleID(),
                            dto.getRoomID(),
                            dto.getDate(),
                            dto.getStartTime()
                    ).isEmpty()) {
                throw new BusinessException("Một lớp không thể học cùng ngày cùng giờ tại hai phòng khác nhau!");
            }
        }

        // Kiểm tra 2 lớp không được học cùng lúc ở 1 phòng
        List<Schedule> roomConflicts = scheduleStreamQueries.findOverlappingSchedules(
                dto.getRoomID(),
                dto.getDate(),
                dto.getStartTime()
        );
        if (!roomConflicts.isEmpty()) {
            throw new BusinessException("Lỗi: Phòng " + room.get().getRoomName() + " có mã " + room.get().getRoomID() + " đã bị trùng lịch học tại thời điểm vừa nhập!");
        }

        schedule.get().setRoom(room.get());
        schedule.get().setAClass(aClass.get());
        schedule.get().setDate(dto.getDate());
        schedule.get().setStartTime(dto.getStartTime());
        schedule.get().setEndTime(dto.getEndTime());

        return repo.update(schedule.get());
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }

    public List<Schedule> searchByClassID(String classID) {
        PermissionChecker.requireAuthenticated();
        try {
            return repo.findByClass(Long.parseLong(classID));
        } catch (Exception e) {
            throw new BusinessException("Mã lớp học không tồn tại.");
        }
    }
}
