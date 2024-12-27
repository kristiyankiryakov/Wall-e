# Wallet API

A Spring Boot REST API that enables user management and wallet operations including creating users, managing wallets, and handling deposits/withdrawals.

## Technologies Used

- Java 17
- Spring Boot
- PostgreSQL
- Docker
- JPA/Hibernate
- Maven

## Prerequisites

Before running this application, make sure you have the following installed:

- Java Development Kit (JDK) 17 or later
- Docker and Docker Compose
- Maven
- Git

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/kristiyankiryakov/Wall-e.git
cd Wall-e
```

### 2. Database Setup

The application uses PostgreSQL running in a Docker container. Start the database using Docker Compose:

```bash
docker compose up
```

This will start a PostgreSQL instance with the following configuration:
- Port: 5432
- Database: postgres
- Username: postgres
- Password: postgres

### 3. Configure Application

The application is configured using `application.yml`. The default configuration is:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

### 4. Build and Run

Build the application using Maven:

```bash
mvn clean install
```

Run the application:

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## API Endpoints

### Users

- Create User
  - POST `/api/users`
  - Creates a new user account

### Wallets

- Create Wallet
  - POST `/api/wallets/{userId}`
  - Creates a new wallet for a user

- Deposit Money
  - POST `/api/wallets/deposit/{userId}`
  - Adds funds to a wallet

- Withdraw Money
  - POST `/api/wallets/withdraw/{userId}`
  - Withdraws funds from a wallet

- View Balance
  - GET `/api/wallets/{userId}`
  - Retrieves the current wallet balance

## Testing

### Running Tests

Execute the test suite using Maven:

```bash
mvn test
```

### Manual Testing

You can use tools like Postman or curl to test the API endpoints. Here's an example using curl:

```bash
# Create a new user
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com", "password": "john123"}'

```