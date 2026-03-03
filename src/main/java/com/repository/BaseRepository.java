package com.repository;

import com.exception.SystemException;
import com.db.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

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
            throw new SystemException("Lỗi khi xóa dữ liệu: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }
}
