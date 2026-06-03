package com.ardms.service.impl;

import com.ardms.dto.request.CreateDeploymentRequest;
import com.ardms.dto.response.DeploymentResponse;
import com.ardms.dto.response.PagedResponse;
import com.ardms.entity.Deployment;
import com.ardms.entity.Environment;
import com.ardms.entity.Release;
import com.ardms.entity.User;
import com.ardms.entity.enums.DeploymentStatus;
import com.ardms.entity.enums.ReleaseStatus;
import com.ardms.exception.InvalidOperationException;
import com.ardms.exception.ResourceNotFoundException;
import com.ardms.mapper.DeploymentMapper;
import com.ardms.repository.DeploymentRepository;
import com.ardms.repository.EnvironmentRepository;
import com.ardms.repository.ReleaseRepository;
import com.ardms.repository.UserRepository;
import com.ardms.service.DeploymentService;
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
public class DeploymentServiceImpl implements DeploymentService {

    private static final Logger logger = LogManager.getLogger(DeploymentServiceImpl.class);
    private static final Logger deploymentLogger = LogManager.getLogger("com.ardms.deployment");

    @Autowired private DeploymentRepository deploymentRepository;
    @Autowired private ReleaseRepository releaseRepository;
    @Autowired private EnvironmentRepository environmentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private DeploymentMapper deploymentMapper;

    @Override
    public DeploymentResponse createDeployment(CreateDeploymentRequest request, String createdBy) {
        logger.info("Creating deployment for release: {} to environment: {}",
            request.getReleaseId(), request.getEnvironmentId());

        Release release = releaseRepository.findById(request.getReleaseId())
            .orElseThrow(() -> new ResourceNotFoundException("Release", "id", request.getReleaseId()));

        if (release.getStatus() == ReleaseStatus.CANCELLED ||
            release.getStatus() == ReleaseStatus.ROLLED_BACK) {
            throw new InvalidOperationException(
                "Cannot deploy a release with status: " + release.getStatus());
        }

        Environment environment = environmentRepository.findById(request.getEnvironmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Environment", "id", request.getEnvironmentId()));

        if (!environment.getIsActive()) {
            throw new InvalidOperationException("Environment '" + environment.getName() + "' is not active");
        }

        User deployedBy = userRepository.findByUsername(createdBy)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", createdBy));

        Deployment deployment = Deployment.builder()
            .release(release)
            .environment(environment)
            .status(DeploymentStatus.PENDING)
            .deploymentType(request.getDeploymentType())
            .deployedBy(deployedBy)
            .startedAt(LocalDateTime.now())
            .jenkinsBuildNumber(request.getJenkinsBuildNumber())
            .jenkinsBuildUrl(request.getJenkinsBuildUrl())
            .dockerImageTag(request.getDockerImageTag())
            .deploymentNotes(request.getDeploymentNotes())
            .createdBy(createdBy)
            .build();

        // Update release status to IN_PROGRESS if it was PLANNED
        if (release.getStatus() == ReleaseStatus.PLANNED || release.getStatus() == ReleaseStatus.DRAFT) {
            release.setStatus(ReleaseStatus.IN_PROGRESS);
            releaseRepository.save(release);
        }

        deployment = deploymentRepository.save(deployment);
        deploymentLogger.info("Deployment initiated: ID={} Release={} v{} Environment={} by {}",
            deployment.getId(), release.getReleaseName(), release.getVersion(),
            environment.getName(), createdBy);

        return deploymentMapper.toResponse(deployment);
    }

    @Override
    public DeploymentResponse updateDeploymentStatus(Long id, DeploymentStatus status,
            String errorMessage, String updatedBy) {
        Deployment deployment = findDeploymentById(id);
        DeploymentStatus oldStatus = deployment.getStatus();

        deployment.setStatus(status);
        deployment.setUpdatedBy(updatedBy);

        if (status == DeploymentStatus.SUCCESS || status == DeploymentStatus.FAILED ||
            status == DeploymentStatus.CANCELLED) {
            LocalDateTime completedAt = LocalDateTime.now();
            deployment.setCompletedAt(completedAt);
            if (deployment.getStartedAt() != null) {
                deployment.setDurationSeconds(
                    java.time.Duration.between(deployment.getStartedAt(), completedAt).getSeconds()
                );
            }
        }

        if (errorMessage != null) {
            deployment.setErrorMessage(errorMessage);
        }

        // Sync release status based on deployment outcome
        if (status == DeploymentStatus.SUCCESS) {
            Release release = deployment.getRelease();
            release.setStatus(ReleaseStatus.DEPLOYED);
            release.setActualDate(LocalDateTime.now());
            releaseRepository.save(release);
        }

        deployment = deploymentRepository.save(deployment);
        deploymentLogger.info("Deployment status updated: ID={} {} -> {} by {}",
            id, oldStatus, status, updatedBy);

        return deploymentMapper.toResponse(deployment);
    }

    @Override
    @Transactional(readOnly = true)
    public DeploymentResponse getDeploymentById(Long id) {
        return deploymentMapper.toResponse(findDeploymentById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DeploymentResponse> getAllDeployments(Pageable pageable) {
        Page<DeploymentResponse> page = deploymentRepository.findAll(pageable)
            .map(deploymentMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DeploymentResponse> getDeploymentsByRelease(Long releaseId, Pageable pageable) {
        Page<DeploymentResponse> page = deploymentRepository.findByReleaseId(releaseId, pageable)
            .map(deploymentMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DeploymentResponse> getDeploymentsByEnvironment(Long environmentId, Pageable pageable) {
        Page<DeploymentResponse> page = deploymentRepository.findByEnvironmentId(environmentId, pageable)
            .map(deploymentMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DeploymentResponse> getDeploymentsByStatus(DeploymentStatus status, Pageable pageable) {
        Page<DeploymentResponse> page = deploymentRepository.findByStatus(status, pageable)
            .map(deploymentMapper::toResponse);
        return PagedResponse.from(page);
    }

    private Deployment findDeploymentById(Long id) {
        return deploymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Deployment", "id", id));
    }
}
