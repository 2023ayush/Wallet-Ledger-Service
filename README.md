# 📘 Ledger Wallet Service API

📍 [Postman API Documentation](https://documenter.getpostman.com/view/33677881/2sBXc8q4Lm)

A secure, scalable Spring Boot-based wallet system implementing a ledger-driven transaction model for managing financial operations with audit-proof logging and JWT-based authentication.

🚀 Overview

The Ledger Wallet Service is a backend API that supports:

Wallet management: Top-up and withdrawal of funds

Peer-to-peer transfers: Transfer amounts between users

Ledger transactions: Recording every transaction with a robust ledger model

Transaction statuses: PENDING, SUCCESS, FAILED

Audit-proof logging: Ensures failed transactions are always recorded using a separate audit service with REQUIRES_NEW transaction propagation

JWT Authentication: Secure API access using JSON Web Tokens

Modular architecture: Structured into clean, separated modules

This service is designed for use in fintech systems, digital wallets, microservices architectures, and financial platforms requiring transactional integrity and audit logs.

# 🧠 Key Features

✔ Wallet balance management
✔ Ledger to track all transactions
✔ Transaction states: PENDING, SUCCESS, FAILED
✔ Audit-proof logging for failed transactions

# 📂 Project Structure

| Module     | Purpose                                          |
| ---------- | ------------------------------------------------ |
| **wallet** | Wallet entity and business logic                 |
| **ledger** | Ledger entity, service, and repository           |
| **audit**  | LedgerAuditService to record failed transactions |
| **auth**   | User authentication and JWT handling             |

# 💡 Transaction Status

| Status      | Meaning                         |
| ----------- | ------------------------------- |
| **PENDING** | Transaction initiated           |
| **SUCCESS** | Transaction completed           |
| **FAILED**  | Transaction failed but recorded |

# 🏗 System Design Highlights

Audit-proof transactions: Failed transactions logged even after rollback

Transactional integrity: Uses Spring transactions with REQUIRES_NEW

JWT security: Secures sensitive endpoints

Extensible architecture: Easy to add modules

REST-ful API design: Clean and consistent endpoint definitions

# 🛠 Tech Stack

| Category      | Technology            |
| ------------- | --------------------- |
| Language      | Java                  |
| Framework     | Spring Boot           |
| Security      | Spring Security + JWT |
| Database      | JPA / Hibernate       |
| Documentation | Postman API           |
