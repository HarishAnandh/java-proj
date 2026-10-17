-- ==========================================================
-- SATARK (सतर्क) - Disaster Management & Early Warning System
-- PostgreSQL Database Setup & Schema Script
-- ==========================================================

-- Optional: Create database (if not already created)
-- CREATE DATABASE disaster_safety;
-- \c disaster_safety

-- Step 1: Create the 'users' table
-- Note: Hibernate ORM with 'hibernate.hbm2ddl.auto=update' will also
-- automatically verify and update this table schema on startup.
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    quiz_score INT DEFAULT 0,
    scramble_score INT DEFAULT 0,
    picture_score INT DEFAULT 0,
    total_score INT DEFAULT 0,
    games_played INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Step 2: Index for high-performance leaderboard queries
CREATE INDEX IF NOT EXISTS idx_total_score ON users(total_score DESC);

-- Step 3: Optional starter demo users for initial leaderboard testing
INSERT INTO users (username, password, quiz_score, scramble_score, picture_score, total_score, games_played)
VALUES 
    ('responder_arjun', 'demo123', 140, 120, 90, 350, 8),
    ('ndrf_priya', 'demo123', 120, 105, 80, 305, 7),
    ('citizen_rahul', 'demo123', 90, 75, 70, 235, 5)
ON CONFLICT (username) DO NOTHING;
