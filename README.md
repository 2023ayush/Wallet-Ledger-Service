# 📘 Ledger Wallet Service 

A secure, scalable Spring Boot-based wallet system implementing a ledger-driven transaction model for managing financial operations with audit-proof logging and JWT-based authentication.

This project is also designed to demonstrate digital transaction monitoring, system uptime management, and operational troubleshooting skills. It highlights monitoring, logging, alerting, and containerized deployments, making it suitable for ICT roles in fintech or digital services.

📍 [Postman API Documentation](https://documenter.getpostman.com/view/33677881/2sBXc8q4Lm)

📈 [Prometheus & AlertManager Screenshots](docs/Promethus%20and%20AlertManager%20Screenshots.pdf)

[![Application Logs](https://raw.githubusercontent.com/2023ayush/Wallet-Ledger-Service/main/applogs.png)](https://raw.githubusercontent.com/2023ayush/Wallet-Ledger-Service/main/applogs.png)


# 🖥️ Service Monitoring & Operations

1. Metrics Collection & Monitoring

Collected system and application metrics using Prometheus (CPU, memory, wallet transactions, active connections).

Configured Alertmanager to notify on service downtime, anomalies, or critical transaction failures.

Maintained dashboards to visualize API health, transaction volumes, and system performance trends.

2. Logging & Analysis

Collected Windows application and system logs using Filebeat.

Indexed and queried logs in Elasticsearch to identify errors and monitor system behavior.

Visualized operational insights and transaction trends through Kibana dashboards.

3. Database Operations

Monitored MySQL wallet database availability and connection health.

Wrote optimized SQL queries to track wallet transactions and troubleshoot issues.

Ensured continuous database connectivity and service uptime.

4. Containerized Deployment & Operations

Managed services in Docker containers with Docker Compose.

Monitored container health, resource usage, and service availability.

Deployed and maintained the system on an AWS Ubuntu VM with operational reliability.

5. Business Metrics Tracking

Tracked user login attempts, successful logins, and wallet transactions.

Monitored failed transactions to ensure audit compliance and operational integrity


# 🧠 System & Operations Features

✔ Wallet balance tracking and transaction monitoring
✔ Ledger maintains a full record of all transactions for auditing
✔ Transaction states monitored: PENDING, SUCCESS, FAILED
✔ Audit-proof logging ensures failed transactions are captured and visible for troubleshooting


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

- **Audit-proof transactions**: Failed transactions are logged even after rollback to ensure traceability.  
- **Transactional integrity**: Uses Spring transactions with `REQUIRES_NEW` to maintain consistent ledger states.  
- **JWT security**: Secures sensitive endpoints with token-based authentication.  
- **Extensible architecture**: Modular design makes it easy to add new services or features.  
- **RESTful API design**: Clean, consistent, and standardized endpoints for integration.  
- **Service Monitoring**: Metrics collected using Prometheus and alerts configured in Alertmanager to ensure uptime.  
- **Logging & Analysis**: Filebeat collects logs, Elasticsearch indexes them, and Kibana visualizes insights for operational troubleshooting.  
- **Containerized Deployment**: Docker and Docker Compose used for consistent environments; deployed on AWS Ubuntu VM.  
- **Database Reliability**: Monitored MySQL wallet database, ensured connectivity, and used optimized queries for faster transaction tracking.  
- **Business Metrics Tracking**: Logged user actions, wallet transactions, and failed operations to maintain audit compliance and operational transparency.  
- **Operational Readiness**: Designed to demonstrate system uptime management, alerting, and monitoring suitable for ICT or fintech operations roles.  


