package com.ardms.service.impl;

import com.ardms.dto.request.InitiateRollbackRequest;
import com.ardms.dto.response.PagedResponse;
import com.ardms.dto.response.RollbackResponse;
import com.ardms.entity.Deployment;
import com.ardms.entity.RollbackHistory;
import com.ardms.entity.User;
import com.ardms.entity.enums.DeploymentStatus;
import com.ardms.entity.enums.ReleaseStatus;
import com.ardms.entity.enums.RollbackStatus;
import com.ardms.exception.InvalidOperationException;
import com.ardms.exception.ResourceNotFoundException;
import com.ardms.mapper.RollbackMapper;
import com.ardms.repository.DeploymentRepository;
import com.ardms.repository.ReleaseRepository;
import com.ardms.repository.RollbackHistoryRepository;
import com.ardms.repository.UserRepository;
import com.ardms.service.RollbackService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class RollbackServiceImpl implements RollbackService {

    private static final Logger deploymentLogger = LogManager.getLogger("com.ardms.deployment");

    @Autowired private RollbackHistoryRepository rollbackHistoryRepository;
    @Autowired private DeploymentRepository deploymentRepository;
    @Autowired private ReleaseRepository releaseRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private RollbackMapper rollbackMapper;

    @Override
    public RollbackResponse initiateRollback(InitiateRollbackRequest request, String initiatedBy) {
        Deployment deployment = deploymentRepository.findById(request.getDeploymentId())
            .orElseThrow(() -> new ResourceNotFoundException("Deployment", "id", request.getDeploymentId()));

        if (deployment.getStatus() != DeploymentStatus.SUCCESS &&
            deployment.getStatus() != DeploymentStatus.FAILED) {
            throw new InvalidOperationException(
                "Can only rollback deployments with status SUCCESS or FAILED. Current: " + deployment.getStatus());
        }

        User user = userRepository.findByUsername(initiatedBy)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", initiatedBy));

        Deployment rollbackToDeployment = null;
        if (request.getRollbackToDeploymentId() != null) {
            rollbackToDeployment = deploymentRepository.findById(request.getRollbackToDeploymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Deployment", "id", request.getRollbackToDeploymentId()));

            if (rollbackToDeployment.getStatus() != DeploymentStatus.SUCCESS) {
                throw new InvalidOperationException("Can only rollback to a successful deployment");
            }
        }

        RollbackHistory rollback = RollbackHistory.builder()
            .deployment(deployment)
            .rollbackToDeployment(rollbackToDeployment)
            .initiatedBy(user)
            .reason(request.getReason())
            .status(RollbackStatus.INITIATED)
            .notes(request.getNotes())
            .startedAt(LocalDateTime.now())
            .createdBy(initiatedBy)
            .build();

        rollback = rollbackHistoryRepository.save(rollback);

        // Mark current deployment as rolled back
        deployment.setStatus(DeploymentStatus.ROLLED_BACK);
        deploymentRepository.save(deployment);

        // Mark release as rolled back
        deployment.getRelease().setStatus(ReleaseStatus.ROLLED_BACK);
        releaseRepository.save(deployment.getRelease());

        deploymentLogger.info("Rollback initiated: ID={} DeploymentID={} by {} Reason: {}",
            rollback.getId(), deployment.getId(), initiatedBy, request.getReason());

        return rollbackMapper.toResponse(rollback);
    }

    @Override
    @Transactional(readOnly = true)
    public RollbackResponse getRollbackById(Long id) {
        RollbackHistory rollback = rollbackHistoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rollback", "id", id));
        return rollbackMapper.toResponse(rollback);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RollbackResponse> getRollbacksByDeployment(Long deploymentId, Pageable pageable) {
        Page<RollbackResponse> page = rollbackHistoryRepository.findByDeploymentId(deploymentId, pageable)
            .map(rollbackMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RollbackResponse> getAllRollbacks(Pageable pageable) {
        Page<RollbackResponse> page = rollbackHistoryRepository.findAll(pageable)
            .map(rollbackMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    public RollbackResponse completeRollback(Long id, boolean success, String errorMessage) {
        RollbackHistory rollback = rollbackHistoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Rollback", "id", id));

        LocalDateTime completedAt = LocalDateTime.now();
        rollback.setCompletedAt(completedAt);
        rollback.setStatus(success ? RollbackStatus.SUCCESS : RollbackStatus.FAILED);

        if (rollback.getStartedAt() != null) {
            rollback.setDurationSeconds(
                java.time.Duration.between(rollback.getStartedAt(), completedAt).getSeconds()
            );
        }

        if (errorMessage != null) {
            rollback.setErrorMessage(errorMessage);
        }

        rollback = rollbackHistoryRepository.save(rollback);
        deploymentLogger.info("Rollback completed: ID={} Status={}", id, rollback.getStatus());

        return rollbackMapper.toResponse(rollback);
    }
}
