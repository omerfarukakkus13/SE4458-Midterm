# Short-Stay API Project (Group 2)

A high-performance, scalable RESTful API built with Java 21, Spring Boot 3.2+, Redis, and PostgreSQL 16, deployed natively on AWS EC2. This project implements a microservices-inspired architecture with a centralized API Gateway.

# Features
Microservices Architecture: Decoupled services for Gateway and Core API.

Java 21 Virtual Threads: Optimized for high-concurrency with minimal footprint.

High-Performance Caching: Redis integration to ensure sub-200ms response times.

API Gateway: Centralized routing, security, and request management.

Security: Stateless authentication using JWT (JSON Web Tokens).

Automated Documentation: Fully interactive Swagger UI.


# Tech Stack
Backend: Java 21 (OpenJDK), Spring Boot 3.x

Database: PostgreSQL 16 (Persistence), Redis (Caching)

Infrastructure: Spring Cloud Gateway, Spring Security (JWT)

Cloud: AWS EC2 (t3.small), Ubuntu 24.04 LTS

Testing: k6 (Load Testing)

# Why Java 21 & PostgreSQL 16?
This project utilizes the latest stable releases for maximum efficiency:

Java 21 Virtual Threads: Allows the t3.small instance to handle 100+ concurrent users (k6 stress test) without crashing, by using lightweight threads instead of expensive OS threads.

PostgreSQL 16: Improved query performance and better JSONB handling for flexible listing metadata.

Redis Optimization: Reduced p95 response times from ~1.1s to 164.19ms.

# Project Structure
```
.
├── api-gateway/         # Spring Cloud Gateway (Java 21)
├── short-stay-api/      # Core Logic & PostgreSQL 16 Integration
├── load-test.js         # k6 performance script
├── shortstay-key.pem    # AWS EC2 access key
└── README.md
```

# Prerequisites
Java 21

PostgreSQL 16

Redis Server

k6

# Deliverables
Demo Video: [(https://drive.google.com/file/d/1Uj78P8z_iZhHNHLFPvBLpzSa4PGK6f8s/view?usp=sharing)]

Swagger URL: [http://13.53.131.21:8081/swagger-ui.html]

GitHub Repo: [(https://github.com/omerfarukakkus13/SE4458-Midterm)]

# Getting Started (AWS Deployment)
1. Build
mvn clean package -DskipTests

2. Run with Performance Optimization
   
The application uses specific JVM flags to fit within t3.small's 2GB RAM:
```
nohup java -Xmx512m -jar short-stay-api.jar > backend.log 2>&1 &
nohup java -Xmx256m -jar api-gateway.jar > gateway.log 2>&1 &
```
# API Endpoints


# Auth Controller

Method,Route,Description,Auth Required

POST,/api/auth/register,Register a new user account,No

POST,/api/auth/login,Authenticate and retrieve JWT,No


# Listing Controller

Method,Route,Description,Auth Required

GET,/api/listings,Retrieve all available listings,No

GET,/api/listings/search,Search listings with filters,No

POST,/api/listings,Create a new listing,Yes

PUT,/api/listings/{id},Update an existing listing,Yes

DELETE,/api/listings/{id},Delete a listing,Yes

# Reservation Controller
Method,Route,Description,Auth Required

POST,/api/reservations,Create a new reservation,Yes

GET,/api/reservations/my-reservations,Get current user's reservations,Yes

GET,/api/reservations/listing/{listingId},Get all reservations for a specific listing,Yes

PATCH,/api/reservations/{id}/cancel,Cancel an active reservation,Yes

# Review Controller
Method	Route	Description	Auth Required

POST	/api/reviews	Submit a review for a completed stay	Yes

GET	/api/reviews/listing/{listingId}	Retrieve all reviews for a listing	No

# Admin Controller
Method,Route,Description,Auth Required

POST,/api/admin/upload,Upload administrative data/files,Yes (Admin)

GET,/api/admin/report,Generate and retrieve system reports,Yes (Admin)


# 🚀 Load Testing Report (k6)
Results (100 Concurrent Users)
Metric,Result
Avg Response Time,128.81ms
p95 Response Time,164.19ms (Threshold < 500ms Passed)
Throughput,35.14 requests/sec
Error Rate,0.00%
![k6test](https://github.com/user-attachments/assets/c05f13ed-5c94-4046-ac76-24fb5c703391)


# Data Model (ER Diagram)
```
erDiagram
    USERS {
        Long id PK
        String username UK
        Role role "Enum (HOST, GUEST, ADMIN)"
    }
    LISTINGS {
        Long id PK
        Integer noOfPeople
        String country
        String city
        Double price
    }
    RESERVATIONS {
        Long id PK
        LocalDate checkInDate
        LocalDate checkOutDate
        Double totalPrice
        ReservationStatus status "Enum (PENDING, CONFIRMED, CANCELLED)"
    }
    REVIEWS {
        Long id PK
        Integer rating "1-5"
        String comment "Length: 500"
    }

    USERS ||--o{ LISTINGS : "hosts"
    USERS ||--o{ RESERVATIONS : "makes (guest)"
    LISTINGS ||--o{ RESERVATIONS : "receives"
    RESERVATIONS ||--|| REVIEWS : "is reviewed in"
    LISTINGS ||--o{ REVIEWS : "has"
    USERS ||--o{ REVIEWS : "writes"
```
![erdiagram](https://github.com/user-attachments/assets/d4b7ac0a-8111-4002-b5f3-3e6f0c8b2784)




