package com.ardms.controller;

import com.ardms.dto.request.CreateReleaseRequest;
import com.ardms.dto.request.UpdateReleaseRequest;
import com.ardms.dto.response.ApiResponse;
import com.ardms.dto.response.PagedResponse;
import com.ardms.dto.response.ReleaseResponse;
import com.ardms.entity.enums.ReleaseStatus;
import com.ardms.service.ReleaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
@RequestMapping("/releases")
@Tag(name = "Releases", description = "APIs for managing software releases")
public class ReleaseController {

    private static final Logger logger = LogManager.getLogger(ReleaseController.class);

    @Autowired
    private ReleaseService releaseService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RELEASE_MANAGER', 'DEVELOPER')")
    @Operation(summary = "Create Release", description = "Create a new software release")
    public ResponseEntity<ApiResponse<ReleaseResponse>> createRelease(
            @Valid @RequestBody CreateReleaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReleaseResponse response = releaseService.createRelease(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Release created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get All Releases", description = "Retrieve all releases with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<ReleaseResponse>>> getAllReleases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<ReleaseResponse> response = releaseService.getAllReleases(pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Release by ID")
    public ResponseEntity<ApiResponse<ReleaseResponse>> getReleaseById(
            @PathVariable @Parameter(description = "Release ID") Long id) {
        return ResponseEntity.ok(ApiResponse.success(releaseService.getReleaseById(id)));
    }

    @GetMapping("/version/{version}")
    @Operation(summary = "Get Release by Version")
    public ResponseEntity<ApiResponse<ReleaseResponse>> getReleaseByVersion(
            @PathVariable String version) {
        return ResponseEntity.ok(ApiResponse.success(releaseService.getReleaseByVersion(version)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get Releases by Status")
    public ResponseEntity<ApiResponse<PagedResponse<ReleaseResponse>>> getReleasesByStatus(
            @PathVariable ReleaseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(releaseService.getReleasesByStatus(status, pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search Releases")
    public ResponseEntity<ApiResponse<PagedResponse<ReleaseResponse>>> searchReleases(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(ApiResponse.success(releaseService.searchReleases(query, pageable)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELEASE_MANAGER')")
    @Operation(summary = "Update Release")
    public ResponseEntity<ApiResponse<ReleaseResponse>> updateRelease(
            @PathVariable Long id,
            @Valid @RequestBody UpdateReleaseRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReleaseResponse response = releaseService.updateRelease(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "Release updated successfully"));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELEASE_MANAGER')")
    @Operation(summary = "Update Release Status")
    public ResponseEntity<ApiResponse<ReleaseResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        ReleaseStatus status = ReleaseStatus.valueOf(body.get("status").toUpperCase());
        ReleaseResponse response = releaseService.updateReleaseStatus(id, status, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response, "Release status updated"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RELEASE_MANAGER')")
    @Operation(summary = "Delete Release")
    public ResponseEntity<ApiResponse<Void>> deleteRelease(@PathVariable Long id) {
        releaseService.deleteRelease(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Release deleted successfully"));
    }
}
