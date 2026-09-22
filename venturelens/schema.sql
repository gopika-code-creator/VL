-- =====================================================================
-- VentureLens Database Schema
-- Production DDL for MySQL 8.0+
-- Startup Operating System & Decision Intelligence Suite
-- =====================================================================

CREATE DATABASE IF NOT EXISTS venturelens_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE venturelens_db;

-- Drop existing tables in reverse dependency order if resetting
DROP TABLE IF EXISTS captable_scenarios;
DROP TABLE IF EXISTS burn_entries;
DROP TABLE IF EXISTS ventures;
DROP TABLE IF EXISTS users;

-- ---------------------------------------------------------------------
-- 1. Table: users
-- Core authentication table with SHA-256 hashed credentials
-- ---------------------------------------------------------------------
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(64) NOT NULL COMMENT 'SHA-256 hex encoded hash (64 chars)',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_username (username),
    INDEX idx_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 2. Table: ventures
-- Evaluated venture blueprints with NLP scores, decision tier, and SWOT
-- ---------------------------------------------------------------------
CREATE TABLE ventures (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    startup_name VARCHAR(200) NOT NULL,
    target_customer TEXT NOT NULL,
    problem_statement TEXT NOT NULL,
    proposed_solution TEXT NOT NULL,
    business_model VARCHAR(100) NOT NULL,
    
    -- NLP Rule-Based Engine Scores (0.0 to 100.0)
    overall_score DECIMAL(5,2) NOT NULL,
    market_score DECIMAL(5,2) NOT NULL,
    feasibility_score DECIMAL(5,2) NOT NULL,
    competition_score DECIMAL(5,2) NOT NULL,
    depth_score DECIMAL(5,2) NOT NULL,
    
    -- Decision Tier: STRONG_GO, GO, CAUTION, PIVOT
    decision_tier VARCHAR(30) NOT NULL,
    
    -- 4-Box SWOT Analysis (Stored as delimiter or structured text)
    swot_strengths TEXT,
    swot_weaknesses TEXT,
    swot_opportunities TEXT,
    swot_threats TEXT,
    
    -- Critical Failure Risks identified by NLP engine
    risks TEXT,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_ventures_user_id (user_id),
    INDEX idx_ventures_tier (decision_tier),
    INDEX idx_ventures_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 3. Table: captable_scenarios
-- Equity splits, ESOP pools, valuations, and dilution models
-- ---------------------------------------------------------------------
CREATE TABLE captable_scenarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venture_id INT NOT NULL,
    scenario_name VARCHAR(100) NOT NULL,
    founder1_pct DECIMAL(5,2) NOT NULL,
    founder2_pct DECIMAL(5,2) NOT NULL,
    esop_pct DECIMAL(5,2) NOT NULL,
    pre_money_val DECIMAL(15,2) NOT NULL,
    investment_amount DECIMAL(15,2) NOT NULL,
    post_money_val DECIMAL(15,2) NOT NULL,
    investor_pct DECIMAL(5,2) NOT NULL,
    founder1_diluted_pct DECIMAL(5,2) NOT NULL,
    founder2_diluted_pct DECIMAL(5,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (venture_id) REFERENCES ventures(id) ON DELETE CASCADE,
    INDEX idx_captable_venture_id (venture_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 4. Table: burn_entries
-- Monthly cash ledger for operational expenses and revenues
-- ---------------------------------------------------------------------
CREATE TABLE burn_entries (
    id INT AUTO_INCREMENT PRIMARY KEY,
    venture_id INT NOT NULL,
    month_label VARCHAR(50) NOT NULL,
    category VARCHAR(50) NOT NULL,
    entry_type VARCHAR(20) NOT NULL COMMENT 'EXPENSE or REVENUE',
    amount DECIMAL(15,2) NOT NULL,
    notes VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (venture_id) REFERENCES ventures(id) ON DELETE CASCADE,
    INDEX idx_burn_venture_id (venture_id),
    INDEX idx_burn_type (entry_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- Seed Demo Founder & Initial Venture Data for quick verification
-- Password for demo user is 'admin123' (SHA-256 hash below)
-- ---------------------------------------------------------------------
INSERT INTO users (id, username, email, password_hash) VALUES
(1, 'founder_alex', 'alex@venturelens.internal', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9');

INSERT INTO ventures (
    id, user_id, startup_name, target_customer, problem_statement, 
    proposed_solution, business_model, overall_score, market_score, 
    feasibility_score, competition_score, depth_score, decision_tier, 
    swot_strengths, swot_weaknesses, swot_opportunities, swot_threats, risks
) VALUES
(
    1, 1, 'DataPulse AI', 
    'B2B SaaS engineering leaders and CTOs', 
    'Engineering organizations struggle with silent data pipeline breakages causing massive enterprise revenue loss and customer churn.', 
    'An automated, cloud-native real-time telemetry agent with proprietary anomaly detection algorithms and zero-config SDK integrations.', 
    'B2B Enterprise SaaS Subscription (Tiered per cluster)', 
    84.50, 88.00, 85.00, 78.00, 85.00, 'STRONG_GO',
    'Proprietary anomaly detection algorithms; automated cloud-native telemetry; high switching costs',
    'Early-stage brand awareness; requires enterprise security certifications (SOC2)',
    'Rapidly expanding enterprise telemetry TAM; high willingness to pay among mid-market & enterprise CTOs',
    'Incumbent legacy APM tools adding basic telemetry; long enterprise sales cycles',
    'Enterprise compliance delay risk; high customer acquisition cost if outbound only'
);

INSERT INTO captable_scenarios (
    id, venture_id, scenario_name, founder1_pct, founder2_pct, esop_pct,
    pre_money_val, investment_amount, post_money_val, investor_pct,
    founder1_diluted_pct, founder2_diluted_pct
) VALUES
(
    1, 1, 'Seed Round', 50.00, 40.00, 10.00,
    6000000.00, 1500000.00, 7500000.00, 20.00,
    40.00, 32.00
);

INSERT INTO burn_entries (id, venture_id, month_label, category, entry_type, amount, notes) VALUES
(1, 1, 'Month 1', 'Engineering Salaries', 'EXPENSE', 22000.00, '2 Senior Engineers + 1 DevOps'),
(2, 1, 'Month 1', 'Cloud Infrastructure', 'EXPENSE', 4500.00, 'AWS ECS, RDS, Redis cluster'),
(3, 1, 'Month 1', 'SaaS Subscriptions', 'EXPENSE', 1200.00, 'GitHub, Datadog, Slack, Figma'),
(4, 1, 'Month 1', 'B2B Pilot Contracts', 'REVENUE', 8000.00, '2 paid pilot implementations'),
(5, 1, 'Month 2', 'Engineering Salaries', 'EXPENSE', 22000.00, 'Staff engineering team'),
(6, 1, 'Month 2', 'Cloud Infrastructure', 'EXPENSE', 5100.00, 'Expanded ingest clusters'),
(7, 1, 'Month 2', 'Marketing & Sales', 'EXPENSE', 3500.00, 'Developer content & outreach'),
(8, 1, 'Month 2', 'B2B Pilot Contracts', 'REVENUE', 14000.00, '3 recurring enterprise pilots');
