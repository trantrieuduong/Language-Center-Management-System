package com.repository;

import com.exception.SystemException;
import com.model.user.UserAccount;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserAccountRepository extends BaseRepository<UserAccount, UUID> {

    public UserAccountRepository() {
        super(UserAccount.class);
    }

    @Override
    public List<UserAccount> findAll() {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT u FROM UserAccount u ORDER BY u.username",
                    UserAccount.class).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn tài khoản: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Optional<UserAccount> findByUsername(String username) {
        EntityManager em = em();
        try {
            return em.createQuery(
                    "SELECT u FROM UserAccount u LEFT JOIN FETCH u.student LEFT JOIN FETCH u.teacher LEFT JOIN FETCH u.staff WHERE u.username = :uname",
                    UserAccount.class)
                    .setParameter("uname", username)
                    .getResultStream()
                    .findFirst();
        } catch (Exception e) {
            throw new SystemException("Lỗi truy vấn tài khoản theo username: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public long countAll() {
        EntityManager em = em();
        try {
            Long count = em.createQuery("SELECT COUNT(u) FROM UserAccount u", Long.class).getSingleResult();
            return count == null ? 0 : count;
        } catch (Exception e) {
            return 0;
        } finally {
            em.close();
        }
    }
}
