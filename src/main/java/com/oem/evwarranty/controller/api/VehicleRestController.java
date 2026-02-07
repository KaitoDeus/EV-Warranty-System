package com.oem.evwarranty.controller.api;

import com.oem.evwarranty.model.dto.VehicleDTO;
import com.oem.evwarranty.service.VehicleService;
import com.oem.evwarranty.service.mapper.VehicleMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles")
@Tag(name = "Vehicle API", description = "Endpoints for vehicle management and search")
public class VehicleRestController {

    private final VehicleService vehicleService;
    private final VehicleMapper vehicleMapper;

    public VehicleRestController(VehicleService vehicleService, VehicleMapper vehicleMapper) {
        this.vehicleService = vehicleService;
        this.vehicleMapper = vehicleMapper;
    }

    @GetMapping
    @Operation(summary = "Get list of vehicles")
    public ResponseEntity<Page<VehicleDTO>> getVehicles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<VehicleDTO> vehicles = vehicleService.findAll(PageRequest.of(page, size))
                .map(vehicleMapper::toDTO);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle details by ID")
    public ResponseEntity<VehicleDTO> getVehicle(@PathVariable Long id) {
        return vehicleService.findById(id)
                .map(vehicleMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    @Operation(summary = "Search vehicle by VIN")
    public ResponseEntity<VehicleDTO> searchByVin(@RequestParam String vin) {
        return vehicleService.findByVin(vin)
                .map(vehicleMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/parts")
    @Operation(summary = "Get parts for a vehicle")
    public ResponseEntity<java.util.List<com.oem.evwarranty.model.dto.VehiclePartDTO>> getVehicleParts(
            @PathVariable Long id) {
        if (id == null)
            return ResponseEntity.badRequest().build();
        java.util.List<com.oem.evwarranty.model.VehiclePart> parts = vehicleService.findPartsByVehicleId(id);

        java.util.List<com.oem.evwarranty.model.dto.VehiclePartDTO> dtos = parts.stream().map(part -> {
            com.oem.evwarranty.model.dto.VehiclePartDTO dto = new com.oem.evwarranty.model.dto.VehiclePartDTO();
            dto.setId(part.getId());
            dto.setSerialNumber(part.getSerialNumber());
            dto.setWarrantyEndDate(part.getWarrantyEndDate());
            dto.setStatus(part.getStatus().name());
            if (part.getPart() != null) {
                dto.setPartName(part.getPart().getName());
                dto.setPartCode(part.getPart().getPartNumber());
            }
            return dto;
        }).collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}
