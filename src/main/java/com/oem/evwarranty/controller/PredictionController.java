package com.oem.evwarranty.controller;

import com.oem.evwarranty.model.dto.PredictionResult;
import com.oem.evwarranty.service.FailurePredictionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for AI-powered failure predictions.
 */
@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class PredictionController {

    private final FailurePredictionService predictionService;

    /**
     * Get predicted part failures for a specific vehicle.
     * 
     * @param vehicleId ID of the vehicle
     * @return List of prediction results
     */
    @GetMapping("/predict/{vehicleId}")
    public ResponseEntity<List<PredictionResult>> predictFailures(@PathVariable Long vehicleId) {
        List<PredictionResult> results = predictionService.predictFailures(vehicleId);
        return ResponseEntity.ok(results);
    }
}
