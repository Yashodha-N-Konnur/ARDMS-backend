package com.ardms.controller;

import com.ardms.dto.request.CreateDeploymentRequest;
import com.ardms.dto.response.ApiResponse;
import com.ardms.dto.response.DeploymentResponse;
import com.ardms.dto.response.PagedResponse;
import com.ardms.entity.enums.DeploymentStatus;
import com.ardms.service.DeploymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/deployments")
@Tag(name = "Deployments", description = "APIs for managing deployments")
public class DeploymentController {

    @Autowired
    private DeploymentService deploymentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RELEASE_MANAGER', 'DEVELOPER')")
    @Operation(summary = "Create Deployment", description = "Initiate a new deployment")
    public ResponseEntity<ApiResponse<DeploymentResponse>> createDeployment(
            @Valid @RequestBody CreateDeploymentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        DeploymentResponse response = deploymentService.createDeployment(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Deployment initiated successfully"));
    }

    @GetMapping
    @Operation(summary = "Get All Deployments")
    public ResponseEntity<ApiResponse<PagedResponse<DeploymentResponse>>> getAllDeployments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(deploymentService.getAllDeployments(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Deployment by ID")
    public ResponseEntity<ApiResponse<DeploymentResponse>> getDeploymentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(deploymentService.getDeploymentById(id)));
    }

    @GetMapping("/release/{releaseId}")
    @Operation(summary = "Get Deployments by Release")
    public ResponseEntity<ApiResponse<PagedResponse<DeploymentResponse>>> getDeploymentsByRelease(
            @PathVariable Long releaseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
            deploymentService.getDeploymentsByRelease(releaseId, pageable)));
    }

    @GetMapping("/environment/{environmentId}")
    @Operation(summary = "Get Deployments by Environment")
    public ResponseEntity<ApiResponse<PagedResponse<DeploymentResponse>>> getDeploymentsByEnvironment(
            @PathVariable Long environmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
            deploymentService.getDeploymentsByEnvironment(environmentId, pageable)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get Deployments by Status")
    public ResponseEntity<ApiResponse<PagedResponse<DeploymentResponse>>> getDeploymentsByStatus(
            @PathVariable DeploymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
            deploymentService.getDeploymentsByStatus(status, pageable)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELEASE_MANAGER')")
    @Operation(summary = "Update Deployment Status")
    public ResponseEntity<ApiResponse<DeploymentResponse>> updateDeploymentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        DeploymentStatus status = DeploymentStatus.valueOf(body.get("status").toUpperCase());
        String errorMessage = body.get("errorMessage");
        DeploymentResponse response = deploymentService.updateDeploymentStatus(
            id, status, errorMessage, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "Deployment status updated"));
    }
}
