package com.ardms.service;

import com.ardms.dto.request.InitiateRollbackRequest;
import com.ardms.dto.response.PagedResponse;
import com.ardms.dto.response.RollbackResponse;
import org.springframework.data.domain.Pageable;

public interface RollbackService {
    RollbackResponse initiateRollback(InitiateRollbackRequest request, String initiatedBy);
    RollbackResponse getRollbackById(Long id);
    PagedResponse<RollbackResponse> getRollbacksByDeployment(Long deploymentId, Pageable pageable);
    PagedResponse<RollbackResponse> getAllRollbacks(Pageable pageable);
    RollbackResponse completeRollback(Long id, boolean success, String errorMessage);
}
