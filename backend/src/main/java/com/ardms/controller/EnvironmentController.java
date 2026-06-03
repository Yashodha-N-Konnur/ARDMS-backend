package com.ardms.controller;

import com.ardms.dto.request.CreateEnvironmentRequest;
import com.ardms.dto.response.ApiResponse;
import com.ardms.dto.response.EnvironmentResponse;
import com.ardms.dto.response.PagedResponse;
import com.ardms.service.EnvironmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/environments")
@Tag(name = "Environments", description = "APIs for managing deployment environments")
public class EnvironmentController {

    @Autowired
    private EnvironmentService environmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RELEASE_MANAGER')")
    @Operation(summary = "Create Environment")
    public ResponseEntity<ApiResponse<EnvironmentResponse>> createEnvironment(
            @Valid @RequestBody CreateEnvironmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        EnvironmentResponse response = environmentService.createEnvironment(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Environment created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get All Environments")
    public ResponseEntity<ApiResponse<PagedResponse<EnvironmentResponse>>> getAllEnvironments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(environmentService.getAllEnvironments(pageable)));
    }

    @GetMapping("/active")
    @Operation(summary = "Get Active Environments")
    public ResponseEntity<ApiResponse<List<EnvironmentResponse>>> getActiveEnvironments() {
        return ResponseEntity.ok(ApiResponse.success(environmentService.getAllActiveEnvironments()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Environment by ID")
    public ResponseEntity<ApiResponse<EnvironmentResponse>> getEnvironmentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(environmentService.getEnvironmentById(id)));
    }

    @PatchMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Toggle Environment Active Status")
    public ResponseEntity<ApiResponse<EnvironmentResponse>> toggleStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        EnvironmentResponse response = environmentService.toggleEnvironmentStatus(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "Environment status toggled"));
    }
}
