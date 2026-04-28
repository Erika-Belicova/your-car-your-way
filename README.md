# Your Car Your Way

Your Car Your Way is a car rental application that allows users to rent vehicles internationally. This README documents the **Proof of Concept (PoC)** implementation of the real-time support chat feature, which enables users to contact support by initiating a support conversation and connecting live with a support agent directly within the platform.

The back-end is built with Java 21 and Spring Boot, using Spring Security with JWT authentication, WebSocket communication via STOMP over SockJS, and a PostgreSQL database. The front-end is built with Angular 21, using standalone components and reactive forms.

## Table of Contents

  - [1. Prerequisites](#1-prerequisites)  
  - [2. Clone the Project from GitHub](#2-clone-the-project-from-github)  
  - [3. Database Setup](#3-database-setup)  
  - [4. Set Environment Variables](#4-set-environment-variables)  
  - [5. Back-End Setup](#5-back-end-setup)  
  - [6. Front-End Setup](#6-front-end-setup)  
  - [7. Running the Application](#7-running-the-application)  
  - [8. PoC Functionalities](#8-poc-functionalities)  
    - [Back-End](#back-end)  
    - [Front-End](#front-end)  
  - [9. Documentation](#9-documentation)  
    - [Javadoc](#javadoc)  
    - [Swagger](#swagger)  
  - [10. Technologies & Libraries Used](#10-technologies--libraries-used)  
  - [11. Troubleshooting](#11-troubleshooting)

---

## 1. Prerequisites

Before running the application, you need the following tools installed:

- **Node.js** (version 24)  
- **Angular CLI** (version 21)  
- **PostgreSQL 18**  
- **Java 21**  
- **Maven**  

---

## 2. Clone the Project from GitHub

Open your terminal or command prompt and run:
```
git clone https://github.com/Erika-Belicova/your-car-your-way.git

cd your-car-your-way
```

The repository contains three main folders:

- **backend** – Spring Boot back-end  
- **frontend** – Angular front-end  
- **database** – SQL script to initialize the database

---

## 3. Database Setup

Make sure PostgreSQL 18 is installed and running.

### Create the Database

Open your terminal and connect to PostgreSQL:
```
psql -U postgres -p 5433
```

Create the database:
```
CREATE DATABASE ycyw_db;
```

Then exit psql:
```
\q
```

### Initialize the Database Schema

Run the SQL script from inside the `database` folder to create all tables:
```
cd database

psql -U postgres -p 5433 -d ycyw_db -f schema.sql
```

- This creates all necessary tables for the application.
- The database `ycyw_db` will be used by the back-end running on port 5433. 

### Add Test Users

The application requires at least one user and one support agent to test the chat feature. 

Generate BCrypt hashed passwords for your test accounts using [bcrypt-generator.com](https://bcrypt-generator.com). Then connect to the database to insert the test accounts manually including the hashed passwords you created.

Open your terminal and connect to PostgreSQL:
```
psql -U postgres -p 5433
```

Choose the database that you have created and initialized with the SQL script:
```
\c ycyw_db;
```

Run the following in psql after adding your hashed passwords:

```
INSERT INTO users (email, email_verified, password, first_name, last_name, date_of_birth, address, language, terms_accepted_at, privacy_accepted_at, is_active, support_access)
VALUES ('user@test.com', true, '<bcrypt_hashed_password>', 'Jean', 'Dupont', '1990-01-01', '1 rue de la Paix, Paris', 'fr', NOW(), NOW(), true, false);

INSERT INTO users (email, email_verified, password, first_name, last_name, date_of_birth, address, language, terms_accepted_at, privacy_accepted_at, is_active, support_access)
VALUES ('agent@test.com', true, '<bcrypt_hashed_password>', 'Marie', 'Martin', '1985-06-15', '2 avenue des Champs, Paris', 'fr', NOW(), NOW(), true, true);
```

The support access is defined by the support_access flag. You need to have at least one user of each kind.

- `support_access = false` - regular user role
- `support_access = true` - support agent role
- Use the same or different passwords for each account and make sure to remember them for logging in.

---

## 4. Set Environment Variables

Set the following system environment variables before running the back-end:

- `DB_USERNAME` → your PostgreSQL username
- `DB_PASSWORD` → your PostgreSQL password
- `JWT_SECRET` → a secret key used for signing and verifying JWT tokens

---

## 5. Back-End Setup

Navigate to the back-end directory and install dependencies:
```
cd backend

mvn clean install
```

Start the back-end application:

```
mvn spring-boot:run
```

- The API will be available at **http://localhost:8080**  

---

## 6. Front-End Setup

Navigate to the front-end directory and install dependencies:
```
cd frontend

npm install
```

Start the front-end application:

```
npm run start
```

- The Angular app will be available at **http://localhost:4200**  

---

## 7. Running the Application

1. Ensure Postgres is running and the `ycyw_db` database is accessible  
2. Start the back-end:
 
   ```
   cd backend
   
   mvn spring-boot:run
   ```
   
3. Start the front-end:
 
   ```
   cd frontend

   npm run start
   ```
   
4. Open your browser at **http://localhost:4200**

You should see the front-end UI, which will interact with the back-end API running at **http://localhost:8080**

---

## 8. PoC Functionalities

### Back-End

- **Authentication and Security:** JWT access and refresh tokens, role-based access control with Spring Security, token refresh interceptor
- **Support Conversations:** Create, retrieve and update support conversations with status management (OPEN, ACTIVE, WAITING, CLOSED)
- **Real-Time Chat:** WebSocket communication via STOMP over SockJS, message persistence, real-time status change notifications
- **Timeout Logic:** Automatic status transitions with notifications and timeouts to close inactive conversations:
  - OPEN: 5 minute agent busy notification for unanswered conversations
  - OPEN: 15 minute auto-close for conversations with no agent response
  - ACTIVE: 5 minute agent inactivity switch to WAITING
  - WAITING: 15 minute auto-close for paused conversations
- **DTOs & Mapping:** Request and response DTOs for secure data transfer
- **Exception Handling:** Global exception handler with custom exceptions for specific cases
- **Database:** PostgreSQL with JPA entities: User, SupportConversation, SupportMessage

### Front-End

- **Authentication:** Login with role-based redirect, JWT token management, automatic token refresh, route guards for role protection
- **User:** Dashboard with menu, support conversation list grouped by status with polling and manual refresh, support conversation detail with real-time WebSocket chat and message history
- **Support Agent:** Support conversation list with status management, polling and manual refresh, support conversation detail with chat controls and status management via WebSocket
- **Real-Time Updates:** WebSocket notifications for status changes, automatic UI updates without page refresh
- **Navigation & Routing:** Auth guard and role guard for protected routes, 404 page for invalid URLs
- **Forms & Validation:** Reactive forms with validation for support conversation creation
- **Data Flow:** RxJS Observables for async data handling, strongly typed DTOs and interfaces

---

## 9. Documentation

### Javadoc

Each class in the back-end has a Javadoc comment explaining its purpose.

To generate Javadoc locally:
```
cd backend

mvn javadoc:javadoc
```
- Open the generated documentation in your browser: **target/reports/apidocs/index.html**

### Swagger

The back-end API documentation is available at: **http://localhost:8080/swagger-ui/index.html**

- Provides details on all API endpoints for testing and reference  

--- 

## 10. Technologies & Libraries Used

**Back-End:** Java 21, Spring Boot 3.5, Spring Security, JWT, PostgreSQL 18, Maven, WebSocket, STOMP, JPA, Hibernate, Javadoc, Swagger UI

**Front-End:** Angular 21, Angular CLI 21, Node.js 24, TypeScript 5.9, STOMP.js, SockJS, RxJS, SCSS, Reactive Forms

---

## 11. Troubleshooting

- **Back-end does not start:** Verify that PostgreSQL is running on port 5433 and all environment variables are set.

- **Front-end does not start:** Verify that Node.js 24 is installed and run `npm install` before starting.
     
- **Database connection fails:** Confirm PostgreSQL is running on port 5433, the `ycyw_db` database exists and the `DB_USERNAME` and `DB_PASSWORD` environment variables match your PostgreSQL credentials.

- **Cannot log in:** Verify the test users were inserted correctly and that the password in the database is a valid BCrypt hash matching the password you are using to log in.

- **WebSocket connection fails:** Confirm the back-end is running at **http://localhost:8080**. WebSocket requires the back-end to be running before opening a support conversation.

- **Conversations not visible after creation:** The support conversation list refreshes every 30 seconds. Click the Refresh button for an immediate update.

- **API endpoints not responding:** Confirm the back-end is running at **http://localhost:8080** and the `JWT_SECRET` environment variable is set.


