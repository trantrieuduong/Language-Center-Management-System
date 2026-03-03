package com.db;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JpaUtil {

    private static final Logger log = LoggerFactory.getLogger(JpaUtil.class);
    private static final String PERSISTENCE_UNIT = "dataSource";
    private static EntityManagerFactory emf;

    private JpaUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            log.info("Initialising EntityManagerFactory (persistence-unit: {})", PERSISTENCE_UNIT);
            emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
        }
        return emf;
    }

    public static EntityManager getEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static synchronized void close() {
        if (emf != null && emf.isOpen()) {
            log.info("Closing EntityManagerFactory");
            emf.close();
        }
    }
}
