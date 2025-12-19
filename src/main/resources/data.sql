-- =====================================================
-- Sample Data for EV Warranty Management System
-- H2 Compatible syntax
-- =====================================================

-- Roles
INSERT INTO roles (name, description, created_at) VALUES 
('SC_STAFF', 'Service Center Staff - Can manage vehicles, customers, and create claims', NOW()),
('SC_TECHNICIAN', 'Service Center Technician - Can work on assigned claims', NOW()),
('EVM_STAFF', 'EV Manufacturer Staff - Can review claims and manage parts', NOW()),
('ADMIN', 'System Administrator - Full access to all features', NOW());

-- Users (password: password123 - BCrypt encoded)
INSERT INTO users (username, password, full_name, email, phone, service_center, active, created_at, updated_at) VALUES
('admin', '$2a$10$N.yJHbqVczz5oI8Mv8OqZeSO3q1z.EO4qFUyCvBTJQLr8Kfj1Kvuy', 'System Administrator', 'admin@evwarranty.com', '+84912345001', NULL, true, NOW(), NOW()),
('scstaff', '$2a$10$N.yJHbqVczz5oI8Mv8OqZeSO3q1z.EO4qFUyCvBTJQLr8Kfj1Kvuy', 'John Smith', 'john.smith@servicecenter.com', '+84912345002', 'SC-HANOI-01', true, NOW(), NOW()),
('sctech', '$2a$10$N.yJHbqVczz5oI8Mv8OqZeSO3q1z.EO4qFUyCvBTJQLr8Kfj1Kvuy', 'Mike Johnson', 'mike.johnson@servicecenter.com', '+84912345003', 'SC-HANOI-01', true, NOW(), NOW()),
('evmstaff', '$2a$10$N.yJHbqVczz5oI8Mv8OqZeSO3q1z.EO4qFUyCvBTJQLr8Kfj1Kvuy', 'Sarah Lee', 'sarah.lee@evmanufacturer.com', '+84912345004', NULL, true, NOW(), NOW());

-- User Roles (using subqueries for H2)
INSERT INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'admin' AND r.name = 'ADMIN';
INSERT INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'scstaff' AND r.name = 'SC_STAFF';
INSERT INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'sctech' AND r.name = 'SC_TECHNICIAN';
INSERT INTO user_roles (user_id, role_id) SELECT u.id, r.id FROM users u, roles r WHERE u.username = 'evmstaff' AND r.name = 'EVM_STAFF';

-- Customers
INSERT INTO customers (full_name, email, phone, address, city, state, zip_code, country, created_at, updated_at) VALUES
('David Chen', 'david.chen@email.com', '+84987654321', '123 Le Loi Street', 'Ho Chi Minh City', 'HCMC', '70000', 'Vietnam', NOW(), NOW()),
('Emily Wang', 'emily.wang@email.com', '+84987654322', '456 Nguyen Hue Blvd', 'Hanoi', 'HN', '10000', 'Vietnam', NOW(), NOW()),
('Robert Kim', 'robert.kim@email.com', '+84987654323', '789 Tran Hung Dao St', 'Da Nang', 'DN', '50000', 'Vietnam', NOW(), NOW());

-- Vehicles
INSERT INTO vehicles (vin, model, make, year, color, battery_type, battery_capacity, motor_type, mileage, manufacture_date, warranty_start_date, warranty_end_date, status, customer_id, created_at, updated_at) VALUES
('5YJ3E1EA1NF123456', 'Model 3', 'Tesla', 2023, 'Pearl White', 'Lithium-ion NCA', 82.0, 'Dual Motor AWD', 15000, '2022-06-01', '2022-07-15', '2026-07-15', 'ACTIVE', 1, NOW(), NOW()),
('WVWZZZ3CZWE123456', 'ID.4', 'Volkswagen', 2023, 'Moonstone Grey', 'Lithium-ion NMC', 77.0, 'Single Motor RWD', 8500, '2022-09-01', '2022-10-01', '2026-10-01', 'ACTIVE', 2, NOW(), NOW()),
('7SAYGDEF5PA123456', 'Model Y', 'Tesla', 2024, 'Midnight Silver', 'LFP', 60.0, 'Single Motor RWD', 3200, '2023-03-01', '2023-04-01', '2027-04-01', 'ACTIVE', 3, NOW(), NOW());

-- Parts
INSERT INTO parts (part_number, name, description, category, price, warranty_months, manufacturer, model_compatibility, is_active, min_stock_level, created_at, updated_at) VALUES
('BAT-LI-82K', 'Battery Pack 82kWh', 'High-capacity lithium-ion battery pack', 'BATTERY', 15000.00, 96, 'Tesla', 'Model 3, Model Y', true, 2, NOW(), NOW()),
('MTR-DM-AWD', 'Dual Motor AWD Unit', 'Dual motor all-wheel drive assembly', 'MOTOR', 8500.00, 48, 'Tesla', 'Model 3, Model Y, Model S', true, 3, NOW(), NOW()),
('CHG-ONB-11K', 'Onboard Charger 11kW', 'AC onboard charger module', 'CHARGER', 1200.00, 36, 'Generic EV Parts', 'Universal', true, 5, NOW(), NOW()),
('BRK-FRT-SET', 'Front Brake Set', 'Front brake pads and rotors', 'BRAKES', 450.00, 24, 'Brembo', 'Universal', true, 10, NOW(), NOW()),
('DSP-TCH-15', 'Touchscreen Display 15"', 'Central infotainment display', 'ELECTRONICS', 2200.00, 36, 'LG', 'Model 3, Model Y', true, 3, NOW(), NOW());

-- Warranty Policies
INSERT INTO warranty_policies (name, description, duration_months, mileage_limit, coverage_type, applicable_models, is_active, created_at, updated_at) VALUES
('Standard EV Warranty', 'Basic warranty coverage for all EV components', 48, 50000, 'BUMPER_TO_BUMPER', 'All Models', true, NOW(), NOW()),
('Battery Extended Warranty', 'Extended warranty for battery pack', 96, 150000, 'BATTERY', 'All EV Models', true, NOW(), NOW()),
('Powertrain Warranty', 'Motor and drivetrain coverage', 72, 100000, 'POWERTRAIN', 'All Models', true, NOW(), NOW());

-- Service Campaigns
INSERT INTO service_campaigns (campaign_number, title, description, campaign_type, status, severity_level, affected_models, remedy_description, estimated_repair_time, total_affected, completed_count, start_date, created_at, updated_at) VALUES
('SC2024001', 'Battery Cooling System Update', 'Software update to improve battery thermal management', 'SERVICE_BULLETIN', 'ACTIVE', 'MEDIUM', 'Model 3 2022-2023', 'Update battery management software to version 2.5.1', 0.5, 1500, 342, '2024-01-15', NOW(), NOW()),
('SC2024002', 'Touchscreen Replacement', 'Recall for touchscreen units with display issues', 'RECALL', 'ACTIVE', 'HIGH', 'Model 3, Model Y 2021-2022', 'Replace faulty touchscreen display unit', 2.0, 850, 127, '2024-02-01', NOW(), NOW());
