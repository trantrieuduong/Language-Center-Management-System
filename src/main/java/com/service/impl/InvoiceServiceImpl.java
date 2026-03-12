package com.service.impl;

import com.exception.BusinessException;
import com.model.academic.Class;
import com.model.academic.EnrollmentStatus;
import com.model.financial.Invoice;
import com.model.financial.InvoiceStatus;
import com.model.user.Student;
import com.model.user.UserRole;
import com.repository.AttendanceRepository;
import com.repository.EnrollmentRepository;
import com.repository.InvoiceRepository;
import com.repository.ResultRepository;
import com.repository.ScheduleRepository;
import com.security.CurrentUser;
import com.security.PermissionChecker;
import com.model.operation.Attendance;
import com.model.academic.Result;
import com.model.operation.Schedule;

import java.util.List;

public class InvoiceServiceImpl {

    private final InvoiceRepository invoiceRepo = new InvoiceRepository();
    private final EnrollmentRepository enrollmentRepo = new EnrollmentRepository();
    private final AttendanceRepository attendanceRepo = new AttendanceRepository();
    private final ResultRepository resultRepo = new ResultRepository();
    private final ScheduleRepository scheduleRepo = new ScheduleRepository();

    /** Xem danh sách: ADMIN/ACCOUNTANT thấy tất cả, STUDENT chỉ thấy của mình */
    public List<Invoice> findAll() {
        CurrentUser u = PermissionChecker.requireAuthenticated();
        if (u.role() == UserRole.STUDENT) {
            Long sid = u.relatedId();
            return sid == null ? List.of() : invoiceRepo.findByStudent(sid);
        }
        if (u.isAdmin() || u.isAccountant()) {
            return invoiceRepo.findAll();
        }
        throw new BusinessException("Bạn không có quyền xem danh sách hóa đơn.");
    }

    /**
     * Cập nhật trạng thái hóa đơn: chỉ ADMIN hoặc ACCOUNTANT.
     * Khi chuyển sang PAID → tự động set enrollment tương ứng sang ACCEPT.
     */
    public Invoice updateStatus(Long invoiceId, InvoiceStatus newStatus) {
        PermissionChecker.requireAdminOrStaff(com.model.user.StaffRole.ACCOUNTANT);

        Invoice invoice = invoiceRepo.findById(invoiceId)
                .orElseThrow(() -> new BusinessException("Không tìm thấy hóa đơn với ID: " + invoiceId));

        if (invoice.getStatus() == InvoiceStatus.CANCELED)
            throw new BusinessException("Hóa đơn đã bị hủy, không thể cập nhật.");
        if (invoice.getStatus() == InvoiceStatus.PAID && newStatus != InvoiceStatus.CANCELED)
            throw new BusinessException("Hóa đơn đã thanh toán, chỉ có thể chuyển sang CANCELED.");

        invoice.setStatus(newStatus);
        Invoice updated = invoiceRepo.update(invoice);

        // Khi thanh toán thành công → chuyển enrollment sang ACCEPT
        if (newStatus == InvoiceStatus.PAID) {
            acceptEnrollment(invoice);
        }

        return updated;
    }

    /**
     * Tìm enrollment của student với class tương ứng và set ACCEPT.
     * Đồng thời tạo Attendance cho các schedule của class,
     * tạo Result record, và hiển thị danh sách Schedule.
     */
    private void acceptEnrollment(Invoice invoice) {
        if (invoice.getStudent() == null || invoice.getAclass() == null)
            return;

        Long studentId = invoice.getStudent().getStudentID();
        Long classId = invoice.getAclass().getClassID();

        enrollmentRepo.findByStudentAndClass(studentId, classId).ifPresent(e -> {
            e.setStatus(EnrollmentStatus.ACCEPT);
            enrollmentRepo.update(e);

            // Tạo Attendance cho tất cả các Schedule của class
            createAttendanceForSchedules(e.getStudent(), e.getAclass());

            // Tạo Result record cho student và class
            createResult(e.getStudent(), e.getAclass());
        });
    }


     // Tạo Attendance record cho mỗi Schedule cho student và tại class đã đăng ký và thanh toán.
    private void createAttendanceForSchedules(Student student, Class aClass) {
        List<Schedule> schedules = scheduleRepo.findByClass(aClass.getClassID());
        for (Schedule schedule : schedules) {
            // Kiểm tra xem attendance đã tồn tại chưa
            boolean exists = attendanceRepo.findAll().stream()
                    .anyMatch(a -> a.getStudent().getStudentID().equals(student.getStudentID())
                            && a.getSchedule().getScheduleID().equals(schedule.getScheduleID()));

            if (!exists) {
                Attendance attendance = Attendance.builder()
                        .student(student)
                        .schedule(schedule)
                        .build();
                attendanceRepo.save(attendance);
            }
        }
    }


     // Tạo Result record cho student và tại class đã đăng ký và thanh toán.
    private void createResult(Student student, Class aClass) {
        // Kiểm tra xem result đã tồn tại chưa
        boolean exists = resultRepo.findAll().stream()
                .anyMatch(r -> r.getStudent().getStudentID().equals(student.getStudentID())
                        && r.getAClass().getClassID().equals(aClass.getClassID()));

        if (!exists) {
            Result result = Result.builder()
                    .student(student)
                    .aClass(aClass)
                    .score(null)
                    .comment(null)
                    .build();
            resultRepo.save(result);
        }
    }
}
