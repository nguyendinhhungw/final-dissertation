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

## 🚀 Cách chạy project
📦 Yêu cầu hệ thống
Node.js (>= 18)
npm hoặc yarn
(Khuyến nghị) Git
### 1. Clone repository
```bash
git clone <your-repo-url>
Cài đặt dependencies: npm install

### 2. Cấu hình database

application.properties
hoặc
application.yml
### 3. Chạy ứng dụng
./mvnw spring-boot:run
npm run dev
👉 Sau khi chạy thành công, truy cập:
http://localhost:8080

(hoặc port được trả về trong terminal)
🔐 Cấu hình môi trường (Environment Variables)

Dự án sử dụng file .env để lưu trữ các biến môi trường quan trọng như:

API keys
Database URL
Supabase keys
Các thông tin cấu hình hệ thống

⚠️ Lưu ý quan trọng về bảo mật:

File .env KHÔNG được commit lên GitHub
File này đã được thêm vào .gitignore
Điều này nhằm đảm bảo an toàn cho hệ thống và dữ liệu
📩 Cách lấy file .env và tài khoản truy cập




🧱 Kiến trúc hệ thống

Dự án được tổ chức theo mô hình:

backend/src/main/java/com/merryblue/api/
├── aspect/      # AOP: Cross-cutting concerns (logging, etc.)
├── config/      # Security, JPA Auditing, Data Seeding
├── constant/    # Constants & message codes
├── controller/  # REST API endpoints (Admin, Public, User)
├── dto/         # Request/Response objects
├── exception/   # Global error handling
├── mapper/      # Entity ↔ DTO mapping
├── model/       # JPA Entities & BaseEntity
├── repository/  # Data access (Spring Data JPA)
├── security/    # JWT & authentication logic
├── service/     # Business logic
└── util/        # Helper utilities

Vì đây là đề tài khóa luận tốt nghiệp, để đảm bảo tính bảo mật:

👉 Nếu bạn cần:

Chạy project đầy đủ
Truy cập hệ thống
Xem giao diện quản trị (Admin Dashboard)

Vui lòng liên hệ trực tiếp qua email:

📧 Email: [dinhhung110310@gmail.com]

Bạn sẽ được cung cấp:

File .env cần thiết
Tài khoản admin để trải nghiệm toàn bộ chức năng hệ thống

💡 Ghi chú
Nếu gặp lỗi khi chạy project, hãy kiểm tra:
Phiên bản Node.js
File .env đã được cấu hình đúng chưa
Có thể cần restart server sau khi thay đổi biến môi trường
👨‍💻 Liên hệ .
Email: [dinhhung110310@gmail.com]