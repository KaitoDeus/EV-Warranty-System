package com.oem.evwarranty.config;

import com.oem.evwarranty.model.*;
import com.oem.evwarranty.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Data initializer to seed the database with sample data.
 * This ensures data is loaded correctly regardless of SQL dialect.
 */
@Configuration
public class DataInitializer {

        @Bean
        CommandLineRunner initDatabase(
                        RoleRepository roleRepository,
                        UserRepository userRepository,
                        CustomerRepository customerRepository,
                        VehicleRepository vehicleRepository,
                        PartRepository partRepository,
                        WarrantyPolicyRepository policyRepository,
                        ServiceCampaignRepository campaignRepository,
                        PasswordEncoder passwordEncoder) {

                return args -> {
                        // Create roles if they don't exist
                        Role scStaff = roleRepository.findByName("SC_STAFF")
                                        .orElseGet(() -> roleRepository.save(
                                                        Role.builder().name("SC_STAFF")
                                                                        .description("Service Center Staff").build()));
                        Role scTech = roleRepository.findByName("SC_TECHNICIAN")
                                        .orElseGet(() -> roleRepository.save(
                                                        Role.builder().name("SC_TECHNICIAN")
                                                                        .description("Service Center Technician")
                                                                        .build()));
                        Role evmStaff = roleRepository.findByName("EVM_STAFF")
                                        .orElseGet(() -> roleRepository.save(
                                                        Role.builder().name("EVM_STAFF")
                                                                        .description("EV Manufacturer Staff").build()));
                        Role admin = roleRepository.findByName("ADMIN")
                                        .orElseGet(() -> roleRepository.save(
                                                        Role.builder().name("ADMIN").description("System Administrator")
                                                                        .build()));

                        // Create default users if they don't exist
                        String encodedPassword = passwordEncoder.encode("password123");

                        // Admin
                        User adminUser = userRepository.findByUsername("admin").orElse(User.builder()
                                        .username("admin")
                                        .password(encodedPassword)
                                        .fullName("System Administrator")
                                        .email("admin@evwarranty.com")
                                        .phone("+84912345001")
                                        .active(true)
                                        .roles(new HashSet<>())
                                        .build());
                        adminUser.setRoles(new HashSet<>(Set.of(admin)));
                        userRepository.save(adminUser);

                        // SC Staff
                        User scStaffUser = userRepository.findByUsername("scstaff").orElse(User.builder()
                                        .username("scstaff")
                                        .password(encodedPassword)
                                        .fullName("John Smith")
                                        .email("john.smith@servicecenter.com")
                                        .phone("+84912345002")
                                        .serviceCenter("SC-HANOI-01")
                                        .active(true)
                                        .roles(new HashSet<>())
                                        .build());
                        scStaffUser.setRoles(new HashSet<>(Set.of(scStaff)));
                        userRepository.save(scStaffUser);

                        // SC Tech
                        User scTechUser = userRepository.findByUsername("sctech").orElse(User.builder()
                                        .username("sctech")
                                        .password(encodedPassword)
                                        .fullName("Mike Johnson")
                                        .email("mike.johnson@servicecenter.com")
                                        .phone("+84912345003")
                                        .serviceCenter("SC-HANOI-01")
                                        .active(true)
                                        .roles(new HashSet<>())
                                        .build());
                        scTechUser.setRoles(new HashSet<>(Set.of(scTech)));
                        userRepository.save(scTechUser);

                        // EVM Staff
                        User evmStaffUser = userRepository.findByUsername("evmstaff").orElse(User.builder()
                                        .username("evmstaff")
                                        .password(encodedPassword)
                                        .fullName("Sarah Lee")
                                        .email("sarah.lee@evmanufacturer.com")
                                        .phone("+84912345004")
                                        .active(true)
                                        .roles(new HashSet<>())
                                        .build());
                        evmStaffUser.setRoles(new HashSet<>(Set.of(evmStaff)));
                        userRepository.save(evmStaffUser);

                        // Create sample customers
                        if (customerRepository.count() == 0) {
                                Customer c1 = customerRepository.save(Customer.builder()
                                                .fullName("David Chen")
                                                .email("david.chen@email.com")
                                                .phone("+84987654321")
                                                .address("123 Le Loi Street")
                                                .city("Ho Chi Minh City")
                                                .state("HCMC")
                                                .zipCode("70000")
                                                .country("Vietnam")
                                                .build());

                                Customer c2 = customerRepository.save(Customer.builder()
                                                .fullName("Emily Wang")
                                                .email("emily.wang@email.com")
                                                .phone("+84987654322")
                                                .address("456 Nguyen Hue Blvd")
                                                .city("Hanoi")
                                                .state("HN")
                                                .zipCode("10000")
                                                .country("Vietnam")
                                                .build());

                                Customer c3 = customerRepository.save(Customer.builder()
                                                .fullName("Robert Kim")
                                                .email("robert.kim@email.com")
                                                .phone("+84987654323")
                                                .address("789 Tran Hung Dao St")
                                                .city("Da Nang")
                                                .state("DN")
                                                .zipCode("50000")
                                                .country("Vietnam")
                                                .build());

                                // Create sample vehicles
                                vehicleRepository.save(Vehicle.builder()
                                                .vin("5YJ3E1EA1NF123456")
                                                .model("Model 3")
                                                .make("Tesla")
                                                .year(2023)
                                                .color("Pearl White")
                                                .batteryType("Lithium-ion NCA")
                                                .batteryCapacity(82.0)
                                                .motorType("Dual Motor AWD")
                                                .mileage(15000)
                                                .manufactureDate(LocalDate.of(2022, 6, 1))
                                                .warrantyStartDate(LocalDate.of(2022, 7, 15))
                                                .warrantyEndDate(LocalDate.of(2026, 7, 15))
                                                .status(Vehicle.VehicleStatus.ACTIVE)
                                                .customer(c1)
                                                .build());

                                vehicleRepository.save(Vehicle.builder()
                                                .vin("WVWZZZ3CZWE123456")
                                                .model("ID.4")
                                                .make("Volkswagen")
                                                .year(2023)
                                                .color("Moonstone Grey")
                                                .batteryType("Lithium-ion NMC")
                                                .batteryCapacity(77.0)
                                                .motorType("Single Motor RWD")
                                                .mileage(8500)
                                                .manufactureDate(LocalDate.of(2022, 9, 1))
                                                .warrantyStartDate(LocalDate.of(2022, 10, 1))
                                                .warrantyEndDate(LocalDate.of(2026, 10, 1))
                                                .status(Vehicle.VehicleStatus.ACTIVE)
                                                .customer(c2)
                                                .build());

                                vehicleRepository.save(Vehicle.builder()
                                                .vin("7SAYGDEF5PA123456")
                                                .model("Model Y")
                                                .make("Tesla")
                                                .year(2024)
                                                .color("Midnight Silver")
                                                .batteryType("LFP")
                                                .batteryCapacity(60.0)
                                                .motorType("Single Motor RWD")
                                                .mileage(3200)
                                                .manufactureDate(LocalDate.of(2023, 3, 1))
                                                .warrantyStartDate(LocalDate.of(2023, 4, 1))
                                                .warrantyEndDate(LocalDate.of(2027, 4, 1))
                                                .status(Vehicle.VehicleStatus.ACTIVE)
                                                .customer(c3)
                                                .build());
                        }

                        // Create sample parts
                        if (partRepository.count() == 0) {
                                partRepository.save(Part.builder()
                                                .partNumber("BAT-LI-82K")
                                                .name("Battery Pack 82kWh")
                                                .description("High-capacity lithium-ion battery pack")
                                                .category(Part.PartCategory.BATTERY)
                                                .price(BigDecimal.valueOf(15000.00))
                                                .warrantyMonths(96)
                                                .manufacturer("Tesla")
                                                .isActive(true)
                                                .minStockLevel(2)
                                                .build());

                                partRepository.save(Part.builder()
                                                .partNumber("MTR-DM-AWD")
                                                .name("Dual Motor AWD Unit")
                                                .description("Dual motor all-wheel drive assembly")
                                                .category(Part.PartCategory.MOTOR)
                                                .price(BigDecimal.valueOf(8500.00))
                                                .warrantyMonths(48)
                                                .manufacturer("Tesla")
                                                .isActive(true)
                                                .minStockLevel(3)
                                                .build());

                                partRepository.save(Part.builder()
                                                .partNumber("CHG-ONB-11K")
                                                .name("Onboard Charger 11kW")
                                                .description("AC onboard charger module")
                                                .category(Part.PartCategory.CHARGER)
                                                .price(BigDecimal.valueOf(1200.00))
                                                .warrantyMonths(36)
                                                .manufacturer("Generic EV Parts")
                                                .isActive(true)
                                                .minStockLevel(5)
                                                .build());

                                partRepository.save(Part.builder()
                                                .partNumber("BRK-FRT-SET")
                                                .name("Front Brake Set")
                                                .description("Front brake pads and rotors")
                                                .category(Part.PartCategory.BRAKES)
                                                .price(BigDecimal.valueOf(450.00))
                                                .warrantyMonths(24)
                                                .manufacturer("Brembo")
                                                .isActive(true)
                                                .minStockLevel(10)
                                                .build());

                                partRepository.save(Part.builder()
                                                .partNumber("DSP-TCH-15")
                                                .name("Touchscreen Display 15\"")
                                                .description("Central infotainment display")
                                                .category(Part.PartCategory.ELECTRONICS)
                                                .price(BigDecimal.valueOf(2200.00))
                                                .warrantyMonths(36)
                                                .manufacturer("LG")
                                                .isActive(true)
                                                .minStockLevel(3)
                                                .build());
                        }

                        // Create warranty policies
                        if (policyRepository.count() == 0) {
                                policyRepository.save(WarrantyPolicy.builder()
                                                .name("Standard EV Warranty")
                                                .description("Basic warranty coverage for all EV components")
                                                .durationMonths(48)
                                                .mileageLimit(50000)
                                                .coverageType(WarrantyPolicy.CoverageType.BUMPER_TO_BUMPER)
                                                .applicableModels("All Models")
                                                .isActive(true)
                                                .build());

                                policyRepository.save(WarrantyPolicy.builder()
                                                .name("Battery Extended Warranty")
                                                .description("Extended warranty for battery pack")
                                                .durationMonths(96)
                                                .mileageLimit(150000)
                                                .coverageType(WarrantyPolicy.CoverageType.BATTERY)
                                                .applicableModels("All EV Models")
                                                .isActive(true)
                                                .build());

                                policyRepository.save(WarrantyPolicy.builder()
                                                .name("Powertrain Warranty")
                                                .description("Motor and drivetrain coverage")
                                                .durationMonths(72)
                                                .mileageLimit(100000)
                                                .coverageType(WarrantyPolicy.CoverageType.POWERTRAIN)
                                                .applicableModels("All Models")
                                                .isActive(true)
                                                .build());
                        }

                        // Create service campaigns
                        if (campaignRepository.count() == 0) {
                                campaignRepository.save(ServiceCampaign.builder()
                                                .campaignNumber("SC2024001")
                                                .title("Battery Cooling System Update")
                                                .description("Software update to improve battery thermal management")
                                                .campaignType(ServiceCampaign.CampaignType.SERVICE_BULLETIN)
                                                .status(ServiceCampaign.CampaignStatus.ACTIVE)
                                                .severityLevel(ServiceCampaign.SeverityLevel.MEDIUM)
                                                .affectedModels("Model 3 2022-2023")
                                                .remedyDescription(
                                                                "Update battery management software to version 2.5.1")
                                                .estimatedRepairTime(0.5)
                                                .totalAffected(1500)
                                                .completedCount(342)
                                                .startDate(LocalDate.of(2024, 1, 15))
                                                .build());

                                campaignRepository.save(ServiceCampaign.builder()
                                                .campaignNumber("SC2024002")
                                                .title("Touchscreen Replacement")
                                                .description("Recall for touchscreen units with display issues")
                                                .campaignType(ServiceCampaign.CampaignType.RECALL)
                                                .status(ServiceCampaign.CampaignStatus.ACTIVE)
                                                .severityLevel(ServiceCampaign.SeverityLevel.HIGH)
                                                .affectedModels("Model 3, Model Y 2021-2022")
                                                .remedyDescription("Replace faulty touchscreen display unit")
                                                .estimatedRepairTime(2.0)
                                                .totalAffected(850)
                                                .completedCount(127)
                                                .startDate(LocalDate.of(2024, 2, 1))
                                                .build());
                        }

                        System.out.println("========================================");
                        System.out.println("EV Warranty System - Data Initialized");
                        System.out.println("Users: " + userRepository.count());
                        System.out.println("Customers: " + customerRepository.count());
                        System.out.println("Vehicles: " + vehicleRepository.count());
                        System.out.println("Parts: " + partRepository.count());
                        System.out.println("========================================");
                };
        }
}
