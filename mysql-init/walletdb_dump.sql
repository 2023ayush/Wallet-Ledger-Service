CREATE DATABASE IF NOT EXISTS walletdb;
USE walletdb;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Add some test users
INSERT INTO users (email, password) VALUES
('nirmal55@example.com', 'password123'),
('testuser@example.com', 'testpass');
