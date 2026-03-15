package com.service.impl;

import com.dto.ClassDTO;
import com.exception.BusinessException;
import com.exception.ValidationException;
import com.model.academic.*;
import com.model.academic.Class;
import com.model.operation.Room;
import com.model.operation.RoomStatus;
import com.model.operation.Schedule;
import com.model.user.StaffRole;
import com.model.user.Teacher;
import com.model.user.UserStatus;
import com.repository.*;
import com.security.CurrentUser;
import com.security.PermissionChecker;
import com.stream.ClassStreamQueries;
import com.stream.ScheduleStreamQueries;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class ClassServiceImpl {
    private final ClassRepository classRepo = new ClassRepository();
    private final CourseRepository courseRepo = new CourseRepository();
    private final TeacherRepository teacherRepo = new TeacherRepository();
    private final RoomRepository roomRepo = new RoomRepository();
    private final ScheduleRepository scheduleRepo = new ScheduleRepository();
    private final ClassStreamQueries classStreamQueries = new ClassStreamQueries();
    private final ScheduleStreamQueries scheduleStreamQueries = new ScheduleStreamQueries();

    public List<Class> findAll() {
        PermissionChecker.requireAuthenticated();
        var u = com.security.SecurityContext.get();
        if (u != null) {
            if (u.isTeacher())
                return classStreamQueries.findClassByTeacher(u.relatedId());
            else if (u.isStudent())
                return classStreamQueries.findClassByStudent(u.relatedId());
        }
        return classRepo.findAll();
    }

    public Class findById(Long id) {
        PermissionChecker.requireAuthenticated();
        return classRepo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lớp học."));
    }

    public List<Class> search(String keyword) {
        CurrentUser user = PermissionChecker.requireAuthenticated();
        List<Class> classFilterByName = keyword == null || keyword.isBlank() ? classRepo.findAll() : classStreamQueries.filterByName(classRepo.findAll(), keyword.trim());

        if (user.isTeacher())
            return classStreamQueries.filterByTeacher(classFilterByName, user.relatedId());
        else if (user.isStudent())
            return classStreamQueries.filterByStudent(classFilterByName, user.relatedId());
        else
            return classFilterByName;
    }

    public Class save(ClassDTO dto) throws Exception {
        PermissionChecker.requireAdminOrStaff(StaffRole.CONSULTANT);
        if (dto.getClassName() == null || dto.getClassName().isBlank())
            throw new ValidationException("Tên lớp không được để trống.");

        List<Class> existing = classRepo.searchByExactName(dto.getClassName());
        if (!existing.isEmpty())
            throw new BusinessException("Tên lớp không được trùng!");

        Optional<Course> course = courseRepo.findById(dto.getCourseID());
        if (course.isEmpty())
            throw new BusinessException("Mã khóa học không tồn tại! Hãy nhập một mã khóa học khác!");
        else if (course.get().getStatus() != CourseStatus.ACTIVE)
            throw new BusinessException("Khóa học chưa sẵn sàng! Hãy nhập một mã khóa học khác!");

        Optional<Room> room = roomRepo.findById(dto.getRoomID());
        if (room.isEmpty())
            throw new BusinessException("Mã phòng học không tồn tại! Hãy nhập một mã phòng học khác!");
        else if (room.get().getStatus() != RoomStatus.ACTIVE)
            throw new BusinessException("Phòng học chưa sẵn sàng! Hãy nhập một mã phòng học khác!");

        Optional<Teacher> teacher = teacherRepo.findById(dto.getTeacherID());
        if (teacher.isEmpty())
            throw new BusinessException("Mã giáo viên không tồn tại! Hãy nhập một mã giáo viên khác!");
        else if (teacher.get().getStatus() != UserStatus.ACTIVE)
            throw new BusinessException("Giáo viên đã bị khóa! Hãy nhập một mã giáo viên khác!");

        List<LocalDate> studyDays = calculateStudyDays(dto.getStartDate(), course.get().getDuration(), getDayOfWeekSet(dto.getDaysOfWeek()));
        LocalDate endDate = studyDays.getLast();

        // KIỂM TRA TRÙNG LẶP cho toàn bộ chuỗi ngày học
        for (LocalDate date : studyDays) {
            List<Schedule> roomConflicts = scheduleStreamQueries.findOverlappingSchedules(
                    dto.getRoomID(),
                    date,
                    dto.getStartTime()
            );
            if (!roomConflicts.isEmpty()) {
                throw new BusinessException("Lỗi: Phòng " + room.get().getRoomName() + " có mã " + room.get().getRoomID() + " đã bị trùng lịch học tại thời điểm vừa nhập!");
            }
        }

        Class aClass = Class.builder()
                .className(dto.getClassName().trim())
                .maxStudent(dto.getMaxStudent())
                .startDate(dto.getStartDate())
                .daysOfWeek(dto.getDaysOfWeek())
                .endDate(endDate)
                .status(dto.getStatus() != null ? dto.getStatus() : ClassStatus.ACTIVE)
                .course(course.get())
                .teacher(teacher.get())
                .room(room.get())
                .build();

        Class savedClass = classRepo.save(aClass);

        // Lưu danh sách Schedule (Tự động tạo lịch cho toàn bộ khóa học)
        for (LocalDate date : studyDays) {
            Schedule schedule = Schedule.builder()
                    .date(date)
                    .startTime(dto.getStartTime())
                    .endTime(dto.getEndTime())
                    .aClass(savedClass)
                    .room(room.get())
                    .build();
            scheduleRepo.save(schedule);
        }

        return savedClass;
    }

    public Class update(Long id, ClassDTO dto) throws Exception {
        PermissionChecker.requireAdminOrStaff(StaffRole.CONSULTANT);
        Class old = this.findById(id);
        LocalDate today = LocalDate.now();

        List<Class> existing = classRepo.searchByExactName(dto.getClassName());
        if (!existing.isEmpty() && !existing.getFirst().getClassID().equals(old.getClassID()))
            throw new BusinessException("Tên lớp không được trùng!");

        Optional<Course> course = courseRepo.findById(dto.getCourseID());
        if (course.isEmpty())
            throw new BusinessException("Khóa học không tồn tại! Hãy chọn một khóa học khác!");
        else if (course.get().getStatus() != CourseStatus.ACTIVE)
            throw new BusinessException("Khóa học chưa sẵn sàng! Hãy chọn một khóa học khác!");

        Optional<Room> room = roomRepo.findById(dto.getRoomID());
        if (room.isEmpty())
            throw new BusinessException("Mã phòng học không tồn tại! Hãy nhập một mã phòng học khác!");
        else if (room.get().getStatus() != RoomStatus.ACTIVE)
            throw new BusinessException("Phòng học chưa sẵn sàng! Hãy nhập một mã phòng học khác!");

        Optional<Teacher> teacher = teacherRepo.findById(dto.getTeacherID());
        if (teacher.isEmpty())
            throw new BusinessException("Mã giáo viên không tồn tại! Hãy nhập một mã giáo viên khác!");
        else if (teacher.get().getStatus() != UserStatus.ACTIVE)
            throw new BusinessException("Giáo viên đã bị khóa! Hãy nhập một mã giáo viên khác!");

        // 1. Tính toán số buổi đã dạy (để biết còn lại bao nhiêu buổi cần xếp lịch)
        List<Schedule> pastSchedules = scheduleRepo.findByClass(id).stream()
                .filter(s -> s.getDate().isBefore(today))
                .toList();

        int sessionsDone = pastSchedules.size();
        int remainingSessions = course.get().getDuration() - sessionsDone;

        if (remainingSessions < 0) {
            throw new BusinessException("Không thể đổi khóa học có tổng số buổi ít hơn số buổi đã dạy!");
        }

        // 2. Tính toán lịch học mới cho các buổi còn lại
        // Ngày bắt đầu tính từ hôm nay hoặc dto.getStartDate() (lấy cái muộn hơn)
        LocalDate nextStartDate = dto.getStartDate().isBefore(today) ? today : dto.getStartDate();
        Set<DayOfWeek> daysSet = getDayOfWeekSet(dto.getDaysOfWeek());

        List<LocalDate> futureStudyDays = calculateStudyDays(nextStartDate, remainingSessions, daysSet);
        LocalDate newEndDate = futureStudyDays.isEmpty() ? old.getEndDate() : futureStudyDays.getLast();

        // 3. Kiểm tra trùng lặp cho các buổi học mới
        for (LocalDate date : futureStudyDays) {
            List<Schedule> classConflicts = scheduleStreamQueries.findClassConflict(scheduleRepo.findAll(), id, date, dto.getStartTime());
            boolean hasClassConflict = classConflicts.stream()
                    .anyMatch(s -> !s.getRoom().getRoomID().equals(dto.getRoomID()));
            if (hasClassConflict) {
                throw new BusinessException("Lớp học này đã có lịch tại một phòng khác vào ngày: " + date);
            }

            List<Schedule> roomConflicts = scheduleStreamQueries.findOverlappingSchedules(dto.getRoomID(), date, dto.getStartTime());
            boolean hasRoomConflict = roomConflicts.stream()
                    .anyMatch(s -> !s.getAClass().getClassID().equals(id));

            if (hasRoomConflict) {
                throw new BusinessException("Phòng học đã bị lớp khác chiếm vào ngày " + date);
            }
        }

        old.setClassName(dto.getClassName().trim());
        old.setMaxStudent(dto.getMaxStudent());
        old.setStatus(dto.getStatus());
        old.setCourse(course.get());
        old.setTeacher(teacher.get());
        old.setRoom(room.get());
        old.setDaysOfWeek(dto.getDaysOfWeek());
        old.setStartDate(dto.getStartDate());
        old.setEndDate(newEndDate);

        // Đồng bộ Schedule: XÓA LỊCH TƯƠNG LAI - GIỮ LỊCH QUÁ KHỨ
        scheduleRepo.deleteFutureSchedulesByClassId(id, today);

        Class updatedClass = classRepo.update(old);

        // Chèn lịch học mới cho tương lai
        for (LocalDate date : futureStudyDays) {
            Schedule s = Schedule.builder()
                    .date(date)
                    .startTime(dto.getStartTime())
                    .endTime(dto.getEndTime())
                    .aClass(updatedClass)
                    .room(room.get())
                    .build();
            scheduleRepo.save(s);
        }

        return updatedClass;
    }

    public static List<LocalDate> calculateStudyDays(
            LocalDate startDate,
            int totalSessions,
            Set<DayOfWeek> daysOfWeek) {
        List<LocalDate> schedule = new ArrayList<>();
        if (totalSessions <= 0 || daysOfWeek.isEmpty()) return schedule;
        LocalDate current = startDate;
        int count = 0;

        while (count < totalSessions) {
            if (daysOfWeek.contains(current.getDayOfWeek())) {
                schedule.add(current);
                count++; // CHỈ TRỪ 1 BUỔI MỖI LẦN TÌM THẤY
            }
            current = current.plusDays(1);
        }
        return schedule;
    }

    public Set<DayOfWeek> getDayOfWeekSet(String daysString) {
        if (daysString == null || daysString.trim().isEmpty()) {
            return java.util.Collections.emptySet();
        }

        return Arrays.stream(daysString.split("\\s*,\\s*"))// Tách chuỗi theo dấu phẩy
                .map(String::trim)
                .map(String::toUpperCase)
                .map(DayOfWeek::valueOf) // Chuyển String thành Enum DayOfWeek
                .collect(Collectors.toSet()); // Gom vào Set
    }

    public String generateClassName(Course course) {
        String prefix = course.getCourseName();
        Long count = classRepo.countByCourse(course.getCourseID());
        return prefix + "-" + String.format("%02d", count + 1);
    }
}
