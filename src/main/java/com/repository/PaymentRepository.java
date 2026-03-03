package com.repository;

import com.exception.SystemException;
import com.model.financial.Payment;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public class PaymentRepository extends BaseRepository<Payment, Long> {

    public PaymentRepository() {
        super(Payment.class);
    }

    @Override
    public List<Payment> findAll() {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT p FROM Payment p LEFT JOIN FETCH p.invoice ORDER BY p.paymentDate DESC",
                    Payment.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn thanh toán: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Optional<Payment> findByInvoice(Long invoiceId) {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT p FROM Payment p WHERE p.invoice.invoiceID = :iid",
                    Payment.class)
                    .setParameter("iid", invoiceId)
                    .getResultStream()
                    .findFirst();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn thanh toán theo hóa đơn: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
