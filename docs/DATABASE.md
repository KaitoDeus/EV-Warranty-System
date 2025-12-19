# Database Schema

## Entity Relationship

```mermaid
erDiagram
    USERS ||--o{ USER_ROLES : has
    ROLES ||--o{ USER_ROLES : has
    CUSTOMERS ||--o{ VEHICLES : owns
    VEHICLES ||--o{ VEHICLE_PARTS : has
    VEHICLES ||--o{ WARRANTY_CLAIMS : has
    VEHICLES ||--o{ SERVICE_HISTORY : has
    PARTS ||--o{ VEHICLE_PARTS : installed_as
    PARTS ||--o{ INVENTORY : tracked_in
    WARRANTY_CLAIMS ||--o{ CLAIM_ATTACHMENTS : has
    WARRANTY_CLAIMS }o--|| WARRANTY_POLICIES : governed_by
    WARRANTY_CLAIMS }o--|| USERS : submitted_by
    WARRANTY_CLAIMS }o--|| USERS : technician
    SERVICE_CAMPAIGNS }o--|| USERS : created_by
```

## Tables

### Core Tables

| Table | Description |
|-------|-------------|
| users | System users with roles |
| roles | Role definitions (SC_STAFF, SC_TECHNICIAN, EVM_STAFF, ADMIN) |
| customers | Vehicle owners |
| vehicles | EV vehicles with VIN and warranty info |
| parts | OEM parts catalog |
| vehicle_parts | Installed parts on vehicles |

### Warranty Tables

| Table | Description |
|-------|-------------|
| warranty_policies | Warranty rules and coverage |
| warranty_claims | Claim records with lifecycle |
| claim_attachments | Supporting documents |

### Operations Tables

| Table | Description |
|-------|-------------|
| service_campaigns | Recall/service campaigns |
| service_history | Vehicle service records |
| inventory | Parts stock at service centers |

## Key Constraints

- **VIN**: Unique 17-character alphanumeric
- **Part Number**: Unique part identifier
- **Serial Number**: Unique for installed parts
- **Claim Number**: Auto-generated (WC + timestamp)
- **Campaign Number**: Auto-generated (SC + timestamp)
