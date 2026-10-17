package com.satark.dao;

import com.satark.model.User;
import com.satark.util.DBUtil;
import com.satark.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDAO - Data Access Object for SATARK User Management.
 * Implements Hibernate ORM operations with resilient JDBC fallback.
 */
public class UserDAO {

    /**
     * Registers a new user.
     * @return null if successful, or error message string if failed.
     */
    public String registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            return "Username and password required";
        }

        String trimmedUser = username.trim();

        // 1. Try Hibernate ORM
        if (HibernateUtil.isAvailable()) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();

                Query<Long> checkQuery = session.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.username = :uname", Long.class);
                checkQuery.setParameter("uname", trimmedUser);
                Long count = checkQuery.uniqueResult();

                if (count != null && count > 0) {
                    return "Username already exists";
                }

                User newUser = new User(trimmedUser, password);
                session.persist(newUser);
                tx.commit();
                return null; // success
            } catch (Exception e) {
                if (tx != null && tx.isActive()) tx.rollback();
                System.err.println("[SATARK UserDAO] Hibernate register failed, attempting JDBC fallback: " + e.getMessage());
            }
        }

        // 2. JDBC Fallback
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement("SELECT id FROM users WHERE username = ?");
            ps.setString(1, trimmedUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rs.close();
                return "Username already exists";
            }
            rs.close();
            ps.close();

            ps = conn.prepareStatement("INSERT INTO users (username, password) VALUES (?, ?)");
            ps.setString(1, trimmedUser);
            ps.setString(2, password);
            ps.executeUpdate();
            return null; // success
        } catch (Exception e) {
            return "Registration error: " + e.getMessage();
        } finally {
            closeQuietly(ps, conn);
        }
    }

    /**
     * Authenticates a user.
     * @return User object if successful, null if credentials invalid or error.
     */
    public User loginUser(String username, String password) {
        if (username == null || password == null) return null;
        String trimmedUser = username.trim();

        // 1. Try Hibernate ORM
        if (HibernateUtil.isAvailable()) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<User> q = session.createQuery(
                        "FROM User WHERE username = :uname AND password = :pwd", User.class);
                q.setParameter("uname", trimmedUser);
                q.setParameter("pwd", password);
                return q.uniqueResult();
            } catch (Exception e) {
                System.err.println("[SATARK UserDAO] Hibernate login error, attempting JDBC fallback: " + e.getMessage());
            }
        }

        // 2. JDBC Fallback
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(
                    "SELECT id, username, password, quiz_score, scramble_score, picture_score, " +
                    "total_score, games_played FROM users WHERE username = ? AND password = ?"
            );
            ps.setString(1, trimmedUser);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setQuizScore(rs.getInt("quiz_score"));
                user.setScrambleScore(rs.getInt("scramble_score"));
                user.setPictureScore(rs.getInt("picture_score"));
                user.setTotalScore(rs.getInt("total_score"));
                user.setGamesPlayed(rs.getInt("games_played"));
                rs.close();
                return user;
            }
        } catch (Exception e) {
            System.err.println("[SATARK UserDAO] JDBC login error: " + e.getMessage());
        } finally {
            closeQuietly(ps, conn);
        }
        return null;
    }

    /**
     * Retrieves user profile by username.
     */
    public User getUserProfile(String username) {
        if (username == null) return null;
        String trimmedUser = username.trim();

        // 1. Try Hibernate ORM
        if (HibernateUtil.isAvailable()) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<User> q = session.createQuery(
                        "FROM User WHERE username = :uname", User.class);
                q.setParameter("uname", trimmedUser);
                return q.uniqueResult();
            } catch (Exception e) {
                System.err.println("[SATARK UserDAO] Hibernate profile error, attempting JDBC fallback: " + e.getMessage());
            }
        }

        // 2. JDBC Fallback
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(
                    "SELECT id, username, quiz_score, scramble_score, picture_score, " +
                    "total_score, games_played FROM users WHERE username = ?"
            );
            ps.setString(1, trimmedUser);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setQuizScore(rs.getInt("quiz_score"));
                user.setScrambleScore(rs.getInt("scramble_score"));
                user.setPictureScore(rs.getInt("picture_score"));
                user.setTotalScore(rs.getInt("total_score"));
                user.setGamesPlayed(rs.getInt("games_played"));
                rs.close();
                return user;
            }
        } catch (Exception e) {
            System.err.println("[SATARK UserDAO] JDBC profile error: " + e.getMessage());
        } finally {
            closeQuietly(ps, conn);
        }
        return null;
    }

    /**
     * Retrieves the top 10 players for the leaderboard.
     */
    public List<User> getLeaderboard(int limit) {
        // 1. Try Hibernate ORM
        if (HibernateUtil.isAvailable()) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Query<User> q = session.createQuery(
                        "FROM User ORDER BY totalScore DESC", User.class);
                q.setMaxResults(limit);
                return q.getResultList();
            } catch (Exception e) {
                System.err.println("[SATARK UserDAO] Hibernate leaderboard error, attempting JDBC fallback: " + e.getMessage());
            }
        }

        // 2. JDBC Fallback
        List<User> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            ps = conn.prepareStatement(
                    "SELECT username, quiz_score, scramble_score, picture_score, " +
                    "total_score, games_played FROM users ORDER BY total_score DESC LIMIT ?"
            );
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setQuizScore(rs.getInt("quiz_score"));
                user.setScrambleScore(rs.getInt("scramble_score"));
                user.setPictureScore(rs.getInt("picture_score"));
                user.setTotalScore(rs.getInt("total_score"));
                user.setGamesPlayed(rs.getInt("games_played"));
                list.add(user);
            }
            rs.close();
        } catch (Exception e) {
            System.err.println("[SATARK UserDAO] JDBC leaderboard error: " + e.getMessage());
        } finally {
            closeQuietly(ps, conn);
        }
        return list;
    }

    /**
     * Updates user's score for a drill/game and recalculates total.
     */
    public User updateScore(String username, String gameType, int score) {
        if (username == null || gameType == null) return null;
        String trimmedUser = username.trim();

        // 1. Try Hibernate ORM
        if (HibernateUtil.isAvailable()) {
            Transaction tx = null;
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                tx = session.beginTransaction();
                Query<User> q = session.createQuery(
                        "FROM User WHERE username = :uname", User.class);
                q.setParameter("uname", trimmedUser);
                User user = q.uniqueResult();

                if (user != null) {
                    if ("quiz".equals(gameType)) {
                        user.setQuizScore(user.getQuizScore() + score);
                    } else if ("scramble".equals(gameType)) {
                        user.setScrambleScore(user.getScrambleScore() + score);
                    } else if ("picture".equals(gameType)) {
                        user.setPictureScore(user.getPictureScore() + score);
                    }
                    user.recalculateTotalScore();
                    user.setGamesPlayed(user.getGamesPlayed() + 1);

                    session.merge(user);
                    tx.commit();
                    return user;
                }
            } catch (Exception e) {
                if (tx != null && tx.isActive()) tx.rollback();
                System.err.println("[SATARK UserDAO] Hibernate updateScore error, attempting JDBC fallback: " + e.getMessage());
            }
        }

        // 2. JDBC Fallback
        String column;
        switch (gameType) {
            case "quiz":     column = "quiz_score";     break;
            case "scramble": column = "scramble_score";  break;
            case "picture":  column = "picture_score";   break;
            default:         return null;
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = DBUtil.getConnection();
            String sql = "UPDATE users SET "
                    + column + " = " + column + " + ?, "
                    + "total_score = quiz_score + scramble_score + picture_score + ?, "
                    + "games_played = games_played + 1 "
                    + "WHERE username = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, score);
            ps.setInt(2, score);
            ps.setString(3, trimmedUser);
            int rows = ps.executeUpdate();
            ps.close();

            if (rows > 0) {
                ps = conn.prepareStatement(
                        "SELECT quiz_score, scramble_score, picture_score, " +
                        "total_score, games_played FROM users WHERE username = ?"
                );
                ps.setString(1, trimmedUser);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    User u = new User();
                    u.setUsername(trimmedUser);
                    u.setQuizScore(rs.getInt("quiz_score"));
                    u.setScrambleScore(rs.getInt("scramble_score"));
                    u.setPictureScore(rs.getInt("picture_score"));
                    u.setTotalScore(rs.getInt("total_score"));
                    u.setGamesPlayed(rs.getInt("games_played"));
                    rs.close();
                    return u;
                }
            }
        } catch (Exception e) {
            System.err.println("[SATARK UserDAO] JDBC updateScore error: " + e.getMessage());
        } finally {
            closeQuietly(ps, conn);
        }
        return null;
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
