package com.repository;

import com.exception.SystemException;
import com.model.financial.Invoice;
import jakarta.persistence.EntityManager;

import java.util.List;

public class InvoiceRepository extends BaseRepository<Invoice, Long> {

    public InvoiceRepository() {
        super(Invoice.class);
    }

    @Override
    public List<Invoice> findAll() {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT i FROM Invoice i LEFT JOIN FETCH i.student ORDER BY i.issuedAt DESC",
                    Invoice.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn hóa đơn: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public List<Invoice> findByStudent(Long studentId) {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT i FROM Invoice i WHERE i.student.studentID = :sid ORDER BY i.issuedAt DESC",
                    Invoice.class)
                    .setParameter("sid", studentId)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn hóa đơn theo học viên: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
