package com.service.impl;

import com.exception.ValidationException;
import com.model.financial.Payment;
import com.model.user.UserRole;
import com.repository.PaymentRepository;
import com.security.PermissionChecker;

import java.math.BigDecimal;
import java.util.List;

public class PaymentServiceImpl {
    private final PaymentRepository repo = new PaymentRepository();

    public List<Payment> findAll() {
        var u = PermissionChecker.requireAuthenticated();
        if (u.role() == UserRole.STUDENT) {
            // Show payments related to own invoices
            Long sid = u.relatedId();
            return repo.findAll().stream()
                    .filter(p -> p.getInvoice() != null
                            && p.getInvoice().getStudent() != null
                            && p.getInvoice().getStudent().getStudentID().equals(sid))
                    .toList();
        }
        return repo.findAll();
    }

    public Payment save(Payment payment) {
        PermissionChecker.requireAdminOrStaff(com.model.user.StaffRole.ACCOUNTANT);
        if (payment.getAmount() == null || payment.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Số tiền thanh toán phải lớn hơn 0.");
        return repo.save(payment);
    }

    public Payment update(Payment payment) {
        PermissionChecker.requireAdminOrStaff(com.model.user.StaffRole.ACCOUNTANT);
        return repo.update(payment);
    }

    public void delete(Long id) {
        PermissionChecker.requireAdmin();
        repo.delete(id);
    }
}
