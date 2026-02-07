# Kịch Bản Kiểm Thử Phần Mềm (API Testing - GraphQL)

## Hệ Thống Quản Lý Bảo Hành Xe Điện

### 1. Data Validation Queries (Positive/Negative)

| ID        | Title                          | Query                                          | Steps                                                    | Test Data                                     | Expected                                                                   |
| :-------- | :----------------------------- | :--------------------------------------------- | :------------------------------------------------------- | :-------------------------------------------- | :------------------------------------------------------------------------- |
| **API01** | Lấy thông tin Xe thành công    | `query { vehicle(id: "V123") { vin, model } }` | 1. Gửi request valid vehicleId.<br>2. Kiểm tra response. | ID: `VH-001`                                  | JSON data có `vehicle` khác null.<br>`vin` khớp với DB.                    |
| **API02** | Lấy thông tin Xe không tồn tại | `query { vehicle(id: "XXX") { vin, model } }`  | 1. Gửi request invalid vehicleId.                        | ID: `INVALID`                                 | JSON data `vehicle` là null hoặc mảng lỗi (errors array) giải thích lý do. |
| **API03** | Lấy danh sách Claims chờ duyệt | `query { pendingClaims { id, status } }`       | 1. Đăng nhập quyền EVM Staff.<br>2. Thực hiện query.     | Header: `Authorization: Bearer <token_admin>` | Trả về mảng `pendingClaims`. Tất cả status phải là `PENDING`.              |

### 2. Mutations (Business Logic)

| ID        | Title                              | Operation                                                       | Steps                                                                           | Test Data                           | Expected                                                                                |
| :-------- | :--------------------------------- | :-------------------------------------------------------------- | :------------------------------------------------------------------------------ | :---------------------------------- | :-------------------------------------------------------------------------------------- |
| **API04** | Tạo Claim hợp lệ                   | `mutation { createClaim(input: { ... }) { id } }`               | 1. Input đầy đủ (vehicleId, errorDesc).<br>2. Gửi mutation.                     | VIN: Valid<br>Desc: "Battery Issue" | Tạo thành công. Trả về `id` mới. Status khởi tạo là `PENDING`.                          |
| **API05** | Tạo Claim thiếu trường bắt buộc    | `mutation { createClaim(input: { vehicleId: "V1" }) { id } }`   | 1. Bỏ trường `failureDescription`.                                              | Desc: Missing                       | Trả về lỗi Validation Error: "Field 'failureDescription' is missing". Không tạo record. |
| **API06** | Phê duyệt Claim (State Transition) | `mutation { updateClaimStatus(id: "CL1", status: "APPROVED") }` | 1. Chọn Claim đang `PENDING`.<br>2. Chuyển sang `APPROVED`.                     | Status: APPROVED                    | Thành công. Query lại `status` claim đó phải là `APPROVED`.                             |
| **API07** | Phê duyệt Claim sai quy trình      | `mutation { updateClaimStatus(id: "CL1", status: "APPROVED") }` | 1. Chọn Claim đã `REJECTED` (Final state).<br>2. Cố gắng chuyển lại `APPROVED`. | Status: REJECTED -> APPROVED        | Trả về lỗi Logic: "Không thể thay đổi trạng thái từ REJECTED sang APPROVED".            |

### 3. Error Handling

| ID        | Title                                | Operation                        | Steps                                                           | Test Data       | Expected                                                      |
| :-------- | :----------------------------------- | :------------------------------- | :-------------------------------------------------------------- | :-------------- | :------------------------------------------------------------ |
| **API08** | Lỗi sai kiểu dữ liệu (Type Mismatch) | `query { vehicle(id: 123) ... }` | 1. Truyền Int vào field mong đợi String/ID (nếu schema strict). | ID: `123` (Int) | Trả về lỗi cú pháp GraphQL hoặc Type Mismatch.                |
| **API09** | Rate Limiting (Performance)          | `query { ... }` (Spam)           | 1. Gửi 100 requests/giây.                                       | N/A             | Server trả về mã `429 Too Many Requests` sau ngưỡng cho phép. |
