# Kịch Bản Kiểm Thử Bảo Mật (Security Test Cases)

## Hệ Thống Quản Lý Bảo Hành Xe Điện

### 1. Broken Access Control (BAC) & IDOR

| ID        | Title                                              | Test Scenario                            | Steps                                                                                                | Expected Result                                                           | Risk         |
| :-------- | :------------------------------------------------- | :--------------------------------------- | :--------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------ | :----------- |
| **SEC01** | Quyền hạn SC Staff (Horizontal IDOR)               | SC Staff xem được yêu cầu của SC khác.   | 1. Đăng nhập SC A.<br>2. Lấy ID Claim của SC B (`CL-999`).<br>3. Truy cập URL: `/claims/view/CL-999` | Trả về lỗi `403 Forbidden` hoặc `404 Not Found`. Không xem được chi tiết. | **Critical** |
| **SEC02** | Quyền hạn SC Staff (Vertical Privilege Escalation) | SC Staff truy cập trang Admin.           | 1. Đăng nhập SC Staff.<br>2. Thay đổi URL thành `/admin/users` hoặc `/admin/dashboard`.              | Trả về `403 Access Denied`. Redirect về trang Login hoặc Home.            | **Critical** |
| **SEC03** | Quyền hạn EVM Staff (Role Restriction)             | EVM Staff chỉnh sửa thông tin xe của SC. | 1. Đăng nhập EVM Staff.<br>2. Thử gọi API cập nhật thông tin VIN (`PUT /api/vehicles/VIN123`).       | Trả về lỗi `403 Forbidden`. Chỉ được phép xem (ReadOnly).                 | High         |

### 2. Injection Flaws (SQLi & GraphQL Injection)

| ID        | Title                                          | Test Scenario                       | Steps                                                                                   | Expected Result                                                                                           | Risk         |
| :-------- | :--------------------------------------------- | :---------------------------------- | :-------------------------------------------------------------------------------------- | :-------------------------------------------------------------------------------------------------------- | :----------- |
| **SEC04** | SQL Injection tại ô Tìm kiếm VIN               | Chèn mã độc SQL vào ô tìm kiếm.     | 1. Vào trang tìm kiếm xe.<br>2. Nhập: `' OR '1'='1` hoặc `VIN123'; DROP TABLE users;--` | Hệ thống báo lỗi "VIN không hợp lệ" hoặc trả về kết quả rỗng. Không hiển thị lỗi SQL thuần (Stack Trace). | **Critical** |
| **SEC05** | GraphQL Introspection (Information Disclosure) | Kiểm tra xem Schema có bị lộ không. | 1. Gửi request `POST /graphql` với body: `{ __schema { types { name } } }`              | Trả về lỗi hoặc `Introspection is disabled` (trên môi trường Prod). Nếu Dev thì OK.                       | Medium       |
| **SEC06** | GraphQL Resource Exhaustion (DoS)              | Gửi query lồng nhau quá sâu.        | 1. Gửi query: `{ vehicle { claims { vehicle { claims { ... } } } } }` (Depth > 5)       | Trả về lỗi `Query depth limit exceeded` hoặc timeout ngắn. Server không bị treo.                          | High         |

### 3. Cross-Site Scripting (XSS)

| ID        | Title                                          | Test Scenario                         | Steps                                                                                                                 | Expected Result                                                                                   | Risk         |
| :-------- | :--------------------------------------------- | :------------------------------------ | :-------------------------------------------------------------------------------------------------------------------- | :------------------------------------------------------------------------------------------------ | :----------- |
| **SEC07** | Stored XSS trong Mô tả lỗi (Claim Description) | Nhập script vào mô tả lỗi.            | 1. Tạo Claim mới.<br>2. Tại ô "Mô tả lỗi", nhập `<script>alert('XSS')</script>`.<br>3. Gửi và chờ Admin xem chi tiết. | Khi Admin xem, **không** có popup hiện lên. Chuỗi script hiển thị dưới dạng text thuần (Escaped). | **Critical** |
| **SEC08** | Reflected XSS tại thanh tìm kiếm               | Nhập script vào URL tham số tìm kiếm. | 1. Truy cập: `/search?q=<script>alert(1)</script>`                                                                    | Trang load bình thường, hiển thị "Không tìm thấy kết quả cho..." (Escaped). Không chạy script.    | High         |

### 4. Authentication & Session Management

| ID        | Title                  | Test Scenario                          | Steps                                                                                            | Expected Result                                                          | Risk   |
| :-------- | :--------------------- | :------------------------------------- | :----------------------------------------------------------------------------------------------- | :----------------------------------------------------------------------- | :----- |
| **SEC09** | Brute Force Protection | Thử đăng nhập sai nhiều lần liên tiếp. | 1. Nhập sai mật khẩu user `admin` 5-10 lần liên tiếp.                                            | Tài khoản bị khóa tạm thời (Lockout) hoặc yêu cầu CAPTCHA.               | High   |
| **SEC10** | Session Timeout        | Kiểm tra thời gian hết phiên làm việc. | 1. Đăng nhập.<br>2. Để yên không thao tác trong 30 phút (tùy cấu hình).<br>3. Thử tải lại trang. | Tự động đăng xuất và chuyển về trang Login.                              | Medium |
| **SEC11** | Weak Password Policy   | Đặt mật khẩu quá đơn giản.             | 1. Đổi mật khẩu thành `123456` hoặc `password`.                                                  | Hệ thống từ chối, yêu cầu độ mạnh (Chữ hoa, thường, số, ký tự đặc biệt). | Medium |
