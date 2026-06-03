package com.ardms.repository;

import com.ardms.entity.Release;
import com.ardms.entity.enums.ReleaseStatus;
import com.ardms.entity.enums.ReleaseType;
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
public interface ReleaseRepository extends JpaRepository<Release, Long> {

    Optional<Release> findByVersion(String version);

    boolean existsByVersion(String version);

    Page<Release> findByStatus(ReleaseStatus status, Pageable pageable);

    Page<Release> findByReleaseType(ReleaseType releaseType, Pageable pageable);

    List<Release> findByStatusAndPlannedDateBetween(
        ReleaseStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    @Query("SELECT r FROM Release r WHERE " +
           "(LOWER(r.releaseName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.version) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(r.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Release> searchReleases(@Param("search") String search, Pageable pageable);

    @Query("SELECT r FROM Release r WHERE r.status NOT IN ('CANCELLED', 'ROLLED_BACK') " +
           "ORDER BY r.createdAt DESC")
    Page<Release> findActiveReleases(Pageable pageable);

    @Query("SELECT COUNT(r) FROM Release r WHERE r.status = :status")
    long countByStatus(@Param("status") ReleaseStatus status);
}
