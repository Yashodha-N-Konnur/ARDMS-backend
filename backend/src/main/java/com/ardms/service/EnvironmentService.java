package com.ardms.service;

import com.ardms.dto.request.CreateEnvironmentRequest;
import com.ardms.dto.response.EnvironmentResponse;
import com.ardms.dto.response.PagedResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EnvironmentService {
    EnvironmentResponse createEnvironment(CreateEnvironmentRequest request, String createdBy);
    EnvironmentResponse getEnvironmentById(Long id);
    List<EnvironmentResponse> getAllActiveEnvironments();
    PagedResponse<EnvironmentResponse> getAllEnvironments(Pageable pageable);
    EnvironmentResponse toggleEnvironmentStatus(Long id, String updatedBy);
}
