package com.ardms.controller;

import com.ardms.dto.response.ApiResponse;
import com.ardms.dto.response.DashboardStatsResponse;
import com.ardms.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@Tag(name = "Dashboard", description = "APIs for dashboard statistics")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get Dashboard Statistics",
               description = "Returns aggregated statistics for releases, deployments, rollbacks, and environments")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(dashboardService.getDashboardStats()));
    }
}
