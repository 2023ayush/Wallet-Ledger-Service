# Ledger Wallet Service

A Spring Boot project implementing a wallet system with ledger transactions.

## Features

- Wallet management (top-up, withdraw)
- Ledger to record all transactions
- Transaction status: PENDING, SUCCESS, FAILED
- Audit-proof system using separate `LedgerAuditService`
- REQUIRES_NEW transactions ensure FAILED transactions are always logged
- JWT authentication support (if implemented)

## Project Structure

- `wallet` - Wallet entity and service logic
- `ledger` - Ledger entity, service, and repository
- `audit` - LedgerAuditService for saving FAILED transactions
- `auth` - User authentication and JWT handling

## API Endpoints

- `POST /ledger/{senderId}/transfer` - Transfer amount from sender to receiver
- `GET /ledger/{userId}/transactions` - Get all transactions for a user

## Example Request

**Transfer Money**

```http
POST /ledger/2/transfer
Content-Type: application/json
Authorization: Bearer <token>

{
  "receiverId": 3,
  "amount": 200.0
}
