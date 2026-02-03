package com.oem.evwarranty.service.impl;

import com.oem.evwarranty.model.Part;
import com.oem.evwarranty.model.Vehicle;
import com.oem.evwarranty.model.VehiclePart;
import com.oem.evwarranty.model.WarrantyClaim;
import com.oem.evwarranty.model.dto.PredictionResult;
import com.oem.evwarranty.repository.VehicleRepository;
import com.oem.evwarranty.repository.WarrantyClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FailurePredictionServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private WarrantyClaimRepository warrantyClaimRepository;

    @InjectMocks
    private FailurePredictionServiceImpl predictionService;

    private Vehicle targetVehicle;
    private Vehicle otherVehicle;
    private Part batteryPart;

    @BeforeEach
    void setUp() {
        targetVehicle = Vehicle.builder()
                .id(1L)
                .model("Model S")
                .mileage(60000)
                .build();

        otherVehicle = Vehicle.builder()
                .id(2L)
                .model("Model S")
                .build();

        batteryPart = Part.builder()
                .id(1L)
                .name("High Voltage Battery")
                .build();
    }

    @Test
    void testPredictFailures_Success() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(targetVehicle));
        when(vehicleRepository.findByModel("Model S")).thenReturn(Arrays.asList(targetVehicle, otherVehicle));

        VehiclePart vp = VehiclePart.builder().part(batteryPart).build();
        WarrantyClaim claim = WarrantyClaim.builder()
                .vehicle(otherVehicle)
                .vehiclePart(vp)
                .build();

        when(warrantyClaimRepository.findByVehicleId(1L)).thenReturn(List.of());
        when(warrantyClaimRepository.findByVehicleId(2L)).thenReturn(List.of(claim));

        // Act
        List<PredictionResult> results = predictionService.predictFailures(1L);

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals("High Voltage Battery", results.get(0).getPartName());
        // Since 1 failure in 2 vehicles, base prob is 0.5.
        // Mileage 60000 > 50000 and "battery" name -> factor 1.5 -> prob 0.75
        assertTrue(results.get(0).getFailureProbability() >= 0.75);
        assertEquals(PredictionResult.RiskLevel.CRITICAL, results.get(0).getRiskLevel());
    }

    @Test
    void testPredictFailures_NoHistory() {
        // Arrange
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(targetVehicle));
        when(vehicleRepository.findByModel("Model S")).thenReturn(Arrays.asList(targetVehicle));
        when(warrantyClaimRepository.findByVehicleId(1L)).thenReturn(List.of());

        // Act
        List<PredictionResult> results = predictionService.predictFailures(1L);

        // Assert
        assertTrue(results.isEmpty());
    }
}
