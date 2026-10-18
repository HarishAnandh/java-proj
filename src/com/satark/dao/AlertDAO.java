package com.satark.dao;

import com.satark.model.Alert;
import com.satark.util.DBUtil;
import com.satark.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * AlertDAO - Data Access Object for Filed Disaster Cases.
 * Uses Hibernate ORM with resilient JDBC fallback to manage the 'alerts' database table.
 */
public class AlertDAO {

    static {
        // Automatically ensure the 'alerts' table schema exists in PostgreSQL
        try (Connection conn = DBUtil.getConnection();
             Statement st = conn.createStatement()) {
            st.execute(
                "CREATE TABLE IF NOT EXISTS alerts (" +
                "id SERIAL PRIMARY KEY, " +
                "case_id VARCHAR(50) UNIQUE NOT NULL, " +
                "disaster_type VARCHAR(50) NOT NULL, " +
                "severity VARCHAR(20) NOT NULL, " +
                "location VARCHAR(150), " +
                "pincode VARCHAR(10), " +
                "affected_count INT DEFAULT 0, " +
                "contact_phone VARCHAR(50), " +
                "contact_email VARCHAR(100), " +
                "reported_by VARCHAR(50), " +
                "raw_report TEXT, " +
                "extracted_summary TEXT, " +
                "status VARCHAR(30) DEFAULT 'ACTIVE', " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
            );
            System.out.println("[SATARK AlertDAO] Verified 'alerts' table schema in database.");
        } catch (Exception e) {
            System.err.println("[SATARK AlertDAO] Table verification: " + e.getMessage());
        }
    }

    /**
     * Saves a new disaster alert into the alerts table.
     */
    public boolean saveAlert(Alert alert) {
        if (alert == null) return false;

        // 1. Try Hibernate ORM
        if (HibernateUtil.isAvailable()) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();
                session.persist(alert);
                tx.commit();
                System.out.println("[SATARK AlertDAO] Alert filed successfully via Hibernate. Case ID: " + alert.getCaseId());
                return true;
            } catch (Exception e) {
                if (tx != null && tx.isActive()) tx.rollback();
                System.err.println("[SATARK AlertDAO] Hibernate saveAlert failed, attempting JDBC fallback: " + e.getMessage());
            }
        }

        // 2. JDBC Fallback
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "INSERT INTO alerts (case_id, disaster_type, severity, location, pincode, " +
                         "affected_count, contact_phone, contact_email, reported_by, raw_report, " +
                         "extracted_summary, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            ps = conn.prepareStatement(sql);
            ps.setString(1, alert.getCaseId());
            ps.setString(2, alert.getDisasterType());
            ps.setString(3, alert.getSeverity());
            ps.setString(4, alert.getLocation());
            ps.setString(5, alert.getPincode());
            ps.setInt(6, alert.getAffectedCount());
            ps.setString(7, alert.getContactPhone());
            ps.setString(8, alert.getContactEmail());
            ps.setString(9, alert.getReportedBy());
            ps.setString(10, alert.getRawReport());
            ps.setString(11, alert.getExtractedSummary());
            ps.setString(12, alert.getStatus() != null ? alert.getStatus() : "ACTIVE");

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (Exception e) {
            System.err.println("[SATARK AlertDAO] JDBC saveAlert error: " + e.getMessage());
            return false;
        } finally {
            closeQuietly(ps, conn);
        }
    }

    /**
     * Retrieves all disaster alerts from the alerts table.
     */
    public List<Alert> getAllAlerts() {
        // 1. Try Hibernate ORM
        if (HibernateUtil.isAvailable()) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<Alert> q = session.createQuery("FROM Alert ORDER BY id DESC", Alert.class);
                return q.getResultList();
            } catch (Exception e) {
                System.err.println("[SATARK AlertDAO] Hibernate getAllAlerts failed, attempting JDBC fallback: " + e.getMessage());
            }
        }

        // 2. JDBC Fallback
        List<Alert> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("SELECT * FROM alerts ORDER BY id DESC");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Alert a = new Alert();
                a.setId(rs.getInt("id"));
                a.setCaseId(rs.getString("case_id"));
                a.setDisasterType(rs.getString("disaster_type"));
                a.setSeverity(rs.getString("severity"));
                a.setLocation(rs.getString("location"));
                a.setPincode(rs.getString("pincode"));
                a.setAffectedCount(rs.getInt("affected_count"));
                a.setContactPhone(rs.getString("contact_phone"));
                a.setContactEmail(rs.getString("contact_email"));
                a.setReportedBy(rs.getString("reported_by"));
                a.setRawReport(rs.getString("raw_report"));
                a.setExtractedSummary(rs.getString("extracted_summary"));
                a.setStatus(rs.getString("status"));
                a.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(a);
            }
            rs.close();
        } catch (Exception e) {
            System.err.println("[SATARK AlertDAO] JDBC getAllAlerts error: " + e.getMessage());
        } finally {
            closeQuietly(ps, conn);
        }
        return list;
    }

    private void closeQuietly(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {
                }
            }
        }
    }
}
