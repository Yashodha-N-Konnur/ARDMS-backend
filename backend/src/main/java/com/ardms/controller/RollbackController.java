package com.ardms.controller;

import com.ardms.dto.request.InitiateRollbackRequest;
import com.ardms.dto.response.ApiResponse;
import com.ardms.dto.response.PagedResponse;
import com.ardms.dto.response.RollbackResponse;
import com.ardms.service.RollbackService;
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
@RequestMapping("/rollbacks")
@Tag(name = "Rollbacks", description = "APIs for managing rollbacks")
public class RollbackController {

    @Autowired
    private RollbackService rollbackService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RELEASE_MANAGER')")
    @Operation(summary = "Initiate Rollback")
    public ResponseEntity<ApiResponse<RollbackResponse>> initiateRollback(
            @Valid @RequestBody InitiateRollbackRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        RollbackResponse response = rollbackService.initiateRollback(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Rollback initiated successfully"));
    }

    @GetMapping
    @Operation(summary = "Get All Rollbacks")
    public ResponseEntity<ApiResponse<PagedResponse<RollbackResponse>>> getAllRollbacks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(rollbackService.getAllRollbacks(pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Rollback by ID")
    public ResponseEntity<ApiResponse<RollbackResponse>> getRollbackById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(rollbackService.getRollbackById(id)));
    }

    @GetMapping("/deployment/{deploymentId}")
    @Operation(summary = "Get Rollbacks by Deployment")
    public ResponseEntity<ApiResponse<PagedResponse<RollbackResponse>>> getRollbacksByDeployment(
            @PathVariable Long deploymentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(
            rollbackService.getRollbacksByDeployment(deploymentId, pageable)));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELEASE_MANAGER')")
    @Operation(summary = "Complete Rollback", description = "Mark a rollback as complete (success or failure)")
    public ResponseEntity<ApiResponse<RollbackResponse>> completeRollback(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        boolean success = Boolean.parseBoolean(body.getOrDefault("success", "false").toString());
        String errorMessage = (String) body.get("errorMessage");
        RollbackResponse response = rollbackService.completeRollback(id, success, errorMessage);
        return ResponseEntity.ok(ApiResponse.success(response, "Rollback status updated"));
    }
}
