package com.ardms.repository;

import com.ardms.entity.RollbackHistory;
import com.ardms.entity.enums.RollbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RollbackHistoryRepository extends JpaRepository<RollbackHistory, Long> {

    Page<RollbackHistory> findByDeploymentId(Long deploymentId, Pageable pageable);

    List<RollbackHistory> findByStatus(RollbackStatus status);

    long countByStatus(RollbackStatus status);
}
