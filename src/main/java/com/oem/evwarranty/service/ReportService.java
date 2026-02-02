package com.oem.evwarranty.service;

import com.oem.evwarranty.model.*;
import com.oem.evwarranty.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for Dashboard and Reports.
 */
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final VehicleRepository vehicleRepository;
    private final WarrantyClaimRepository claimRepository;
    private final ServiceCampaignRepository campaignRepository;
    private final CustomerRepository customerRepository;
    private final PartRepository partRepository;
    private final InventoryRepository inventoryRepository;

    public ReportService(VehicleRepository vehicleRepository,
            WarrantyClaimRepository claimRepository,
            ServiceCampaignRepository campaignRepository,
            CustomerRepository customerRepository,
            PartRepository partRepository,
            InventoryRepository inventoryRepository) {
        this.vehicleRepository = vehicleRepository;
        this.claimRepository = claimRepository;
        this.campaignRepository = campaignRepository;
        this.customerRepository = customerRepository;
        this.partRepository = partRepository;
        this.inventoryRepository = inventoryRepository;
    }

    /**
     * Get dashboard statistics
     */
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Vehicle stats
        stats.put("totalVehicles", vehicleRepository.count());
        stats.put("activeVehicles", vehicleRepository.countByStatus(Vehicle.VehicleStatus.ACTIVE));
        stats.put("vehiclesUnderWarranty", vehicleRepository.findVehiclesUnderWarranty().size());

        // Claim stats
        stats.put("totalClaims", claimRepository.count());
        stats.put("pendingClaims", claimRepository.countByStatus(WarrantyClaim.ClaimStatus.SUBMITTED) +
                claimRepository.countByStatus(WarrantyClaim.ClaimStatus.UNDER_REVIEW));
        stats.put("approvedClaims", claimRepository.countByStatus(WarrantyClaim.ClaimStatus.APPROVED));
        stats.put("completedClaims", claimRepository.countByStatus(WarrantyClaim.ClaimStatus.COMPLETED));

        // Campaign stats
        stats.put("totalCampaigns", campaignRepository.count());
        stats.put("activeCampaigns", campaignRepository.countByStatus(ServiceCampaign.CampaignStatus.ACTIVE));

        // Customer stats
        stats.put("totalCustomers", customerRepository.count());

        // Part stats
        stats.put("totalParts", partRepository.count());

        // Low stock alerts
        stats.put("lowStockItems", inventoryRepository.findLowStockItems().size());

        return stats;
    }

    /**
     * Get Service Center specific statistics
     */
    public Map<String, Object> getServiceCenterStats(String serviceCenter) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("pendingClaims", claimRepository.findByServiceCenter(serviceCenter).stream()
                .filter(c -> c.getStatus() == WarrantyClaim.ClaimStatus.SUBMITTED ||
                        c.getStatus() == WarrantyClaim.ClaimStatus.UNDER_REVIEW)
                .count());

        stats.put("inProgressClaims", claimRepository.findByServiceCenter(serviceCenter).stream()
                .filter(c -> c.getStatus() == WarrantyClaim.ClaimStatus.IN_PROGRESS)
                .count());

        stats.put("lowStockItems", inventoryRepository.findLowStockItemsByServiceCenter(serviceCenter).size());

        return stats;
    }

    /**
     * Get claim statistics by status
     */
    public Map<String, Long> getClaimStatsByStatus() {
        Map<String, Long> stats = new HashMap<>();
        for (WarrantyClaim.ClaimStatus status : WarrantyClaim.ClaimStatus.values()) {
            stats.put(status.name(), claimRepository.countByStatus(status));
        }
        return stats;
    }

    /**
     * Get campaign statistics by status
     */
    public Map<String, Long> getCampaignStatsByStatus() {
        Map<String, Long> stats = new HashMap<>();
        for (ServiceCampaign.CampaignStatus status : ServiceCampaign.CampaignStatus.values()) {
            stats.put(status.name(), campaignRepository.countByStatus(status));
        }
        return stats;
    }

    /**
     * Get vehicle statistics by status
     */
    public Map<String, Long> getVehicleStatsByStatus() {
        Map<String, Long> stats = new HashMap<>();
        for (Vehicle.VehicleStatus status : Vehicle.VehicleStatus.values()) {
            stats.put(status.name(), vehicleRepository.countByStatus(status));
        }
        return stats;
    }
}
