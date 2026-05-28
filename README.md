# 🍽️ QuanLyNhaHang - Restaurant Management System

## 📌 Giới thiệu

Hệ thống quản lý nhà hàng được xây dựng bằng **Spring Boot**, hỗ trợ đầy đủ các chức năng:

* Đặt bàn online (Reservation)
* Thanh toán online (VNPay + MoMo)
* Chat realtime giữa khách hàng và admin
* Quản lý món ăn, đơn hàng, người dùng
* Dashboard thống kê
* Bảo mật với Spring Security + JWT

---

## 🚀 Công nghệ sử dụng

### Backend

* Java 17
* Spring Boot
* Spring Security (JWT Authentication)
* Spring Data JPA (Hibernate)
* WebSocket (Realtime Chat)
* MySQL

### Frontend

* ReactJS / Vite
* Axios
* Socket.io / WebSocket client

### Payment Integration

* VNPay
* MoMo

### Test API
* Postman
---
## ⚙️ Chức năng chính

### 👤 Người dùng

* Đăng ký / Đăng nhập (JWT)
* Xem menu món ăn
* Đặt bàn
* Đặt món
* Thanh toán online (VNPay / MoMo)
* Chat realtime với admin

### 🛠️ Admin

* Quản lý người dùng
* Quản lý món ăn
* Quản lý danh mục (Category)
* Quản lý đơn hàng
* Quản lý bàn (Table)
* Xem dashboard thống kê
* Chat với khách hàng

---

## 💳 Thanh toán

### VNPay

* Redirect sang cổng thanh toán VNPay
* Xử lý callback trả về
* Cập nhật trạng thái đơn hàng

### MoMo

* Tạo request thanh toán
* Xử lý IPN (Instant Payment Notification)
* Verify chữ ký bảo mật

---

## 💬 Realtime Chat

* Sử dụng WebSocket (Spring Boot)
* Chat giữa user và admin
* Cập nhật tin nhắn realtime không cần reload

---

## 🔐 Bảo mật

* Spring Security
* JWT Authentication
* Role-based Authorization (USER / ADMIN)
* Password mã hoá (BCrypt)

---

## 🗄️ Database

* MySQL
* Sử dụng JPA/Hibernate
* Quan hệ:

  * User
  * Order
  * Table
  * Reservation
  * Payment
  * Review

---

## 📂 Cấu trúc project

```
backend/
  ├── controller/
  ├── service/
  ├── repository/
  ├── model/
  ├── config/
  ├── security/
  ...

frontend/
  ├── src/
  ├── components/
  ├── pages/
  ...

---

## ▶️ Cách chạy project

### 1. Clone project

```
git clone https://github.com/huutuandev/QuanLyNhaHang.git
```

---

### 2. Backend

```
cd backend
mvn spring-boot:run
```

---

### 3. Frontend

```
cd frontend
npm install
npm run dev
```

---

## 🔧 Cấu hình môi trường

### application.properties

```
spring.datasource.url=jdbc:mysql://localhost:3306/restaurant
spring.datasource.username=root
spring.datasource.password=yourpassword

jwt.secret=your_secret_key
```

---

## 📡 API tiêu biểu

| Method | Endpoint           | Mô tả             |
| ------ | ------------------ | ----------------- |
| POST   | /api/auth/login    | Đăng nhập         |
| POST   | /api/auth/register | Đăng ký           |
| GET    | /api/foods         | Lấy danh sách món |
| POST   | /api/orders        | Tạo đơn hàng      |
| POST   | /api/payment/vnpay | Thanh toán VNPay  |

---

## 📈 Tính năng nổi bật

* ✅ Fullstack hoàn chỉnh (FE + BE)
* ✅ Thanh toán online thực tế (VNPay + MoMo)
* ✅ Chat realtime WebSocket
* ✅ Bảo mật chuẩn doanh nghiệp (JWT + Role)
* ✅ Kiến trúc rõ ràng (Controller → Service → Repository)

---

## 👨‍💻 Tác giả

* Be: Hữu Tuấn
* Fe: Công Huy + Hữu Tuấn

---

## ⭐ Ghi chú

Project phục vụ mục đích học tập và demo hệ thống quản lý nhà hàng thực tế.
