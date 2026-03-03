package com.service.impl;

import com.exception.ValidationException;
import com.model.financial.Invoice;
import com.model.user.UserRole;
import com.repository.InvoiceRepository;
import com.security.PermissionChecker;

import java.math.BigDecimal;
import java.util.List;

public class InvoiceServiceImpl {
    private final InvoiceRepository invoiceRepo = new InvoiceRepository();

    public List<Invoice> findAll() {
        var u = PermissionChecker.requireAuthenticated();
        if (u.role() == UserRole.STUDENT) {
            Long sid = u.relatedId();
            return sid == null ? List.of() : invoiceRepo.findByStudent(sid);
        }
        return invoiceRepo.findAll();
    }

    public Invoice save(Invoice invoice) {
        PermissionChecker.requireAdminOrStaff(com.model.user.StaffRole.ACCOUNTANT);
        if (invoice.getTotalAmount() == null || invoice.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Tổng tiền hóa đơn phải lớn hơn 0.");
        return invoiceRepo.save(invoice);
    }

    public Invoice update(Invoice invoice) {
        PermissionChecker.requireAdminOrStaff(com.model.user.StaffRole.ACCOUNTANT);
        return invoiceRepo.update(invoice);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        invoiceRepo.delete(id);
    }
}
