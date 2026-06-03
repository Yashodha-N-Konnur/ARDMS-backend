package com.ardms.repository;

import com.ardms.entity.Environment;
import com.ardms.entity.enums.EnvironmentType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnvironmentRepository extends JpaRepository<Environment, Long> {

    Optional<Environment> findByName(String name);

    boolean existsByName(String name);

    List<Environment> findByIsActiveTrue();

    Page<Environment> findByIsActiveTrue(Pageable pageable);

    List<Environment> findByEnvType(EnvironmentType envType);
}
