# Hệ Thống Quản Lý Bảo Hành Xe Điện (OEM EV Warranty Management)

## 1. Giới Thiệu Sản Phẩm
Đây là giải pháp phần mềm toàn diện giúp kết nối **Nhà sản xuất xe điện (OEM)** và hệ thống **Trung tâm dịch vụ (Service Centers)**. Hệ thống được thiết kế để số hóa và tự động hóa quy trình bảo hành, từ lúc tiếp nhận xe đến khi hoàn tất sửa chữa và thanh toán chi phí bảo hành.

### Các phân hệ chức năng:
*   **Service Center (Dành cho Đại lý/Trung tâm sửa chữa):**
    *   Quản lý thông tin Xe và Khách hàng.
    *   Tạo và gửi Yêu cầu bảo hành (Warranty Claims).
    *   Theo dõi trạng thái sửa chữa và quản lý kho phụ tùng.
*   **Manufacturer (Dành cho Hãng sản xuất):**
    *   Thiết lập Chính sách bảo hành và Danh mục phụ tùng.
    *   Phê duyệt hoặc từ chối các Yêu cầu bảo hành từ đại lý.
    *   Quản lý các Chiến dịch dịch vụ (Recall/Campaigns).
*   **Administration (Quản trị):**
    *   Quản lý người dùng, phân quyền và cấu hình hệ thống.

## 2. Công Nghệ Sử Dụng
Dự án được xây dựng trên nền tảng Java Enterprise hiện đại, đảm bảo hiệu năng và khả năng mở rộng.

*   **Backend:**
    *   Language: **Java 17**
    *   Framework: **Spring Boot 3.2**
    *   Security: Spring Security (Role-based Access Control)
    *   Data: Spring Data JPA, Hibernate
*   **Frontend:**
    *   Template Engine: **Thymeleaf** (Server-side rendering)
    *   Styling: **Bootstrap 5**, Custom CSS (Premium Dark Theme)
    *   Icons: FontAwesome 6
*   **Database:**
    *   Production: **MySQL 8.0**
    *   Development: H2 In-Memory
*   **DevOps:**
    *   Containerization: **Docker**
    *   Orchestration: **Docker Compose**

## 3. Hướng Dẫn Cài Đặt & Chạy (Run Instructions)

### Cách 1: Chạy bằng Docker (Khuyên dùng)
Đây là cách đơn giản nhất để khởi chạy hệ thống bao gồm cả Ứng dụng và Cơ sở dữ liệu.

**Yêu cầu:** Đã cài đặt Docker và Docker Compose.

1.  Mở terminal tại thư mục gốc của dự án.
2.  Chạy lệnh sau để build và khởi động:
    ```bash
    docker-compose up -d --build
    ```
3.  Hệ thống sẽ khởi động tại: `http://localhost:8080`

### Cách 2: Chạy thủ công (Development Mode)
**Yêu cầu:** Java 17 SDK và Maven 3.8+.

1.  Cài đặt cơ sở dữ liệu MySQL và cập nhật thông tin trong `application.yml` (hoặc sử dụng H2 mặc định).
2.  Chạy ứng dụng bằng Maven:
    ```bash
    mvn spring-boot:run
    ```

## 4. Tài Khoản Demo (Credentials)

Hệ thống được khởi tạo sẵn các tài khoản với các vai trò khác nhau để kiểm thử:

| Vai trò (Role) | Username | Password | Quyền hạn chính |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `password123` | Toàn quyền hệ thống |
| **SC Staff** | `scstaff` | `password123` | Tiếp nhận xe, Tạo claim |
| **SC Technician** | `sctech` | `password123` | Xem nhiệm vụ, Sửa chữa |
| **EVM Staff** | `evmstaff` | `password123` | Duyệt claim, Quản lý phụ tùng |
