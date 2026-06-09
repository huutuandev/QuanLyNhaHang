🍽️ QuanLyNhaHang - Restaurant Management System
📌 Giới thiệu
Hệ thống quản lý nhà hàng là một nền tảng tự động hóa cấp doanh nghiệp (production-grade) được xây dựng bằng Spring Boot
. Hệ thống hỗ trợ đa người thuê (multi-tenant) với đầy đủ các chức năng
:
Đặt bàn online (Reservation) với thuật toán tự động xếp bàn chống trùng lặp
.
Thanh toán online (VNPay + MoMo) tích hợp đồng bộ trạng thái webhook
.
Chat realtime giữa khách hàng và admin qua WebSocket, STOMP và SockJS
.
Quản lý món ăn, danh mục, đơn hàng giao tận nơi và người dùng
.
Dashboard thống kê doanh thu, đơn đặt bàn và món ăn bán chạy
.
Bảo mật chặt chẽ với Spring Security, JWT và Refresh Token
.

--------------------------------------------------------------------------------
🚀 Công nghệ sử dụng
Backend
Java 1.8 (Oracle JDK / OpenJDK)
.
Spring Boot 2.7.18
.
Spring Security (JWT Authentication với JJWT 0.11.5)
.
Spring Data JPA & Hibernate
.
Cơ sở dữ liệu: MS SQL Server (JRE 8)
.
Công cụ tìm kiếm: Elasticsearch (Spring Data Elasticsearch)
.
WebSocket & STOMP (Realtime Chat với SockJS fallback)
.
ModelMapper & Lombok
.
OpenAPI / Swagger UI (springdoc-openapi-ui)
.
Frontend (Thông tin từ mẫu, không có trong tài liệu backend)
ReactJS / Vite
Axios
Socket.io / WebSocket client
Payment Integration
VNPay (HmacSHA512)
.
MoMo (HmacSHA256)
.
Test API (Thông tin từ mẫu)
Postman

--------------------------------------------------------------------------------
⚙️ Chức năng chính
👤 Người dùng (CUSTOMER)
Đăng ký / Đăng nhập bằng số điện thoại (JWT + Refresh Token)
.
Xem menu, danh mục món ăn và tìm kiếm có hỗ trợ gợi ý (Elasticsearch)
.
Đặt bàn (hệ thống tự động lọc bàn trống theo sức chứa và thời gian 2 tiếng)
.
Đặt món giao hàng (có giỏ hàng, tính tổng tiền và phí ship cố định 15.000 VND)
.
Thanh toán online bằng VNPay hoặc MoMo
.
Đánh giá và bình luận món ăn
.
Chat realtime với admin/staff qua /chat-websocket
.
🛠️ Admin / Staff
Quản lý phân quyền chặt chẽ (ADMIN, STAFF, CUSTOMER)
.
Quản lý menu, danh mục (FoodCategory, FoodEntity)
.
Quản lý đặt bàn (chu kỳ: Pending, Confirmed, Completed, Cancelled)
.
Cập nhật trạng thái đơn hàng và theo dõi thanh toán
.
Xem dashboard thống kê: tổng doanh thu, số lượng khách, tỷ lệ đặt bàn và món bán chạy nhất (lọc theo ngày, tuần, tháng)
.
Hỗ trợ khách hàng qua kênh chat nội bộ tại /topic/admin
.

--------------------------------------------------------------------------------
💳 Thanh toán
VNPay
Hỗ trợ xử lý webhook (IPN) qua cả GET và POST
.
Verify chữ ký bảo mật bằng vnpay.hash-secret với thuật toán HmacSHA512
.
Tự động trích xuất vnp_OrderInfo để cập nhật hóa đơn đặt bàn hoặc đơn giao hàng
.
MoMo
Xử lý IPN callback qua request POST
.
Verify chữ ký bảo mật bằng momo.secret-key với thuật toán HmacSHA256
.
Parse payload extraData để phân loại thanh toán (RESERVATION hoặc ORDER) và cập nhật database
.

--------------------------------------------------------------------------------
💬 Realtime Chat
Giao tiếp hai chiều sử dụng Spring WebSocket tích hợp giao thức STOMP và fallback SockJS
.
Khách hàng gửi tin nhắn qua /app/sendMessage, admin nhận tin tại /topic/admin
.
Admin phản hồi qua /app/adminSend và chuyển trực tiếp đến session riêng của khách tại /topic/session/{sessionId}
.

