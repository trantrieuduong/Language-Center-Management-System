package com.service.impl;

import com.dto.EnrollmentDTO;
import com.exception.BusinessException;
import com.exception.InvalidStatusException;
import com.exception.SystemException;
import com.model.academic.Class;
import com.model.academic.ClassStatus;
import com.model.academic.Enrollment;
import com.model.financial.Invoice;
import com.model.financial.InvoiceStatus;
import com.model.financial.Payment;
import com.model.financial.PaymentStatus;
import com.model.user.Student;
import com.model.user.UserRole;
import com.model.user.UserStatus;
import com.repository.ClassRepository;
import com.repository.EnrollmentRepository;
import com.repository.InvoiceRepository;
import com.repository.PaymentRepository;
import com.repository.StudentRepository;
import com.security.CurrentUser;
import com.security.PermissionChecker;

import java.util.List;

public class EnrollmentServiceImpl {
    private final EnrollmentRepository enrollmentRepo = new EnrollmentRepository();
    private final ClassRepository classRepo = new ClassRepository();
    private final StudentRepository studentRepo = new StudentRepository();
    private final InvoiceRepository invoiceRepo = new InvoiceRepository();
    private final PaymentRepository paymentRepo = new PaymentRepository();

    public List<Enrollment> findAll() {
        var u = PermissionChecker.requireAuthenticated();
        if (u.role() == UserRole.STUDENT) {
            Long sid = u.relatedId();
            return sid == null ? List.of() : enrollmentRepo.findByStudent(sid);
        }
        return enrollmentRepo.findAll();
    }

    public List<Enrollment> findByStudent(Long studentId) {
        PermissionChecker.requireAuthenticated();
        return enrollmentRepo.findByStudent(studentId);
    }

    /**
     * Enroll a student into a class, enforcing maxStudent limit.
     * Automatically creates an Invoice (PENDING) + Payment (PENDING) after success.
     */
    public Enrollment save(EnrollmentDTO dto) {
        CurrentUser user = PermissionChecker.requireAuthenticated();

        // Validate DTO
        if (dto.getStudentID() == null || dto.getStudentID() <= 0) {
            throw new BusinessException("Mã học viên không hợp lệ!");
        }
        if (dto.getClassID() == null || dto.getClassID() <= 0) {
            throw new BusinessException("Mã lớp học không hợp lệ!");
        }

        Student aStudent = studentRepo.findById(dto.getStudentID())
                .orElseThrow(() -> new BusinessException("Mã học viên không tồn tại! Hãy nhập mã học viên khác!"));
        if (aStudent.getStatus() != UserStatus.ACTIVE)
            throw new InvalidStatusException("Học viên bị khóa! Nhập mã học viên khác!");

        Class aClass = classRepo.findById(dto.getClassID())
                .orElseThrow(() -> new BusinessException("Mã lớp học không tồn tại! Hãy nhập mã lớp học khác!"));
        if (aClass.getStatus() != ClassStatus.ACTIVE)
            throw new InvalidStatusException("Lớp học bị khóa! Nhập lớp học khác!");

        if (user.isAdmin() || user.isConsultant()) {
            long current = enrollmentRepo.countByClass(dto.getClassID());
            if (aClass.getMaxStudent() > 0 && current >= aClass.getMaxStudent()) {
                throw new BusinessException(
                        "Lớp học đã đủ số học viên tối đa (" + aClass.getMaxStudent() + " người).");
            }
        } else
            throw new BusinessException("Bạn không có quyền đăng ký lớp học!");

        Enrollment enrollment = Enrollment.builder()
                .aclass(aClass)
                .student(aStudent)
                .build();

        Enrollment saved;
        try {
            saved = enrollmentRepo.save(enrollment);
        } catch (SystemException e) {
            Throwable cause = e.getCause();
            while (cause != null) {
                if (cause.getMessage() != null && cause.getMessage().contains("Duplicate entry")) {
                    throw new BusinessException(
                            "Học viên đã từng đăng ký lớp học này! Hãy điều chỉnh lại các trường giá trị!");
                }
                cause = cause.getCause();
            }
            throw e;
        }

        // Tự động tạo Invoice + Payment (PENDING)
        autoCreateInvoiceAndPayment(aStudent, aClass);

        return saved;
    }

    /** Tạo Invoice PENDING và Payment PENDING tương ứng */
    private void autoCreateInvoiceAndPayment(Student student, Class aClass) {
        java.math.BigDecimal fee = (aClass.getCourse() != null && aClass.getCourse().getFee() != null)
                ? aClass.getCourse().getFee()
                : java.math.BigDecimal.ZERO;

        Invoice invoice = Invoice.builder()
                .student(student)
                .aclass(aClass)
                .totalAmount(fee)
                .status(InvoiceStatus.PENDING)
                .build();
        Invoice savedInvoice = invoiceRepo.save(invoice);

        Payment payment = Payment.builder()
                .invoice(savedInvoice)
                .amount(fee)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepo.save(payment);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdminOrAnyStaff();
        enrollmentRepo.delete(id);
    }
}
