# Hệ Thống Quản Lý Bảo Hành Xe Điện (EV Warranty System)

## Mục lục

1. [Giới Thiệu Sản Phẩm](#1-giới-thiệu-sản-phẩm)
2. [Công Nghệ Sử Dụng](#2-công-nghệ-sử-dụng)
3. [Cấu Trúc Cấu Hình (Configuration)](#3-cấu-trúc-cấu-hình-configuration)
4. [Hướng Dẫn Cài Đặt và Chạy Ứng Dụng (Local)](#4-hướng-dẫn-cài-đặt-và-chạy-ứng-dụng-local)
5. [Danh Sách Tài Khoản Thử Nghiệm](#5-danh-sách-tài-khoản-thử-nghiệm)

## 1. Giới Thiệu Sản Phẩm

EV Warranty System là nền tảng quản lý bảo hành chuyên dụng cho hệ sinh thái xe điện, đóng vai trò cầu nối dữ liệu giữa Nhà sản xuất (OEM) và các Trung tâm dịch vụ (Service Centers). Hệ thống giúp chuẩn hóa quy trình tiếp nhận, xử lý yêu cầu bảo hành và quản trị dữ liệu vận hành của phương tiện.

### Các phân hệ chức năng chính:

- **Service Center (Dành cho đại lý và xưởng dịch vụ):**
  - Quản lý thông tin xe, lịch sử sửa chữa và tình trạng linh kiện.
  - Phân tích rủi ro lỗi linh kiện bằng AI: Dự báo các bộ phận có khả năng hỏng hóc cao dựa trên dữ liệu vận hành và lịch sử dòng xe.
  - Tiếp nhận và tạo yêu cầu bảo hành (Warranty Claims) trực tuyến.
- **Manufacturer (Dành cho hãng sản xuất):**
  - Giám sát toàn bộ hoạt động bảo hành trên hệ thống qua biểu đồ thời gian thực.
  - Phê duyệt hoặc từ chối các yêu cầu bảo hành từ phía đại lý.
  - Quản lý danh mục linh kiện, chính sách bảo hành và các chiến dịch triệu hồi (Recalls).
- **Administration (Quản trị hệ thống):**
  - Quản lý người dùng, phân quyền chi tiết theo vai trò (Role-based Access Control).
  - Tối ưu hóa trải nghiệm người dùng với hệ thống trang báo lỗi chuyên nghiệp (403, 404, 500) giúp quá trình vận hành ổn định.

## 2. Công Nghệ Sử Dụng

Hệ thống được phát triển trên kiến trúc nền tảng Java hiện đại (LTS mới nhất), tập trung vào tính bảo mật và khả năng xử lý dữ liệu tập trung.

### Backend

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)

### Frontend

![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005F0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)
![Bootstrap](https://img.shields.io/badge/bootstrap-%238511FA.svg?style=for-the-badge&logo=bootstrap&logoColor=white)
![jQuery](https://img.shields.io/badge/jQuery-0769AD?style=for-the-badge&logo=jquery&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=for-the-badge&logo=css3&logoColor=white)

### Database

![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-316192?style=for-the-badge&logo=postgresql&logoColor=white)

### DevOps & Tools

![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

## 3. Cấu Trúc Cấu Hình (Configuration)

Hệ thống sử dụng cơ chế **Profile** của Spring Boot để tách biệt hoàn toàn môi trường Phát triển (Dev) và Sản phẩm (Production).

| Môi trường       | Profile   | File Cấu Hình                 | Mô tả & Bảo Mật                                                                                                                 |
| :--------------- | :-------- | :---------------------------- | :------------------------------------------------------------------------------------------------------------------------------ |
| **Local IDE**    | `default` | `application.properties`      | Chạy trực tiếp trên máy (IntelliJ/Eclipse). Password DB: `evwarranty123`. **Không push lên Git.**                               |
| **Local Docker** | `dev`     | `application-dev.properties`  | Chạy bằng `docker-compose`. Password DB: `evwarranty123`. **Không push lên Git.**                                               |
| **Production**   | `prod`    | `application-prod.properties` | Chạy trên Server (Railway/Render). **Không chứa Password thật.** Sử dụng biến môi trường `${ENV_VAR}`. An toàn để push lên Git. |

## 4. Hướng Dẫn Cài Đặt và Chạy Ứng Dụng (Local)

### 4.1. Chạy bằng Docker Compose (Khuyên dùng)

Đây là cách nhanh nhất để dựng toàn bộ môi trường (App + Database) mà không cần cài Java/Postgres trên máy.

1. Tại thư mục gốc dự án, chạy lệnh:

   ```bash
   docker-compose up -d --build
   ```

   _(Lệnh này sẽ tự động tải Java 21, build ứng dụng và khởi tạo Database PostgreSQL 17)_.

2. Truy cập ứng dụng: `http://localhost:8080`.
   - Database Port: `5432` (nội bộ Docker) được map ra ngoài máy chủ ở cổng `5433` (hoặc `5432` tuỳ `docker-compose.yml`).

### 4.2. Chạy Thủ Công (Development Mode)

Dành cho việc phát triển và debug code trên IDE.

1. Khởi chạy riêng Database bằng Docker:
   ```bash
   docker-compose up -d evwarranty-db
   ```
2. Chạy ứng dụng bằng lệnh Maven hoặc Run trong IDE:
   ```bash
   mvn spring-boot:run
   ```
   _(Lưu ý: Cần cài đặt JDK 21 trên máy để chạy lệnh này)._

## 5. Danh Sách Tài Khoản Thử Nghiệm

Bạn có thể sử dụng các tài khoản sau để kiểm tra quy trình nghiệp vụ của từng vai trò:

| Vai trò        | Tên đăng nhập | Mật khẩu    | Chức năng kiểm thử                          |
| :------------- | :------------ | :---------- | :------------------------------------------ |
| Quản trị viên  | admin         | password123 | Toàn quyền quản trị và xem dashboard        |
| Nhân viên SC   | scstaff       | password123 | Tiếp nhận xe và tạo yêu cầu bảo hành        |
| Kỹ thuật viên  | sctech        | password123 | Xem phân tích AI và thực hiện sửa chữa      |
| Nhân viên Hãng | evmstaff      | password123 | Duyệt yêu cầu bảo hành và quản lý linh kiện |
