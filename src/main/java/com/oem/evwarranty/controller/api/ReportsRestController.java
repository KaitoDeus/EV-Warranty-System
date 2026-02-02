package com.oem.evwarranty.controller.api;

import com.oem.evwarranty.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports API", description = "Endpoints for dashboard statistics and analytics")
public class ReportsRestController {

    private final ReportService reportService;

    public ReportsRestController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get overall dashboard statistics")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(reportService.getDashboardStats());
    }

    @GetMapping("/claims/status")
    @Operation(summary = "Get claim count breakdown by status")
    public ResponseEntity<Map<String, Long>> getClaimStats() {
        return ResponseEntity.ok(reportService.getClaimStatsByStatus());
    }
}
