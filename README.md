# Hệ Thống Quản Lý Bảo Hành Xe Điện (OEM EV Warranty Management)

## 1. Giới Thiệu Sản Phẩm

Đây là giải pháp phần mềm toàn diện giúp kết nối **Nhà sản xuất xe điện (OEM)** và hệ thống **Trung tâm dịch vụ (Service Centers)**. Hệ thống được thiết kế để số hóa và tự động hóa quy trình bảo hành, từ lúc tiếp nhận xe đến khi hoàn tất sửa chữa và thanh toán chi phí bảo hành.

### Các phân hệ chức năng:

- **Service Center (Dành cho Đại lý/Trung tâm sửa chữa):**
  - **Bảng điều khiển (Dashboard):** Thống kê trực quan về xe, trạng thái các yêu cầu bảo hành và chiến dịch dịch vụ.
  - Quản lý tiếp nhận xe và thông tin khách hàng.
  - Tạo và gửi Yêu cầu bảo hành (Warranty Claims) với quy trình duyệt tự động.
  - Theo dõi tiến độ sửa chữa và quản lý kho phụ tùng.
- **Manufacturer (Dành cho Hãng sản xuất):**
  - **Dashboard quản trị:** Giám sát toàn bộ hoạt động bảo hành trên toàn hệ thống qua các biểu đồ thời gian thực.
  - Thiết lập Chính sách bảo hành và Danh mục phụ tùng.
  - Quy trình phê duyệt chặt chẽ cho các Yêu cầu bảo hành từ đại lý.
  - Quản lý và kích hoạt các Chiến dịch dịch vụ (Recall/Campaigns).
- **Administration (Quản trị):**
  - Quản lý người dùng, phân quyền chi tiết (RBAC) và cấu hình hệ thống.

## 2. Công Nghệ Sử Dụng

Dự án được xây dựng trên nền tảng Java Enterprise hiện đại, đảm bảo hiệu năng và khả năng mở rộng.

- **Backend:**
  - Language: **Java 17**
  - Framework: **Spring Boot 3.2**
  - Security: Spring Security (Role-based Access Control)
  - Data: Spring Data JPA, Hibernate
- **Frontend:**
  - Template Engine: **Thymeleaf** (Server-side rendering với SSR stats cho Dashboard)
  - Styling: **Bootstrap 5**, Custom CSS (Premium Dark Theme, Responsive Layout)
  - JavaScript: jQuery, **API Client pattern** cho các module bảng biểu động.
  - Icons: FontAwesome 6
- **Database:**
  - Database Engine: **MySQL 8.0**
  - Schema Management: Spring SQL Init (init.sql)
- **DevOps:**
  - Containerization: **Docker**
  - Orchestration: **Docker Compose**

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

1.  Cài đặt cơ sở dữ liệu MySQL và tạo database `evwarranty`.
2.  Cập nhật thông tin kết nối (username/password) trong `src/main/resources/application.properties`.
3.  Chạy ứng dụng bằng Maven:
    ```bash
    mvn spring-boot:run
    ```

## 4. Tài Khoản Demo (Credentials)

Hệ thống được khởi tạo sẵn các tài khoản với các vai trò khác nhau để kiểm thử:

| Vai trò (Role)    | Username   | Password      | Quyền hạn chính               |
| :---------------- | :--------- | :------------ | :---------------------------- |
| **Administrator** | `admin`    | `password123` | Toàn quyền hệ thống           |
| **SC Staff**      | `scstaff`  | `password123` | Tiếp nhận xe, Tạo claim       |
| **SC Technician** | `sctech`   | `password123` | Xem nhiệm vụ, Sửa chữa        |
| **EVM Staff**     | `evmstaff` | `password123` | Duyệt claim, Quản lý phụ tùng |

## 5. Tiến Độ Dự Án (Project Progress)

- [x] **Core System:** Phân quyền Security (RBAC), Cấu hình Docker hoàn chỉnh.
- [x] **Admin Dashboard:** Thống kê tổng quan toàn hệ thống.
- [x] **Service Center Dashboard:** Đồng bộ dữ liệu Server-side, Quản lý xe và yêu cầu gần đây.
- [x] **EVM Staff Dashboard:** Bảng điều khiển duyệt claim, biểu đồ thống kê chiến dịch (Campaign).
- [x] **API Layer:** Hỗ trợ API Client đồng nhất cho Frontend.
- [ ] **Mobile Integration:** Giao diện tối ưu hơn cho thiết bị di động.
- [ ] **AI module:** Dự đoán lỗi xe dựa trên dữ liệu lịch sử.
