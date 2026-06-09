# 🍽️ QuanLyNhaHang - Restaurant Management System

A full-stack restaurant management platform built with Spring Boot and React, providing online reservations, food ordering, real-time customer support, payment gateway integration, and business analytics.

---

## 📖 Overview

QuanLyNhaHang is designed to automate restaurant operations through a centralized system that supports:

* Online table reservations
* Food ordering and delivery management
* Real-time customer support chat
* Online payments via VNPay and MoMo
* Menu and category management
* Revenue and business analytics dashboard
* JWT-based authentication and authorization

The project follows a layered architecture (Controller → Service → Repository) and focuses on maintainability, scalability, and clean separation of concerns.

---

## 🚀 Technology Stack

### Backend

* Java 8
* Spring Boot 2.7.18
* Spring Security
* JWT Authentication (JJWT)
* Spring Data JPA (Hibernate)
* Microsoft SQL Server
* Spring Data Elasticsearch
* WebSocket + STOMP + SockJS
* Lombok
* ModelMapper
* OpenAPI / Swagger

### Frontend

* ReactJS
* Vite
* Axios
* WebSocket Client

### Payment Integration

* VNPay
* MoMo

### Development Tools

* Maven
* Postman
* Git & GitHub

---

## ⚙️ Key Features

### 👤 Customer

#### Authentication

* User registration
* Secure login with JWT
* Refresh token support

#### Food Ordering

* Browse menu and categories
* Search food items
* Add products to cart
* Place delivery orders

#### Reservation System

* Online table booking
* Automatic table allocation based on:

  * Number of guests
  * Reservation time
  * Existing reservations

#### Online Payment

* VNPay integration
* MoMo integration
* Payment status synchronization via IPN/Webhook

#### Reviews & Feedback

* Submit food reviews
* Rate dining experiences

#### Realtime Support

* Chat directly with administrators
* Instant messaging via WebSocket

---

### 🛠️ Admin & Staff

#### User Management

* Manage customer accounts
* Role-based authorization

#### Food Management

* CRUD food items
* CRUD food categories
* Update pricing and availability

#### Reservation Management

* Confirm reservations
* Cancel reservations
* Monitor booking schedules

#### Order Management

* Track order status
* Update delivery progress
* Manage payment information

#### Dashboard & Analytics

* Revenue statistics
* Reservation reports
* Popular food reports
* Daily, weekly and monthly analytics

#### Customer Support

* Realtime communication with customers

---

## 💳 Payment Processing

### VNPay

Features:

* Payment URL generation
* HMAC SHA512 signature verification
* IPN callback processing
* Automatic order status updates

### MoMo

Features:

* Payment request creation
* HMAC SHA256 verification
* IPN callback handling
* Transaction synchronization

---

## 💬 Realtime Chat System

Implemented using:

* Spring WebSocket
* STOMP Messaging Protocol
* SockJS Fallback

Workflow:

1. Customer sends message
2. Admin receives notification instantly
3. Admin responds in real-time
4. Messages are delivered without page refresh

---

## 🔍 Search System

The application integrates Elasticsearch to provide:

* Full-text search
* Fuzzy search
* Fast menu lookup

When Elasticsearch is unavailable, the application can still retrieve data through standard database queries.

---

## 🔐 Security

Security is implemented using:

* Spring Security
* JWT Authentication
* Role-Based Access Control (RBAC)
* BCrypt password encryption

Roles:

* ADMIN
* STAFF
* CUSTOMER

---

## 🗄️ Database Design

Main entities:

* User
* Role
* Food
* Category
* Reservation
* Table
* Order
* Bill
* Review
* Message
* ChatSession

Database:

* Microsoft SQL Server
* JPA / Hibernate ORM
* Foreign key constraints
* Transaction management

---

## 📂 Project Structure

```text
backend/
├── controller/
├── service/
├── repository/
├── model/
├── dto/
├── config/
├── security/
├── websocket/
├── payment/
└── util/

frontend/
├── src/
├── components/
├── pages/
├── services/
└── hooks/
```

---

## ▶️ Getting Started

### Clone Repository

```bash
git clone https://github.com/huutuandev/QuanLyNhaHang.git
```

### Backend Setup

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Frontend Setup

```bash
cd frontend
npm install
npm run dev
```

---

## 🔧 Environment Variables

Create a `.env` file:

```env
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=restaurant
DB_USERNAME=sa
DB_PASSWORD=your_password

JWT_SECRET=your_jwt_secret

VNPAY_HASH_SECRET=your_vnpay_secret

MOMO_SECRET_KEY=your_momo_secret
```

---

## 📡 Sample APIs

| Method | Endpoint           | Description          |
| ------ | ------------------ | -------------------- |
| POST   | /api/auth/login    | User login           |
| POST   | /api/auth/register | User registration    |
| POST   | /api/auth/refresh  | Refresh access token |
| GET    | /api/categories    | Get categories       |
| GET    | /api/foods         | Get foods            |
| POST   | /api/orders        | Create order         |
| POST   | /api/reservations  | Create reservation   |
| POST   | /api/payment/vnpay | VNPay payment        |
| POST   | /api/payment/momo  | MoMo payment         |

---

## 🌟 Highlights

* JWT + Refresh Token Authentication
* Online Reservation Management
* Realtime Chat with WebSocket
* VNPay & MoMo Integration
* Elasticsearch Search Engine
* Revenue Analytics Dashboard
* Layered Architecture (Controller → Service → Repository)

---

## 👨‍💻 Authors

### Backend Development

* Nguyen Huu Tuan

### Frontend Development

* Cong Huy
* Nguyen Huu Tuan

---

## 📄 License

This project was developed for educational purposes and portfolio demonstration.
