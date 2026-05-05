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

### 📦 Yêu cầu hệ thống

* Node.js (>= 18)
* npm hoặc yarn
* (Khuyến nghị) Git

---

### 🔹 1. Clone repository

```bash
git clone <your-repo-url>
cd <your-project-folder>
```

---

### 🔹 2. Cài đặt dependencies (Frontend)

```bash
npm install
```

---

### 🔹 3. Cấu hình Backend (Spring Boot)

Chỉnh sửa file:

* `application.properties`
  hoặc
* `application.yml`

👉 Cập nhật:

* Database connection
* JWT secret (nếu có)
* Các cấu hình cần thiết khác

---

### 🔹 4. Chạy ứng dụng

#### ▶️ Backend (Spring Boot)

```bash
./mvnw spring-boot:run
```

#### ▶️ Frontend (React + TypeScript)

```bash
npm run dev
```

---

### 🌐 Truy cập hệ thống

http://localhost:8080(hoặc port hiển thị trong terminal)*

---

## 🔐 Cấu hình môi trường (Environment Variables)

Dự án sử dụng file `.env` để lưu trữ:

* API Keys
* Database URL
* Supabase Keys
* Các biến môi trường khác

### ⚠️ Lưu ý bảo mật

* File `.env` **KHÔNG được commit lên GitHub**
* File đã được thêm vào `.gitignore`
* Điều này giúp tránh rò rỉ thông tin nhạy cảm

---

## 📩 Truy cập hệ thống & dữ liệu

Vì đây là **đề tài khóa luận tốt nghiệp**, một số thông tin được giới hạn.

### 👉 Nếu bạn cần:

* Chạy project đầy đủ
* Truy cập hệ thống
* Trải nghiệm Admin Dashboard

📧 **Liên hệ:** [dinhhung110310@gmail.com](mailto:dinhhung110310@gmail.com)

---

### 📦 Thông tin sẽ được cung cấp

* File `.env`
* Tài khoản Admin

---

### 🔒 Quy định sử dụng

* Chỉ phục vụ:

  * Học tập
  * Đánh giá đồ án
* Không chia sẻ lại hoặc dùng cho mục đích thương mại

---

## 🧱 Kiến trúc hệ thống (Backend)

```bash
backend/src/main/java/com/merryblue/api/
├── aspect/      # Logging, AOP
├── config/      # Security, JPA, Data Seeding
├── constant/    # Constants
├── controller/  # REST API
├── dto/         # Request/Response
├── exception/   # Error handling
├── mapper/      # Mapping
├── model/       # Entities
├── repository/  # Data access
├── security/    # JWT
├── service/     # Business logic
└── util/        # Utilities
```

---

## 💡 Ghi chú

* Kiểm tra đúng version Node.js
* Đảm bảo `.env` đã cấu hình
* Restart server nếu thay đổi env

---

## 👨‍💻 Liên hệ

📧 [dinhhung110310@gmail.com](mailto:dinhhung110310@gmail.com)
