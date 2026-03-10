package com.service.impl;

import com.exception.BusinessException;
import com.model.academic.EnrollmentStatus;
import com.model.financial.Invoice;
import com.model.financial.InvoiceStatus;
import com.model.user.UserRole;
import com.repository.EnrollmentRepository;
import com.repository.InvoiceRepository;
import com.security.CurrentUser;
import com.security.PermissionChecker;

import java.util.List;

public class InvoiceServiceImpl {

    private final InvoiceRepository invoiceRepo = new InvoiceRepository();
    private final EnrollmentRepository enrollmentRepo = new EnrollmentRepository();

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
     */
    private void acceptEnrollment(Invoice invoice) {
        if (invoice.getStudent() == null || invoice.getAclass() == null)
            return;

        Long studentId = invoice.getStudent().getStudentID();
        Long classId = invoice.getAclass().getClassID();

        enrollmentRepo.findByStudentAndClass(studentId, classId).ifPresent(e -> {
            e.setStatus(EnrollmentStatus.ACCEPT);
            enrollmentRepo.update(e);
        });
    }
}
