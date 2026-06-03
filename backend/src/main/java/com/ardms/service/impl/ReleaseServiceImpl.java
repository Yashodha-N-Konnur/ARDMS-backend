package com.ardms.service.impl;

import com.ardms.dto.request.CreateReleaseRequest;
import com.ardms.dto.request.UpdateReleaseRequest;
import com.ardms.dto.response.PagedResponse;
import com.ardms.dto.response.ReleaseResponse;
import com.ardms.entity.Release;
import com.ardms.entity.enums.ReleaseStatus;
import com.ardms.exception.InvalidOperationException;
import com.ardms.exception.ResourceAlreadyExistsException;
import com.ardms.exception.ResourceNotFoundException;
import com.ardms.mapper.ReleaseMapper;
import com.ardms.repository.ReleaseRepository;
import com.ardms.service.ReleaseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;

@Service
@Transactional
public class ReleaseServiceImpl implements ReleaseService {

    private static final Logger logger = LogManager.getLogger(ReleaseServiceImpl.class);
    private static final Logger deploymentLogger = LogManager.getLogger("com.ardms.deployment");

    @Autowired private ReleaseRepository releaseRepository;
    @Autowired private ReleaseMapper releaseMapper;

    @Override
    public ReleaseResponse createRelease(CreateReleaseRequest request, String createdBy) {
        logger.info("Creating release: {} version: {}", request.getReleaseName(), request.getVersion());

        if (releaseRepository.existsByVersion(request.getVersion())) {
            throw new ResourceAlreadyExistsException("Release", "version", request.getVersion());
        }

        Release release = releaseMapper.toEntity(request);
        release.setStatus(ReleaseStatus.DRAFT);
        release.setCreatedBy(createdBy);

        release = releaseRepository.save(release);
        deploymentLogger.info("Release created: {} v{} by {}", release.getReleaseName(), release.getVersion(), createdBy);

        return releaseMapper.toResponse(release);
    }

    @Override
    public ReleaseResponse updateRelease(Long id, UpdateReleaseRequest request, String updatedBy) {
        logger.info("Updating release ID: {}", id);

        Release release = findReleaseById(id);

        // Cannot update a deployed/cancelled release
        if (EnumSet.of(ReleaseStatus.DEPLOYED, ReleaseStatus.CANCELLED).contains(release.getStatus())) {
            throw new InvalidOperationException(
                "Cannot modify a release with status: " + release.getStatus());
        }

        if (request.getReleaseName() != null) release.setReleaseName(request.getReleaseName());
        if (request.getDescription() != null) release.setDescription(request.getDescription());
        if (request.getStatus() != null) release.setStatus(request.getStatus());
        if (request.getReleaseType() != null) release.setReleaseType(request.getReleaseType());
        if (request.getPlannedDate() != null) release.setPlannedDate(request.getPlannedDate());
        if (request.getReleaseNotes() != null) release.setReleaseNotes(request.getReleaseNotes());
        if (request.getGitTag() != null) release.setGitTag(request.getGitTag());
        if (request.getGitBranch() != null) release.setGitBranch(request.getGitBranch());
        if (request.getGitCommitHash() != null) release.setGitCommitHash(request.getGitCommitHash());
        if (request.getArtifactPath() != null) release.setArtifactPath(request.getArtifactPath());
        if (request.getArtifactVersion() != null) release.setArtifactVersion(request.getArtifactVersion());
        release.setUpdatedBy(updatedBy);

        release = releaseRepository.save(release);
        logger.info("Release updated: ID={} by {}", id, updatedBy);

        return releaseMapper.toResponse(release);
    }

    @Override
    @Transactional(readOnly = true)
    public ReleaseResponse getReleaseById(Long id) {
        return releaseMapper.toResponse(findReleaseById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ReleaseResponse getReleaseByVersion(String version) {
        Release release = releaseRepository.findByVersion(version)
            .orElseThrow(() -> new ResourceNotFoundException("Release", "version", version));
        return releaseMapper.toResponse(release);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReleaseResponse> getAllReleases(Pageable pageable) {
        Page<ReleaseResponse> page = releaseRepository.findAll(pageable)
            .map(releaseMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReleaseResponse> getReleasesByStatus(ReleaseStatus status, Pageable pageable) {
        Page<ReleaseResponse> page = releaseRepository.findByStatus(status, pageable)
            .map(releaseMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReleaseResponse> searchReleases(String search, Pageable pageable) {
        Page<ReleaseResponse> page = releaseRepository.searchReleases(search, pageable)
            .map(releaseMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    public ReleaseResponse updateReleaseStatus(Long id, ReleaseStatus status, String updatedBy) {
        Release release = findReleaseById(id);
        ReleaseStatus oldStatus = release.getStatus();
        release.setStatus(status);
        release.setUpdatedBy(updatedBy);

        if (status == ReleaseStatus.DEPLOYED) {
            release.setActualDate(LocalDateTime.now());
        }

        release = releaseRepository.save(release);
        deploymentLogger.info("Release status changed: ID={} v{} {} -> {} by {}",
            id, release.getVersion(), oldStatus, status, updatedBy);

        return releaseMapper.toResponse(release);
    }

    @Override
    public void deleteRelease(Long id) {
        Release release = findReleaseById(id);

        if (!EnumSet.of(ReleaseStatus.DRAFT, ReleaseStatus.CANCELLED).contains(release.getStatus())) {
            throw new InvalidOperationException(
                "Can only delete releases in DRAFT or CANCELLED status. Current: " + release.getStatus());
        }

        releaseRepository.delete(release);
        logger.info("Release deleted: ID={} v{}", id, release.getVersion());
    }

    private Release findReleaseById(Long id) {
        return releaseRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Release", "id", id));
    }
}
