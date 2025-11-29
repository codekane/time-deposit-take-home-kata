# Time Deposit Refactoring Kata

A RESTful API for managing time deposits with interest calculation.

## Prerequisites

- Java 17+
- Maven 3.8+
- Docker (for running tests and local PostgreSQL)

## Quick Start

### 1. Start PostgreSQL

```bash
docker-compose up -d
```

This starts PostgreSQL on port 5433 (to avoid conflicts with local PostgreSQL installations).

### 2. Run the Application

```bash
mvn spring-boot:run
```

### 3. Access Swagger UI

Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### 4. Stop PostgreSQL

```bash
docker-compose down
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/time-deposits` | Retrieve all time deposits with their withdrawals |
| POST | `/time-deposits/update-balances` | Apply monthly interest to all time deposits |

### Using Swagger UI

1. Navigate to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
2. Expand the "Time Deposits" section
3. Click on an endpoint, then "Try it out", then "Execute"

### Using cURL

```bash
# Get all time deposits
curl -X GET http://localhost:8080/time-deposits

# Update all balances (apply interest)
curl -X POST http://localhost:8080/time-deposits/update-balances
```

## Running Tests

```bash
mvn test
```

Tests include:
- **Characterization tests**: Verify existing `TimeDepositCalculator` behavior
- **Integration tests**: Full API tests using Testcontainers with PostgreSQL

Note: Docker must be running for integration tests. Tests use Testcontainers, which spins up isolated containers automatically.

## Interest Calculation Rules

| Plan Type | Annual Rate | Conditions |
|-----------|-------------|------------|
| Basic | 1% | No interest for first 30 days |
| Student | 3% | No interest for first 30 days; no interest after 1 year |
| Premium | 5% | No interest for first 45 days |

Interest is calculated monthly: `balance * (rate / 12)`

## Architecture

This project follows a simplified Hexagonal Architecture:

```
org.ikigaidigital/
├── TimeDeposit.java              # Domain entity (shared, unchanged)
├── TimeDepositCalculator.java    # Domain logic (shared, unchanged)
├── TimeDepositApplication.java   # Spring Boot entry point
├── domain/
│   └── Withdrawal.java           # Domain entity
├── application/
│   └── TimeDepositService.java   # Application service
└── adapter/
    ├── web/                      # REST controllers and DTOs
    └── persistence/              # JPA repositories
```

### Design Decisions

1. **No breaking changes to shared classes**: `TimeDeposit` and `TimeDepositCalculator` remain in the root package with their original signatures intact. JPA annotations were added to `TimeDeposit` as metadata (not a breaking change).

2. **DTO pattern for API responses**: `TimeDepositResponse` includes withdrawals without modifying the shared `TimeDeposit` class.

3. **Protected no-arg constructor**: Added to `TimeDeposit` for JPA compatibility. This is an additive change that doesn't break existing consumers.

4. **Service delegates to existing calculator**: `TimeDepositService.updateAllBalances()` uses the original `TimeDepositCalculator` to ensure identical behavior.