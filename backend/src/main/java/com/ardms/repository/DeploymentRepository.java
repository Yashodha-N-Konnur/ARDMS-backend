package com.ardms.repository;

import com.ardms.entity.Deployment;
import com.ardms.entity.enums.DeploymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeploymentRepository extends JpaRepository<Deployment, Long> {

    Page<Deployment> findByReleaseId(Long releaseId, Pageable pageable);

    Page<Deployment> findByEnvironmentId(Long environmentId, Pageable pageable);

    Page<Deployment> findByStatus(DeploymentStatus status, Pageable pageable);

    List<Deployment> findByReleaseIdAndEnvironmentId(Long releaseId, Long environmentId);

    Optional<Deployment> findTopByEnvironmentIdOrderByCreatedAtDesc(Long environmentId);

    @Query("SELECT d FROM Deployment d WHERE d.environment.id = :envId " +
           "AND d.status = 'SUCCESS' ORDER BY d.completedAt DESC")
    List<Deployment> findLastSuccessfulDeploymentForEnvironment(@Param("envId") Long envId, Pageable pageable);

    @Query("SELECT d FROM Deployment d WHERE d.startedAt BETWEEN :start AND :end")
    Page<Deployment> findDeploymentsBetween(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end,
        Pageable pageable
    );

    @Query("SELECT COUNT(d) FROM Deployment d WHERE d.status = :status " +
           "AND d.startedAt >= :since")
    long countByStatusSince(@Param("status") DeploymentStatus status, @Param("since") LocalDateTime since);

    @Query("SELECT d FROM Deployment d WHERE d.deployedBy.id = :userId ORDER BY d.createdAt DESC")
    Page<Deployment> findByDeployedByUserId(@Param("userId") Long userId, Pageable pageable);
}
