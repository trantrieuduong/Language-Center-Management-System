package com.repository;

import com.exception.SystemException;
import com.model.user.Staff;
import jakarta.persistence.EntityManager;

import java.util.List;

public class StaffRepository extends BaseRepository<Staff, Long> {

    public StaffRepository() {
        super(Staff.class);
    }

    @Override
    public List<Staff> findAll() {
        try (EntityManager em = em()) {
            return em.createQuery("SELECT s FROM Staff s ORDER BY s.fullName", Staff.class)
                    .getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn nhân viên: " + e.getMessage(), e);
        }
    }
}
