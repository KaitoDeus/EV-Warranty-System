# System Architecture

## Overview
The EV Warranty Management System follows a layered MVC architecture with Spring Boot.

```mermaid
graph TB
    subgraph "Presentation Layer"
        A[Thymeleaf Templates]
        B[CSS/JavaScript]
    end
    
    subgraph "Controller Layer"
        C[AuthController]
        D[DashboardController]
        E[VehicleController]
        F[WarrantyClaimController]
        G[PartController]
        H[CampaignController]
        I[AdminController]
    end
    
    subgraph "Service Layer"
        J[VehicleService]
        K[CustomerService]
        L[WarrantyClaimService]
        M[PartService]
        N[ServiceCampaignService]
        O[InventoryService]
        P[ReportService]
    end
    
    subgraph "Repository Layer"
        Q[JPA Repositories]
    end
    
    subgraph "Database"
        R[(MySQL/H2)]
    end
    
    A --> C
    A --> D
    A --> E
    C --> J
    D --> P
    E --> J
    E --> K
    F --> L
    G --> M
    H --> N
    I --> O
    J --> Q
    K --> Q
    L --> Q
    M --> Q
    N --> Q
    O --> Q
    P --> Q
    Q --> R
```

## Packages

| Package | Purpose |
|---------|---------|
| `config` | Spring Security, Web MVC configuration |
| `controller` | Thymeleaf MVC controllers |
| `model` | JPA entity classes |
| `repository` | Spring Data JPA repositories |
| `service` | Business logic layer |

## Security

Spring Security with form-based authentication:
- URL-based access control by role
- BCrypt password encoding
- Session management

### Role-Based Access

| Role | Access | Description |
|------|--------|-------------|
| SC_STAFF | /sc/**, /dashboard | Full service center ops |
| SC_TECHNICIAN | /sc/**, /dashboard | Limited to assigned work |
| EVM_STAFF | /evm/**, /dashboard | Manufacturer operations |
| ADMIN | /** | Full system access |
