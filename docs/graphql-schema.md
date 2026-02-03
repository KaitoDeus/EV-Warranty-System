# GraphQL Schema Visualization

Tài liệu mô tả trực quan cấu trúc dữ liệu (Graph) của hệ thống.

## 1. Bản đồ Quan hệ Thực thể (Entity Relationship Map)

Sơ đồ dưới đây mô tả mối quan hệ giữa các đối tượng chính trong hệ thống: Xe, Linh kiện, và Yêu cầu bảo hành.

```text
       +-----------------+                        +-------------------+
       |     Vehicle     | 1                    * |    VehiclePart    |
       +-----------------+----------------------->+-------------------+
       | id              | has_many               | id                |
       | vin             |                        | serialNumber      |
       | model           |                        | status            |
       | manufactureDate |                        | warrantyEndDate   |
       +--------+--------+                        +---------+---------+
                |                                           | *
                | 1                                         |
                |                                           | is_type_of
                |                                           |
                |                                           v 1
                | has_many                        +---------+---------+
                |                                 |       Part        |
                v *                               +-------------------+
       +--------+--------+                        | id                |
       |  WarrantyClaim  |                        | name              |
       +-----------------+                        | category          |
       | id              |                        +-------------------+
       | claimNumber     |
       | status          |
       | submittedAt     |
       +-----------------+
```

## 2. Cấu trúc Đối tượng (Object Details)

Chi tiết các trường dữ liệu (Fields) cho từng đối tượng.

### A. Nhóm Xe & Linh Kiện

```text
+-------------------------------------------------------+
|  Vehicle (Xe)                                         |
+-----------------------------------+-------------------+
| Field Name                        | Type              |
+-----------------------------------+-------------------+
| id                                | ID!               |
| vin                               | String!           |
| mileage                           | Int               |
| installedParts                    | [VehiclePart]     | --> List of Parts
| warrantyClaims                    | [WarrantyClaim]   | --> List of Claims
| aiPredictions                     | [PredictionResult]| --> AI Analysis
+-----------------------------------+-------------------+
```

```text
+-------------------------------------------------------+
|  VehiclePart (Linh kiện gắn trên xe)                  |
+-----------------------------------+-------------------+
| Field Name                        | Type              |
+-----------------------------------+-------------------+
| id                                | ID!               |
| serialNumber                      | String!           |
| part                              | Part!             | --> Reference to Catalog
| status                            | String            | (ACTIVE, DEFECTIVE)
+-----------------------------------+-------------------+
```

### B. Nhóm Bảo hành & AI

```text
+-------------------------------------------------------+
|  WarrantyClaim (Yêu cầu bảo hành)                     |
+-----------------------------------+-------------------+
| Field Name                        | Type              |
+-----------------------------------+-------------------+
| id                                | ID!               |
| claimNumber                       | String!           |
| vehicle                           | Vehicle!          |
| failureDescription                | String            |
| status                            | String!           | (PENDING, APPROVED)
+-----------------------------------+-------------------+
```

```text
+-------------------------------------------------------+
|  PredictionResult (Kết quả dự báo AI)                 |
+-----------------------------------+-------------------+
| Field Name                        | Type              |
+-----------------------------------+-------------------+
| partName                          | String!           |
| failureProbability                | Float!            | (0.0 - 1.0)
| riskLevel                         | String!           | (CRITICAL, HIGH...)
| recommendedAction                 | String!           |
+-----------------------------------+-------------------+
```

## 3. GraphQL SDL Code (Tham khảo)

Dưới đây là mã nguồn định nghĩa Schema để import vào Server.

```graphql
type Query {
  vehicle(id: ID!): Vehicle
  aiPredictions(vehicleId: ID!): [PredictionResult]
  pendingClaims: [WarrantyClaim]
}

type Mutation {
  createClaim(input: ClaimInput!): WarrantyClaim
  updateClaimStatus(id: ID!, status: String!): WarrantyClaim
  assignPartToVehicle(
    vehicleId: ID!
    partId: ID!
    serialNumber: String!
  ): VehiclePart
}
```
