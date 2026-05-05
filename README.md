🎨 SOURCE CODE – ĐỀ TÀI TÁI THIẾT KẾ WEBSITE

🚀 Dự án tái thiết kế và phát triển lại một website với mục tiêu cải thiện UI/UX, tối ưu hiệu năng, và nâng cấp kiến trúc hệ thống backend/frontend.

📌 Giới thiệu

Dự án này tập trung vào việc:

Phân tích hệ thống website cũ
Xác định các vấn đề về trải nghiệm người dùng và hiệu năng
Thiết kế lại giao diện hiện đại hơn
Xây dựng lại hệ thống với công nghệ mới
🛠️ Công nghệ sử dụng
🔹 Backend
Java Spring Boot
RESTful API
JPA / Hibernate
🔹 Frontend
HTML / CSS / JavaScript/ReactJS/Typescript

✨ Tính năng chính
🔐 Xác thực & phân quyền người dùng
📄 Quản lý nội dung (CMS)
🔎 Tìm kiếm và lọc dữ liệu
⚡ Tối ưu hiệu năng so với hệ thống cũ
🎨 Giao diện mới thân thiện và hiện đại
🚀 Cách chạy project
1. Clone repository
git clone <your-repo-url>
2. Cấu hình database
Sửa file application.properties hoặc application.yml
3. Chạy ứng dụng
./mvnw spring-boot:run
🧱 Kiến trúc hệ thống
Dự án được tổ chức theo mô hình:
backend/src/main/java/com/merryblue/api/
├── aspect/      # AOP: Cross-cutting concerns like global logging.
├── config/      # Configurations: Security, JPA Auditing, Data Seeding.
├── constant/    # Constants: Global strings and message codes.
├── controller/  # REST Controllers: API endpoints (Admin, Public, User).
├── dto/         # Data Transfer Objects: Request/Response schemas.
├── exception/   # Error Handling: Custom exceptions and global advice.
├── mapper/      # Mappers: Entity-to-DTO conversion logic.
├── model/       # Entities: JPA database models and BaseEntity.
├── repository/  # Data Access: Spring Data JPA and Specifications.
├── security/    # Auth: JWT filtering and UserPrincipal logic.
├── service/     # Business Logic: Service interfaces and implementations.
└── util/        # Utilities: Helper classes for Date, File, and Security.
