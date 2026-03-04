package com.service.impl;

import com.dto.ClassDTO;
import com.exception.BusinessException;
import com.model.academic.*;
import com.model.academic.Class;
import com.repository.*;
import com.security.PermissionChecker;

import java.util.List;

public class ClassServiceImpl {
    private final ClassRepository classRepo = new ClassRepository();
    private final EnrollmentRepository enrollmentRepo = new EnrollmentRepository();
    private final CourseRepository courseRepo = new CourseRepository();
    private final TeacherRepository teacherRepo = new TeacherRepository();
    private final RoomRepository roomRepo = new RoomRepository();

    public List<Class> findAll() {
        PermissionChecker.requireAuthenticated();
        var u = com.security.SecurityContext.get();
        if (u != null && u.isTeacher()) {
            return classRepo.findByTeacher(u.relatedId());
        }
        return classRepo.findAll();
    }

    public Class findById(Long id) {
        PermissionChecker.requireAuthenticated();
        return classRepo.findById(id)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lớp học."));
    }

    public List<Class> search(String keyword) {
        PermissionChecker.requireAuthenticated();
        return keyword == null || keyword.isBlank() ? classRepo.findAll() : classRepo.searchByName(keyword);
    }

    public Class save(ClassDTO dto) {
        PermissionChecker.requireAdminOrAnyStaff();
        if (dto.getClassName() == null || dto.getClassName().isBlank())
            throw new com.exception.ValidationException("Tên lớp không được để trống.");
        Class aClass = Class.builder()
                .className(dto.getClassName().trim())
                .maxStudent(dto.getMaxStudent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .status(dto.getStatus() != null ? dto.getStatus() : ClassStatus.ACTIVE)
                .course(courseRepo.findById(dto.getCourseID()).orElseThrow(() -> new RuntimeException("Không tìm thầy khóa học.")))
                .teacher(teacherRepo.findById(dto.getTeacherID()).orElseThrow(() -> new RuntimeException("Không tìm thầy giáo viên.")))
                .room(roomRepo.findById(dto.getRoomID()).orElseThrow(() -> new RuntimeException("Không tìm thầy phòng học.")))
                .build();
        return classRepo.save(aClass);
    }

    public Class update(Long id, ClassDTO dto) {
        PermissionChecker.requireAdminOrAnyStaff();
        Class old = this.findById(id);
        old.setClassName(dto.getClassName().trim());
        old.setMaxStudent(dto.getMaxStudent());
        old.setStatus(dto.getStatus());
        old.setCourse(courseRepo.findById(dto.getCourseID()).orElseThrow(() -> new RuntimeException("Không tìm thầy khóa học.")));
        old.setTeacher(teacherRepo.findById(dto.getTeacherID()).orElseThrow(() -> new RuntimeException("Không tìm thầy giáo viên.")));
        old.setRoom(roomRepo.findById(dto.getRoomID()).orElseThrow(() -> new RuntimeException("Không tìm thầy phòng học.")));
        old.setStartDate(dto.getStartDate());
        old.setEndDate(dto.getEndDate());
        return classRepo.update(old);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        classRepo.delete(id);
    }

    /**
     * Enroll a student into a class, enforcing maxStudent limit.
     */
    public Enrollment enroll(Enrollment enrollment) {
        PermissionChecker.requireAdminOrAnyStaff();
        Long classId = enrollment.getAclass().getClassID();
        Class aClass = classRepo.findById(classId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy lớp học."));
        long current = enrollmentRepo.countByClass(classId);
        if (aClass.getMaxStudent() > 0 && current >= aClass.getMaxStudent()) {
            throw new BusinessException(
                    "Lớp học đã đủ số học viên tối đa (" + aClass.getMaxStudent() + " người).");
        }
        return enrollmentRepo.save(enrollment);
    }
}
