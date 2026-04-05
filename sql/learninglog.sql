-- ============================================================
-- Learning Logs Database — Schema (Week 5)
-- ============================================================
-- Run this file FIRST in phpMyAdmin to create the database
-- and tables. Then run seed.sql to add sample data.
--
-- Changes from Week 4:
--   1. NEW: users table (for registration and login)
--   2. UPDATED: topics table — added user_id foreign key
--      (each topic now belongs to a user)
--   3. entries table — unchanged from Week 4
-- ============================================================

-- Create and Use Database
CREATE DATABASE IF NOT EXISTS learning_logs;
USE learning_logs;

-- Drop existing tables for a clean install
-- (order matters: entries → topics → users, because of foreign keys)
DROP TABLE IF EXISTS entries;
DROP TABLE IF EXISTS topics;
DROP TABLE IF EXISTS users;

-- ========== NEW for Week 5 ==========
-- Users table — stores registered accounts
-- Password column stores BCrypt hash (60 chars), NOT plaintext
CREATE TABLE users (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(255) NOT NULL UNIQUE,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ========== UPDATED for Week 5 ==========
-- Topics table — added user_id column (each topic belongs to a user)
-- Week 4 version had: id, name, created_at, updated_at
-- Week 5 adds: user_id (foreign key to users table)
CREATE TABLE topics (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  user_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ========== Same as Week 4 ==========
-- Entries table — no changes from Week 4
CREATE TABLE entries (
  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  topic_id INT NOT NULL,
  title VARCHAR(200) NOT NULL,
  text TEXT NOT NULL,
  link VARCHAR(500),
  image VARCHAR(500),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (topic_id) REFERENCES topics(id) ON DELETE CASCADE
);
