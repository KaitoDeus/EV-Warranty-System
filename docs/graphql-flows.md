# Biểu đồ Luồng GraphQL (GraphQL Flows)

Tài liệu này mô tả các luồng nghiệp vụ chính của hệ thống EV Warranty sử dụng giao diện GraphQL theo định dạng trực quan.

## 1. Luồng truy vấn AI Phán đoán (AI Analysis Flow)

Luồng này mô tả cách Frontend sử dụng GraphQL để lấy dữ liệu phân tích rủi ro cho xe.

```text
+-----------------------+              +-----------------------+              +------------------------+
|   CLIENT (FRONTEND)   |              |    GRAPHQL SERVICE    |              |   AI ENGINES / DATA    |
|   (Vehicle View)      |              |      (BACKEND)        |              |   (BUSINESS LOGIC)     |
+-----------+-----------+              +-----------+-----------+              +-----------+------------+
            |                                      |                                      |
            | (1) Query: aiPredictions             |                                      |
            |     { partName, riskLevel }          |                                      |
            +------------------------------------->|                                      |
            |                                      | (2) Fetch Fleet History              |
            |                                      +------------------------------------->|
            |                                      |                                      |
            |                                      | (3) Return Probability & Recs        |
            |                                      |<-------------------------------------+
            | (4) Trả về JSON Data                 |                                      |
            |<-------------------------------------+                                      |
            |                                      |                                      |
    [ Hiển thị Badge Màu ]                         |                                      |
```

## 2. Luồng nộp Yêu cầu Bảo hành (Warranty Claim Submission)

Quy trình từ lúc kỹ thuật viên phát hiện lỗi đến khi yêu cầu được lưu vào hệ thống qua Mutation.

```text
 [ NHÂN VIÊN TRUNG TÂM ]                        [ HỆ THỐNG GRAPHQL ]                      [ CSDL / STORAGE ]
           |                                             |                                       |
           | (1) Chọn linh kiện bị hỏng                  |                                       |
           |-------------------------------------------->|                                       |
           |                                             |                                       |
           | (2) Mutation: createClaim(input)            |                                       |
           |     (Mô tả lỗi, Hình ảnh, ODO)              |                                       |
           |-------------------------------------------->|                                       |
           |                                             |                                       |
           |                                             | (3) Kiểm tra hiệu lực bảo hành        |
           |                                             |-------------------------------------->|
           |                                             |                                       |
           |                                             | (4) Lưu Hồ sơ (Status: PENDING)       |
           |                                             |-------------------------------------->|
           |                                             |                                       |
           | (5) Confirm Success (ID, Claim#)            |                                       |
           |<--------------------------------------------|                                       |
```

## 3. Luồng Phê duyệt từ phía Hãng (Manufacturer Approval Flow)

Luồng xử lý khi nhân viên của hãng xe (EVM Staff) duyệt các yêu cầu từ đại lý.

```text
 [ NHÂN VIÊN HÃNG (EVM) ]                      [ HỆ THỐNG GRAPHQL ]                      [ TRUNG TÂM DỊCH VỤ ]
           |                                             |                                       |
           | (1) Query: pendingClaims()                  |                                       |
           |-------------------------------------------->|                                       |
           |                                             |                                       |
           | (2) Trả về Danh sách chờ duyệt              |                                       |
           |<--------------------------------------------|                                       |
           |                                             |                                       |
           | (3) Mutation: updateClaimStatus(...)        |                                       |
           |     (Status: APPROVED)                      |                                       |
           |-------------------------------------------->|                                       |
           |                                             |                                       |
           |                                             | (4) Notification (Subscription)       |
           |                                             |-------------------------------------->|
           |                                             |                                  [ Nhận Phụ tùng ]
```

## 4. Luồng Quản lý Linh kiện (Vehicle Part Assignment)

Luồng gắn định danh số Seri cho linh kiện khi xe xuất xưởng hoặc thay thế.

```text
     [ ADMIN / STAFF ]                         [ GRAPHQL MUTATION ]                      [ DATABASE ]
             |                                          |                                     |
             | (1) Quét mã VIN xe                       |                                     |
             +----------------------------------------->|                                     |
             |                                          |                                     |
             | (2) Gửi Mutation: assignPartToVehicle    |                                     |
             |     (Serial#, PartID, InstallDate)       |                                     |
             +----------------------------------------->|                                     |
             |                                          |                                     |
             |                                          | (3) Mapping Seri -> VIN             |
             |                                          +------------------------------------>|
             |                                          |                                     |
             | (4) Hoàn tất định danh                   |                                     |
             |<-----------------------------------------+                                     |
```
