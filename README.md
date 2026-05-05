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

### 1. Clone repository
```bash
git clone <your-repo-url>
2. Cấu hình database

Sửa file:

application.properties
hoặc
application.yml
3. Chạy ứng dụng
./mvnw spring-boot:run
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