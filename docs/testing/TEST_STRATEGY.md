# Chiến Lược Kiểm Thử Phần Mềm (Software Testing Strategy)

## Dự án: EV Warranty Management System

### 1. Mục Tiêu (Objective)

Đảm bảo hệ thống EV Warranty hoạt động ổn định, đáp ứng đầy đủ các yêu cầu nghiệp vụ về quản lý bảo hành xe điện, xử lý yêu cầu (claims), dự báo AI và quản trị hệ thống. Đồng thời đảm bảo tính bảo mật và hiệu năng của ứng dụng.

### 2. Phạm Vi Kiểm Thử (Test Scope)

#### 2.1. Phân Hệ Service Center (SC)

- Đăng nhập/Đăng xuất (SC Staff, Technician).
- Quản lý hồ sơ xe & Khách hàng.
- Tạo yêu cầu bảo hành (Warranty Claims).
- Xem dự báo lỗi linh kiện (AI Prediction).
- Cập nhật trạng thái sửa chữa.

#### 2.2. Phân Hệ Nhà Sản Xuất (Manufacturer - EVM)

- Dashboard giám sát hoạt động bảo hành.
- Phê duyệt/Từ chối yêu cầu bảo hành.
- Quản lý danh mục linh kiện & Chính sách.
- Quản lý chiến dịch triệu hồi (Recalls).

#### 2.3. Phân Hệ Quản Trị (Admin)

- Quản lý người dùng và phân quyền (RBAC).
- Cấu hình hệ thống.

#### 2.4. API & Bảo Mật

- Kiểm thử GraphQL API (Queries & Mutations).
- Kiểm thử bảo mật (Authentication, Authorization, Input Validation).

### 3. Môi Trường Kiểm Thử (Test Environment)

- **Hạ tầng:** Docker Containers (App + PostgreSQL).
- **Backend:** Java Spring Boot 3.2.
- **Frontend:** Web Browser (Chrome/Firefox/Edge latest).
- **Database:** PostgreSQL 15.
- **Công cụ:**
  - Postman/Insomnia (API Testing).
  - Chrome DevTools (UI/UX Debugging).
  - Burp Suite (Security Testing - Optional).

### 4. Các Loại Kiểm Thử (Test Types)

1.  **Functional Testing (UAT):** Kiểm tra chức năng nghiệp vụ.
2.  **Boundary Testing:** Kiểm tra các giá trị biên (VD: Mileage âm, ngày tháng không hợp lệ).
3.  **Negative Testing:** Kiểm tra khả năng xử lý lỗi của hệ thống khi nhập liệu sai.
4.  **Security Testing:** Kiểm tra quyền truy cập, XSS, Injection.
5.  **Interface/UI Testing:** Đảm bảo giao diện hiển thị đúng trên các thiết bị.

### 5. Quy Trình Báo Cáo Lỗi (Defect Reporting)

Mỗi lỗi phát hiện cần được ghi nhận với các thông tin:

- **ID:** Mã lỗi.
- **Title:** Mô tả ngắn gọn.
- **Steps to Reproduce:** Các bước để tái hiện lỗi.
- **Expected Result:** Kết quả mong đợi.
- **Actual Result:** Kết quả thực tế.
- **Severity:** Mức độ nghiêm trọng (Critical/High/Medium/Low).
- **Evidence:** Hình ảnh hoặc Logs đính kèm.
