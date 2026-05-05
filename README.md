# 🎨 SOURCE CODE – ĐỀ TÀI TÁI THIẾT KẾ WEBSITE

🚀 Dự án tái thiết kế và phát triển lại một website với mục tiêu:
- Cải thiện **UI/UX**
- Tối ưu **hiệu năng**
- Nâng cấp kiến trúc **backend/frontend**

---

## 📌 Giới thiệu

Dự án này tập trung vào:

- 🔍 Phân tích hệ thống website cũ  
- ⚠️ Xác định các vấn đề về trải nghiệm người dùng và hiệu năng  
- 🎨 Thiết kế lại giao diện hiện đại hơn  
- ⚙️ Xây dựng lại hệ thống với công nghệ mới  

---

## 🛠️ Công nghệ sử dụng

### 🔹 Backend
- Java Spring Boot  
- RESTful API  
- JPA / Hibernate  

### 🔹 Frontend
- HTML / CSS / JavaScript / ReactJS / TypeScript  

---

## ✨ Tính năng chính

- 🔐 Xác thực & phân quyền người dùng  
- 📄 Quản lý nội dung (CMS)  
- 🔎 Tìm kiếm và lọc dữ liệu  
- ⚡ Tối ưu hiệu năng so với hệ thống cũ  
- 🎨 Giao diện mới thân thiện và hiện đại  

---

🚀 Cách chạy project
📦 Yêu cầu hệ thống
Node.js (>= 18)
npm hoặc yarn
(Khuyến nghị) Git
🔹 1. Clone repository
git clone <your-repo-url>
cd <your-project-folder>
🔹 2. Cài đặt dependencies (Frontend)
npm install
🔹 3. Cấu hình Backend (Spring Boot)

Chỉnh sửa file cấu hình:

application.properties
hoặc
application.yml

👉 Cập nhật các thông tin:

Database connection
JWT secret (nếu có)
Các cấu hình cần thiết khác
🔹 4. Chạy ứng dụng
▶️ Backend (Spring Boot)
./mvnw spring-boot:run
▶️ Frontend (React + TypeScript)
npm run dev
🌐 Truy cập hệ thống
Backend API: http://localhost:8080
Frontend: http://localhost:3000 (hoặc port hiển thị trong terminal)
🔐 Cấu hình môi trường (Environment Variables)

Dự án sử dụng file .env để lưu trữ các thông tin cấu hình quan trọng:

API Keys
Database URL
Supabase Keys
Các biến môi trường khác
⚠️ Lưu ý bảo mật
File .env KHÔNG được commit lên GitHub
File này đã được thêm vào .gitignore
Điều này nhằm bảo vệ dữ liệu và tránh rò rỉ thông tin nhạy cảm
📩 Truy cập hệ thống & dữ liệu

Vì đây là đề tài khóa luận tốt nghiệp, một số thông tin được giới hạn truy cập nhằm đảm bảo tính bảo mật.

👉 Nếu bạn cần:
Chạy project đầy đủ
Truy cập hệ thống
Trải nghiệm giao diện quản trị (Admin Dashboard)

Vui lòng liên hệ qua email:

📧 Email: dinhhung110310@gmail.com

📦 Thông tin sẽ được cung cấp
File .env cấu hình môi trường
Tài khoản Admin để truy cập hệ thống
🔒 Quy định sử dụng
Chỉ sử dụng cho mục đích:
Học tập
Đánh giá đồ án
Không chia sẻ lại hoặc sử dụng vào mục đích thương mại
🧱 Kiến trúc hệ thống (Backend)
backend/src/main/java/com/merryblue/api/
├── aspect/      # AOP: Logging, xử lý cross-cutting concerns
├── config/      # Security, JPA Auditing, Data Seeding
├── constant/    # Hằng số & message code
├── controller/  # REST API (Admin, Public, User)
├── dto/         # Request / Response objects
├── exception/   # Global Exception Handling
├── mapper/      # Mapping Entity ↔ DTO
├── model/       # JPA Entities & BaseEntity
├── repository/  # Data Access Layer (Spring Data JPA)
├── security/    # JWT & Authentication
├── service/     # Business Logic
└── util/        # Helper Utilities
💡 Ghi chú
Kiểm tra đúng phiên bản Node.js trước khi chạy
Đảm bảo file .env đã được cấu hình đầy đủ
Restart server sau khi thay đổi biến môi trường
👨‍💻 Liên hệ
📧 Email: dinhhung110310@gmail.com