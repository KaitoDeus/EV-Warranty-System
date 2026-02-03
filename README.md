# Hệ Thống Quản Lý Bảo Hành Xe Điện (EV Warranty System)

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

Hệ thống được phát triển trên kiến trúc nền tảng Java hiện đại, tập trung vào tính bảo mật và khả năng xử lý dữ liệu tập trung.

- **Backend:** Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA.
- **Frontend:** Thymeleaf, Bootstrap 5, Custom CSS, jQuery.
- **Database:** PostgreSQL 15.
- **Hạ tầng:** Docker, Docker Compose, Maven.

## 3. Hướng Dẫn Cài Đặt và Chạy Ứng Dụng

### 3.1. Triển khai bằng Docker (Chế độ khuyến dùng)

Yêu cầu máy tính đã cài đặt Docker và Docker Compose.

1. Truy cập vào thư mục gốc của dự án.
2. Chạy lệnh để khởi tạo toàn bộ hệ thống (bao gồm App và Database):
   ```bash
   docker-compose up -d --build
   ```
3. Truy cập ứng dụng tại địa chỉ: `http://localhost:8080`.
4. Cơ sở dữ liệu PostgreSQL sẽ lắng nghe tại cổng `5433` để tránh xung đột với các phiên bản Postgres cài sẵn trên máy.

### 3.2. Chạy cho mục đích phát triển (Development Mode)

Nếu bạn cần thực hiện chỉnh sửa mã nguồn và sử dụng tính năng nạp lại nhanh (Hot Reload):

1. Khởi chạy riêng cơ sở dữ liệu:
   ```bash
   docker-compose up -d evwarranty-db
   ```
2. Chạy ứng dụng thông qua Maven:
   ```bash
   mvn spring-boot:run
   ```

## 4. Quản Lý Cơ Sở Dữ Liệu

Để kết nối trực tiếp vào cơ sở dữ liệu qua các công cụ như pgAdmin hoặc DBeaver, sử dụng thông tin sau:

- **Host:** localhost
- **Port:** 5433
- **Database:** evwarranty
- **Username:** evwarranty
- **Password:** evwarranty123

## 5. Danh Sách Tài Khoản Thử Nghiệm

Bạn có thể sử dụng các tài khoản sau để kiểm tra quy trình nghiệp vụ của từng vai trò:

| Vai trò        | Tên đăng nhập | Mật khẩu    | Chức năng kiểm thử                          |
| :------------- | :------------ | :---------- | :------------------------------------------ |
| Quản trị viên  | admin         | password123 | Toàn quyền quản trị và xem dashboard        |
| Nhân viên SC   | scstaff       | password123 | Tiếp nhận xe và tạo yêu cầu bảo hành        |
| Kỹ thuật viên  | sctech        | password123 | Xem phân tích AI và thực hiện sửa chữa      |
| Nhân viên Hãng | evmstaff      | password123 | Duyệt yêu cầu bảo hành và quản lý linh kiện |
