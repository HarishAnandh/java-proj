package com.satark.util;

import com.satark.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * HibernateUtil - Initializes and manages the Hibernate SessionFactory for SATARK.
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;
    private static boolean initialized = false;

    static {
        try {
            // Build SessionFactory from hibernate.cfg.xml and register entity
            sessionFactory = new Configuration()
                    .configure("hibernate.cfg.xml")
                    .addAnnotatedClass(User.class)
                    .buildSessionFactory();
            initialized = true;
            System.out.println("[SATARK] Hibernate SessionFactory initialized successfully.");
        } catch (Throwable ex) {
            System.err.println("[SATARK] Hibernate SessionFactory initialization failed: " + ex);
            initialized = false;
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static boolean isAvailable() {
        return initialized && sessionFactory != null && !sessionFactory.isClosed();
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
}
