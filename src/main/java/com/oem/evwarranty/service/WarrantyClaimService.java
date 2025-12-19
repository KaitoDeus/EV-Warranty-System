package com.oem.evwarranty.service;

import com.oem.evwarranty.model.*;
import com.oem.evwarranty.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Service for Warranty Claim management operations.
 */
@Service
@Transactional
public class WarrantyClaimService {

    private final WarrantyClaimRepository claimRepository;
    private final VehicleRepository vehicleRepository;
    private final VehiclePartRepository vehiclePartRepository;
    private final UserRepository userRepository;

    public WarrantyClaimService(WarrantyClaimRepository claimRepository,
            VehicleRepository vehicleRepository,
            VehiclePartRepository vehiclePartRepository,
            UserRepository userRepository) {
        this.claimRepository = claimRepository;
        this.vehicleRepository = vehicleRepository;
        this.vehiclePartRepository = vehiclePartRepository;
        this.userRepository = userRepository;
    }

    public List<WarrantyClaim> findAll() {
        return claimRepository.findAll();
    }

    public Optional<WarrantyClaim> findById(Long id) {
        return claimRepository.findById(id);
    }

    public Optional<WarrantyClaim> findByClaimNumber(String claimNumber) {
        return claimRepository.findByClaimNumber(claimNumber);
    }

    public Page<WarrantyClaim> findAll(Pageable pageable) {
        return claimRepository.findAll(pageable);
    }

    public Page<WarrantyClaim> searchClaims(String search, Pageable pageable) {
        if (search == null || search.trim().isEmpty()) {
            return claimRepository.findAll(pageable);
        }
        return claimRepository.searchClaims(search, pageable);
    }

    public List<WarrantyClaim> findByStatus(WarrantyClaim.ClaimStatus status) {
        return claimRepository.findByStatus(status);
    }

    public List<WarrantyClaim> findByVehicleId(Long vehicleId) {
        return claimRepository.findByVehicleId(vehicleId);
    }

    public Page<WarrantyClaim> findByServiceCenter(String serviceCenter, Pageable pageable) {
        return claimRepository.findByServiceCenterPaged(serviceCenter, pageable);
    }

    public Page<WarrantyClaim> findPendingClaims(Pageable pageable) {
        return claimRepository.findByStatusIn(
                Arrays.asList(WarrantyClaim.ClaimStatus.SUBMITTED, WarrantyClaim.ClaimStatus.UNDER_REVIEW),
                pageable);
    }

    public WarrantyClaim createClaim(WarrantyClaim claim, Long vehicleId, Long vehiclePartId, Long submittedById) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        claim.setVehicle(vehicle);

        if (vehiclePartId != null) {
            VehiclePart vehiclePart = vehiclePartRepository.findById(vehiclePartId)
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle part not found"));
            claim.setVehiclePart(vehiclePart);
        }

        if (submittedById != null) {
            User submitter = userRepository.findById(submittedById)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            claim.setSubmittedBy(submitter);
        }

        claim.setStatus(WarrantyClaim.ClaimStatus.DRAFT);
        claim.setClaimNumber("WC" + System.currentTimeMillis());

        return claimRepository.save(claim);
    }

    public WarrantyClaim submitClaim(Long id) {
        return claimRepository.findById(id)
                .map(claim -> {
                    if (claim.getStatus() != WarrantyClaim.ClaimStatus.DRAFT) {
                        throw new IllegalStateException("Claim can only be submitted from DRAFT status");
                    }
                    claim.setStatus(WarrantyClaim.ClaimStatus.SUBMITTED);
                    claim.setSubmittedAt(LocalDateTime.now());
                    return claimRepository.save(claim);
                })
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
    }

    public WarrantyClaim approveClaim(Long id, Long reviewerId) {
        return claimRepository.findById(id)
                .map(claim -> {
                    User reviewer = userRepository.findById(reviewerId)
                            .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));

                    claim.setStatus(WarrantyClaim.ClaimStatus.APPROVED);
                    claim.setReviewedBy(reviewer);
                    claim.setReviewedAt(LocalDateTime.now());
                    return claimRepository.save(claim);
                })
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
    }

    public WarrantyClaim rejectClaim(Long id, Long reviewerId, String reason) {
        return claimRepository.findById(id)
                .map(claim -> {
                    User reviewer = userRepository.findById(reviewerId)
                            .orElseThrow(() -> new IllegalArgumentException("Reviewer not found"));

                    claim.setStatus(WarrantyClaim.ClaimStatus.REJECTED);
                    claim.setReviewedBy(reviewer);
                    claim.setReviewedAt(LocalDateTime.now());
                    claim.setRejectionReason(reason);
                    return claimRepository.save(claim);
                })
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
    }

    public WarrantyClaim completeClaim(Long id) {
        return claimRepository.findById(id)
                .map(claim -> {
                    claim.setStatus(WarrantyClaim.ClaimStatus.COMPLETED);
                    claim.setCompletedAt(LocalDateTime.now());

                    // Calculate total cost
                    BigDecimal laborCost = claim.getLaborCost() != null ? claim.getLaborCost() : BigDecimal.ZERO;
                    BigDecimal partsCost = claim.getPartsCost() != null ? claim.getPartsCost() : BigDecimal.ZERO;
                    claim.setTotalCost(laborCost.add(partsCost));

                    return claimRepository.save(claim);
                })
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
    }

    public WarrantyClaim updateClaim(Long id, WarrantyClaim updatedClaim) {
        return claimRepository.findById(id)
                .map(claim -> {
                    claim.setFailureDescription(updatedClaim.getFailureDescription());
                    claim.setDiagnosisNotes(updatedClaim.getDiagnosisNotes());
                    claim.setRepairDescription(updatedClaim.getRepairDescription());
                    claim.setLaborHours(updatedClaim.getLaborHours());
                    claim.setLaborCost(updatedClaim.getLaborCost());
                    claim.setPartsCost(updatedClaim.getPartsCost());
                    claim.setMileageAtClaim(updatedClaim.getMileageAtClaim());
                    return claimRepository.save(claim);
                })
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
    }

    public WarrantyClaim assignTechnician(Long claimId, Long technicianId) {
        return claimRepository.findById(claimId)
                .map(claim -> {
                    User technician = userRepository.findById(technicianId)
                            .orElseThrow(() -> new IllegalArgumentException("Technician not found"));
                    claim.setTechnician(technician);
                    claim.setStatus(WarrantyClaim.ClaimStatus.IN_PROGRESS);
                    return claimRepository.save(claim);
                })
                .orElseThrow(() -> new IllegalArgumentException("Claim not found"));
    }

    public void deleteClaim(Long id) {
        claimRepository.deleteById(id);
    }

    public long count() {
        return claimRepository.count();
    }

    public long countByStatus(WarrantyClaim.ClaimStatus status) {
        return claimRepository.countByStatus(status);
    }
}
