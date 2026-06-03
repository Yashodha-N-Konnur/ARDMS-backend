package com.ardms.service;

import com.ardms.dto.request.CreateDeploymentRequest;
import com.ardms.dto.response.DeploymentResponse;
import com.ardms.dto.response.PagedResponse;
import com.ardms.entity.enums.DeploymentStatus;
import org.springframework.data.domain.Pageable;

public interface DeploymentService {
    DeploymentResponse createDeployment(CreateDeploymentRequest request, String createdBy);
    DeploymentResponse updateDeploymentStatus(Long id, DeploymentStatus status, String errorMessage, String updatedBy);
    DeploymentResponse getDeploymentById(Long id);
    PagedResponse<DeploymentResponse> getAllDeployments(Pageable pageable);
    PagedResponse<DeploymentResponse> getDeploymentsByRelease(Long releaseId, Pageable pageable);
    PagedResponse<DeploymentResponse> getDeploymentsByEnvironment(Long environmentId, Pageable pageable);
    PagedResponse<DeploymentResponse> getDeploymentsByStatus(DeploymentStatus status, Pageable pageable);
}
