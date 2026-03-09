package com.repository;

import com.exception.DataInUseException;
import com.exception.SystemException;
import com.db.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.hibernate.exception.ConstraintViolationException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepository<T, ID> {

    protected final Class<T> entityClass;

    protected BaseRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager em() {
        return JpaUtil.getEntityManager();
    }

    public T save(T entity) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(entity);
            em.flush();  // Force Hibernate to execute INSERT and validate relationships
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new SystemException("Lỗi khi lưu dữ liệu: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public T update(T entity) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T merged = em.merge(entity);
            em.flush();  // Force Hibernate to execute UPDATE and validate relationships
            tx.commit();
            return merged;
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            throw new SystemException("Lỗi khi cập nhật dữ liệu: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public Optional<T> findById(ID id) {
        try (EntityManager em = em()) {
            return Optional.ofNullable(em.find(entityClass, id));
        } catch (Exception e) {
            throw new SystemException("Lỗi khi truy vấn dữ liệu: " + e.getMessage(), e);
        }
    }

    public List<T> findAll() {
        try (EntityManager em = em()) {
            String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
            return em.createQuery(jpql, entityClass).getResultList();
        } catch (Exception e) {
            throw new SystemException("Lỗi khi truy vấn dữ liệu: " + e.getMessage(), e);
        }
    }

    public void delete(ID id) {
        EntityManager em = em();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            T entity = em.find(entityClass, id);
            if (entity != null)
                em.remove(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();

            Throwable cause = e;
            while (cause != null) {
                if (cause instanceof ConstraintViolationException ||
                        cause instanceof SQLIntegrityConstraintViolationException) {

                    throw new DataInUseException(
                            "Không thể xóa: Dữ liệu đang được sử dụng ở bảng khác (vi phạm khóa ngoại)!"
                    );
                }
                cause = cause.getCause();
            }

            throw new SystemException("Lỗi khi xóa dữ liệu: " + e.getMessage(), e);

        } finally {
            em.close();
        }
    }
}
