-- ==========================================================
-- SATARK (सतर्क) - Disaster Management & Early Warning System
-- PostgreSQL Database Setup & Schema Script
-- ==========================================================

-- Optional: Create database (if not already created)
-- CREATE DATABASE disaster_safety;
-- \c disaster_safety

-- Step 1: Create the 'users' table
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

CREATE INDEX IF NOT EXISTS idx_total_score ON users(total_score DESC);

-- Step 2: Create the 'alerts' table for Filed Disaster Cases
-- Extracted via Java Regular Expressions (Regex) from incoming incident reports
CREATE TABLE IF NOT EXISTS alerts (
    id SERIAL PRIMARY KEY,
    case_id VARCHAR(50) UNIQUE NOT NULL,
    disaster_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    location VARCHAR(150),
    pincode VARCHAR(10),
    affected_count INT DEFAULT 0,
    contact_phone VARCHAR(50),
    contact_email VARCHAR(100),
    reported_by VARCHAR(50),
    raw_report TEXT,
    extracted_summary TEXT,
    status VARCHAR(30) DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_alerts_created_at ON alerts(created_at DESC);

-- Step 3: Starter demo users for leaderboard testing
INSERT INTO users (username, password, quiz_score, scramble_score, picture_score, total_score, games_played)
VALUES 
    ('responder_arjun', 'demo123', 140, 120, 90, 350, 8),
    ('ndrf_priya', 'demo123', 120, 105, 80, 305, 7),
    ('citizen_rahul', 'demo123', 90, 75, 70, 235, 5)
ON CONFLICT (username) DO NOTHING;

-- Step 4: Starter demo alerts in alerts table
INSERT INTO alerts (case_id, disaster_type, severity, location, pincode, affected_count, contact_phone, contact_email, reported_by, raw_report, extracted_summary, status)
VALUES 
    ('CASE-2026-FL01', 'Flood', 'HIGH', 'Chennai, Marina Beach area', '600005', 150, '+91-9876543210', 'relief.chennai@ndma.gov.in', 'Harish', 'Severe Flash Flood reported near Marina Beach. Water level 4ft, 150 citizens stranded.', 'Flash Flood in Chennai (PIN: 600005) affecting 150 people. Immediate rescue boats required.', 'DISPATCHED'),
    ('CASE-2026-EQ02', 'Earthquake', 'MODERATE', 'Uttarkashi Region', '249193', 30, '+91-9845123456', 'emergency.uttar@seoc.gov.in', 'Officer Sharma', 'M 4.8 Seismic tremor reported near Uttarkashi. Structural wall cracks in 12 buildings.', 'Seismic tremor M4.8 in Uttarkashi. Structural inspection initiated.', 'ACTIVE')
ON CONFLICT (case_id) DO NOTHING;
