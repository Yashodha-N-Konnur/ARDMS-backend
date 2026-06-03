-- =====================================================
-- ARDMS - Automated Release & Deployment Management System
-- Database Schema V1 - Initial Setup
-- =====================================================

-- Roles Table
CREATE TABLE roles (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_roles_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Users Table
CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User-Role Association Table
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Environments Table
CREATE TABLE environments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(500),
    env_type ENUM('DEVELOPMENT','QA','STAGING','PRODUCTION','DR') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    base_url VARCHAR(255),
    config_json TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uk_environments_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Releases Table
CREATE TABLE releases (
    id BIGINT NOT NULL AUTO_INCREMENT,
    release_name VARCHAR(100) NOT NULL,
    version VARCHAR(50) NOT NULL,
    description TEXT,
    status ENUM('DRAFT','PLANNED','IN_PROGRESS','DEPLOYED','FAILED','ROLLED_BACK','CANCELLED') NOT NULL DEFAULT 'DRAFT',
    release_type ENUM('MAJOR','MINOR','PATCH','HOTFIX','EMERGENCY') NOT NULL DEFAULT 'MINOR',
    planned_date TIMESTAMP,
    actual_date TIMESTAMP,
    release_notes TEXT,
    git_tag VARCHAR(100),
    git_branch VARCHAR(100),
    git_commit_hash VARCHAR(50),
    artifact_path VARCHAR(500),
    artifact_version VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    PRIMARY KEY (id),
    UNIQUE KEY uk_releases_version (version),
    INDEX idx_releases_status (status),
    INDEX idx_releases_planned_date (planned_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Deployments Table
CREATE TABLE deployments (
    id BIGINT NOT NULL AUTO_INCREMENT,
    release_id BIGINT NOT NULL,
    environment_id BIGINT NOT NULL,
    status ENUM('PENDING','IN_PROGRESS','SUCCESS','FAILED','ROLLED_BACK','CANCELLED') NOT NULL DEFAULT 'PENDING',
    deployment_type ENUM('FULL','INCREMENTAL','HOTFIX','ROLLBACK') NOT NULL DEFAULT 'FULL',
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    duration_seconds BIGINT,
    deployed_by BIGINT NOT NULL,
    jenkins_build_number VARCHAR(50),
    jenkins_build_url VARCHAR(500),
    docker_image_tag VARCHAR(255),
    deployment_notes TEXT,
    error_message TEXT,
    config_snapshot TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_by VARCHAR(50),
    PRIMARY KEY (id),
    CONSTRAINT fk_deployments_release FOREIGN KEY (release_id) REFERENCES releases (id),
    CONSTRAINT fk_deployments_environment FOREIGN KEY (environment_id) REFERENCES environments (id),
    CONSTRAINT fk_deployments_user FOREIGN KEY (deployed_by) REFERENCES users (id),
    INDEX idx_deployments_status (status),
    INDEX idx_deployments_release (release_id),
    INDEX idx_deployments_environment (environment_id),
    INDEX idx_deployments_started_at (started_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Rollback History Table
CREATE TABLE rollback_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    deployment_id BIGINT NOT NULL,
    rollback_to_deployment_id BIGINT,
    initiated_by BIGINT NOT NULL,
    reason TEXT NOT NULL,
    status ENUM('INITIATED','IN_PROGRESS','SUCCESS','FAILED') NOT NULL DEFAULT 'INITIATED',
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    duration_seconds BIGINT,
    jenkins_build_number VARCHAR(50),
    error_message TEXT,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    PRIMARY KEY (id),
    CONSTRAINT fk_rollback_deployment FOREIGN KEY (deployment_id) REFERENCES deployments (id),
    CONSTRAINT fk_rollback_to_deployment FOREIGN KEY (rollback_to_deployment_id) REFERENCES deployments (id),
    CONSTRAINT fk_rollback_user FOREIGN KEY (initiated_by) REFERENCES users (id),
    INDEX idx_rollback_deployment (deployment_id),
    INDEX idx_rollback_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Refresh Tokens Table
CREATE TABLE refresh_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    token VARCHAR(512) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_refresh_tokens_token (token),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    INDEX idx_refresh_tokens_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
