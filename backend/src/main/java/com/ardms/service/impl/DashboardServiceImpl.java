package com.ardms.service.impl;

import com.ardms.dto.response.DashboardStatsResponse;
import com.ardms.entity.enums.DeploymentStatus;
import com.ardms.entity.enums.ReleaseStatus;
import com.ardms.entity.enums.RollbackStatus;
import com.ardms.repository.DeploymentRepository;
import com.ardms.repository.EnvironmentRepository;
import com.ardms.repository.ReleaseRepository;
import com.ardms.repository.RollbackHistoryRepository;
import com.ardms.repository.UserRepository;
import com.ardms.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    @Autowired private ReleaseRepository releaseRepository;
    @Autowired private DeploymentRepository deploymentRepository;
    @Autowired private RollbackHistoryRepository rollbackHistoryRepository;
    @Autowired private EnvironmentRepository environmentRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        return DashboardStatsResponse.builder()
            .totalReleases(releaseRepository.count())
            .draftReleases(releaseRepository.countByStatus(ReleaseStatus.DRAFT))
            .plannedReleases(releaseRepository.countByStatus(ReleaseStatus.PLANNED))
            .deployedReleases(releaseRepository.countByStatus(ReleaseStatus.DEPLOYED))
            .failedReleases(releaseRepository.countByStatus(ReleaseStatus.FAILED))
            .totalDeployments(deploymentRepository.count())
            .successfulDeployments(deploymentRepository.countByStatusSince(DeploymentStatus.SUCCESS, LocalDateTime.of(2000,1,1,0,0)))
            .failedDeployments(deploymentRepository.countByStatusSince(DeploymentStatus.FAILED, LocalDateTime.of(2000,1,1,0,0)))
            .pendingDeployments(deploymentRepository.countByStatusSince(DeploymentStatus.PENDING, LocalDateTime.of(2000,1,1,0,0)))
            .deploymentsLast30Days(deploymentRepository.countByStatusSince(DeploymentStatus.SUCCESS, thirtyDaysAgo) +
                deploymentRepository.countByStatusSince(DeploymentStatus.FAILED, thirtyDaysAgo))
            .totalRollbacks(rollbackHistoryRepository.count())
            .successfulRollbacks(rollbackHistoryRepository.countByStatus(RollbackStatus.SUCCESS))
            .totalEnvironments(environmentRepository.count())
            .activeEnvironments(environmentRepository.findByIsActiveTrue().size())
            .totalUsers(userRepository.count())
            .build();
    }
}
