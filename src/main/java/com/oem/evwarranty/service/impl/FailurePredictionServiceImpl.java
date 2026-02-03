package com.oem.evwarranty.service.impl;

import com.oem.evwarranty.model.Vehicle;
import com.oem.evwarranty.model.WarrantyClaim;
import com.oem.evwarranty.model.dto.PredictionResult;
import com.oem.evwarranty.repository.VehicleRepository;
import com.oem.evwarranty.repository.WarrantyClaimRepository;
import com.oem.evwarranty.service.FailurePredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FailurePredictionServiceImpl implements FailurePredictionService {

    private final VehicleRepository vehicleRepository;
    private final WarrantyClaimRepository warrantyClaimRepository;

    @Override
    public List<PredictionResult> predictFailures(Long vehicleId) {
        Vehicle targetVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found: " + vehicleId));

        // 1. Find similar vehicles (same model)
        List<Vehicle> similarVehicles = vehicleRepository.findByModel(targetVehicle.getModel());

        if (similarVehicles.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Aggregate warranty claims for these vehicles
        List<Long> vehicleIds = similarVehicles.stream().map(Vehicle::getId).collect(Collectors.toList());
        List<WarrantyClaim> historicalClaims = new ArrayList<>();
        for (Long id : vehicleIds) {
            historicalClaims.addAll(warrantyClaimRepository.findByVehicleId(id));
        }

        // 3. Statistical Analysis: Count failures and calculate average mileage at
        // failure
        Map<String, List<WarrantyClaim>> claimsByPart = historicalClaims.stream()
                .filter(wc -> wc.getVehiclePart() != null && wc.getVehiclePart().getPart() != null)
                .collect(Collectors.groupingBy(wc -> wc.getVehiclePart().getPart().getName()));

        double fleetSize = similarVehicles.size();
        List<PredictionResult> predictions = new ArrayList<>();

        claimsByPart.forEach((partName, claims) -> {
            long count = claims.size();
            double avgMileageAtFailure = claims.stream()
                    .filter(c -> c.getMileageAtClaim() != null)
                    .mapToInt(WarrantyClaim::getMileageAtClaim)
                    .average()
                    .orElse(0.0);

            double baseProbability = (double) count / fleetSize;

            // Advanced Logic: Anomaly Detection
            // If the current vehicle has significantly lower mileage than avg failure
            // mileage
            // but the fleet failure rate is rising, it's a predictive risk.
            // If the vehicle ALREADY had a failure at much lower mileage than fleet avg,
            // it's an anomaly.

            double riskMultiplier = 1.0;
            String anomalyNote = "";

            if (targetVehicle.getMileage() != null) {
                // Predictive: Vehicle approaching average failure mileage
                if (targetVehicle.getMileage() > avgMileageAtFailure * 0.8
                        && targetVehicle.getMileage() < avgMileageAtFailure) {
                    riskMultiplier *= 1.4;
                    anomalyNote = " (Sắp đến ngưỡng hỏng hóc định kỳ)";
                }

                // Anomaly: Current vehicle's specific history (if it already had issues)
                long specificVehicleFailures = targetVehicle.getWarrantyClaims().stream()
                        .filter(c -> c.getVehiclePart() != null
                                && c.getVehiclePart().getPart().getName().equals(partName))
                        .count();

                if (specificVehicleFailures > 0) {
                    riskMultiplier *= 2.0; // High risk of repeat failure
                    anomalyNote = " (Phát hiện lỗi lặp lại)";
                }
            }

            double finalProbability = Math.min(0.99, baseProbability * riskMultiplier);

            // Lowered threshold to 5% to be more sensitive
            if (finalProbability > 0.05) {
                predictions.add(PredictionResult.builder()
                        .partName(partName + anomalyNote)
                        .failureProbability(finalProbability)
                        .riskLevel(determineRiskLevel(finalProbability))
                        .recommendedAction(generateRecommendation(partName, finalProbability))
                        .build());
            }
        });

        // 4. Sort by probability descending
        predictions.sort(Comparator.comparing(PredictionResult::getFailureProbability).reversed());

        return predictions;
    }

    private PredictionResult.RiskLevel determineRiskLevel(double probability) {
        if (probability > 0.6)
            return PredictionResult.RiskLevel.CRITICAL;
        if (probability > 0.35)
            return PredictionResult.RiskLevel.HIGH;
        if (probability > 0.15)
            return PredictionResult.RiskLevel.MEDIUM;
        return PredictionResult.RiskLevel.LOW;
    }

    private String generateRecommendation(String partName, double probability) {
        if (probability > 0.6) {
            return "NGUY CẤP: " + partName
                    + " có dấu hiệu hỏng hóc bất thường. Yêu cầu kiểm tra chẩn đoán ngay lập tức.";
        } else if (probability > 0.35) {
            return "CẢNH BÁO: Xác suất hỏng " + partName
                    + " cao dựa trên lịch sử dòng xe. Cần kiểm tra trong lần bảo dưỡng tới.";
        } else if (probability > 0.15) {
            return "KHUYẾN NGHỊ: Theo dõi hiệu suất của " + partName
                    + ". Dữ liệu cho thấy có dấu hiệu hao mòn trung bình.";
        } else {
            return "Khuyến nghị giám sát định kỳ cho " + partName + ".";
        }
    }
}
