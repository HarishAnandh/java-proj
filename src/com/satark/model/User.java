package com.satark.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User Entity - Hibernate / JPA Mapping for SATARK Disaster Management System.
 * Maps to 'users' table in PostgreSQL.
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "quiz_score")
    private int quizScore = 0;

    @Column(name = "scramble_score")
    private int scrambleScore = 0;

    @Column(name = "picture_score")
    private int pictureScore = 0;

    @Column(name = "total_score")
    private int totalScore = 0;

    @Column(name = "games_played")
    private int gamesPlayed = 0;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Timestamp createdAt;

    // Default constructor for Hibernate
    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.quizScore = 0;
        this.scrambleScore = 0;
        this.pictureScore = 0;
        this.totalScore = 0;
        this.gamesPlayed = 0;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getQuizScore() {
        return quizScore;
    }

    public void setQuizScore(int quizScore) {
        this.quizScore = quizScore;
    }

    public int getScrambleScore() {
        return scrambleScore;
    }

    public void setScrambleScore(int scrambleScore) {
        this.scrambleScore = scrambleScore;
    }

    public int getPictureScore() {
        return pictureScore;
    }

    public void setPictureScore(int pictureScore) {
        this.pictureScore = pictureScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Recalculates total score
     */
    public void recalculateTotalScore() {
        this.totalScore = this.quizScore + this.scrambleScore + this.pictureScore;
    }
}