--------------------------------------------------------------------------------
🔐 Bảo mật
Spring Security kiểm soát Role-based Authorization
.
Mã hoá mật khẩu bằng thuật toán BCrypt
.
Xác thực qua Bearer Token (JWT), hỗ trợ cả Access Token và Refresh Token để duy trì phiên đăng nhập mượt mà
.
Tách biệt cấu hình bảo mật: Các secret key và thông tin database được đưa vào file .env động để tránh rò rỉ mã nguồn
.

--------------------------------------------------------------------------------
🗄️ Database
MS SQL Server với các ràng buộc khóa ngoại (Foreign Key) chặt chẽ
.
Kiến trúc tìm kiếm lai (Hybrid Search): Dùng Elasticsearch để tìm kiếm full-text, fuzzy search và autocomplete. Nếu Elasticsearch sập, hệ thống tự động fallback về JPA in-memory stream
.
Các thực thể chính: Users, UserRoles, FoodCategory, Food, Reservation, Bill, Orders, v.v.
.

--------------------------------------------------------------------------------
📂 Cấu trúc project
Áp dụng kiến trúc N-Tier chuẩn xác
:
backend/
  ├── controller/       (Nhận request, validate Model, trả về ApiResponse<T>)
  ├── service/          (Chứa logic nghiệp vụ, @Transactional, thuật toán)
  ├── repository/       (Kế thừa JpaRepository, truy vấn database)
  ├── integration/      (WebSocket broker, Elasticsearch, Cryptography)
  ├── config/           (Cấu hình EnvPostProcessor, OpenAPI)
  ├── security/         (Filter chain, JWT validation)
  ...
(Cấu trúc Frontend được giữ nguyên theo mẫu của người dùng, không nằm trong tài liệu)

--------------------------------------------------------------------------------
▶️ Cách chạy project
1. Clone project
git clone https://github.com/huutuandev/QuanLyNhaHang.git
2. Backend
cd backend
mvn spring-boot:run
3. Frontend (Theo mẫu cung cấp)
cd frontend
npm install
npm run dev

--------------------------------------------------------------------------------
🔧 Cấu hình môi trường
Hệ thống đã loại bỏ việc hardcode thông tin nhạy cảm trong application.properties
. Để chạy project, bạn cần cấu hình qua file .env
:
Tạo file .env ở thư mục gốc (hoặc thư mục backend) dựa trên backend/.env.example
:
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=restaurant
DB_USERNAME=sa
DB_PASSWORD=your_secure_password

JWT_SECRET=your_jwt_secret_key
VNPAY_HASH_SECRET=your_vnpay_secret
MOMO_SECRET_KEY=your_momo_secret

--------------------------------------------------------------------------------
📡 API tiêu biểu
Method
Endpoint
Mô tả
POST
/api/auth/login
Đăng nhập bằng số điện thoại và mật khẩu
POST
/api/auth/register
Đăng ký tài khoản người dùng
POST
/api/auth/refresh
Cấp lại Access Token mới
GET
/api/categories/**
Lấy danh mục món ăn (Public bypass)
POST
/api/orders
Tạo đơn hàng giao tận nơi
GET/POST
/api/payment/vnpay/ipn
Webhook cập nhật thanh toán VNPay
POST
/api/payment/momo/ipn
Webhook cập nhật thanh toán MoMo

--------------------------------------------------------------------------------
📈 Tính năng nổi bật
✅ Thuật toán xếp bàn tự động: Lọc sức chứa và tính toán thời gian tránh trùng lặp 100%
.
✅ Hệ thống tìm kiếm chống lỗi (Failover): Tìm kiếm siêu tốc qua Elasticsearch với cơ chế fallback tự động về SQL Database
.
✅ Bảo mật biến môi trường tự động: Quét file .env thông qua EnvPostProcessor can thiệp sâu vào quá trình khởi động Spring
.
✅ Thanh toán online tự động cập nhật bill qua IPN Webhooks
.
✅ Chat realtime hỗ trợ SockJS fallback cho mạng chập chờn
.

--------------------------------------------------------------------------------
👨‍💻 Tác giả
Be: Hữu Tuấn
Fe: Công Huy + Hữu Tuấn

--------------------------------------------------------------------------------
⭐ Ghi chú
Project phục vụ mục đích học tập và demo hệ thống quản lý nhà hàng thực tế. Cấu trúc README đã được cập nhật dựa trên thông số kỹ thuật backend chính thức.
