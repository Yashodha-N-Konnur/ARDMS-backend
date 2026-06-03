-- =====================================================
-- ARDMS - Seed Data V2
-- =====================================================

-- Insert Roles
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'System Administrator with full access'),
('ROLE_RELEASE_MANAGER', 'Can manage releases and deployments'),
('ROLE_DEVELOPER', 'Can view releases and create deployment requests'),
('ROLE_VIEWER', 'Read-only access to releases and deployments');

-- Insert Admin User (password: Admin@1234 - BCrypt encoded)
INSERT INTO users (username, email, password, first_name, last_name, is_active, created_by) VALUES
('admin', 'admin@ardms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh4W', 'System', 'Admin', TRUE, 'SYSTEM'),
('release_mgr', 'release.manager@ardms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh4W', 'Release', 'Manager', TRUE, 'SYSTEM'),
('dev_user', 'developer@ardms.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh4W', 'Dev', 'User', TRUE, 'SYSTEM');

-- Assign Roles
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username='admin' AND r.name='ROLE_ADMIN';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username='release_mgr' AND r.name='ROLE_RELEASE_MANAGER';

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r WHERE u.username='dev_user' AND r.name='ROLE_DEVELOPER';

-- Insert Environments
INSERT INTO environments (name, description, env_type, is_active, base_url, created_by) VALUES
('Development', 'Local development environment', 'DEVELOPMENT', TRUE, 'http://localhost:8080', 'SYSTEM'),
('QA', 'Quality Assurance testing environment', 'QA', TRUE, 'http://qa.ardms.internal', 'SYSTEM'),
('Staging', 'Pre-production staging environment', 'STAGING', TRUE, 'http://staging.ardms.internal', 'SYSTEM'),
('Production', 'Live production environment', 'PRODUCTION', TRUE, 'https://ardms.company.com', 'SYSTEM'),
('DR', 'Disaster Recovery environment', 'DR', TRUE, 'https://dr.ardms.company.com', 'SYSTEM');

-- Insert Sample Releases
INSERT INTO releases (release_name, version, description, status, release_type, planned_date, git_tag, git_branch, git_commit_hash, artifact_version, created_by) VALUES
('ARDMS Initial Release', '1.0.0', 'Initial production release of ARDMS', 'DEPLOYED', 'MAJOR', '2024-01-15 10:00:00', 'v1.0.0', 'main', 'abc123def456', '1.0.0', 'admin'),
('ARDMS Feature Update', '1.1.0', 'Added rollback support and enhanced monitoring', 'DEPLOYED', 'MINOR', '2024-02-01 10:00:00', 'v1.1.0', 'main', 'def456ghi789', '1.1.0', 'admin'),
('ARDMS Patch Release', '1.1.1', 'Bug fixes and performance improvements', 'PLANNED', 'PATCH', '2024-03-01 10:00:00', 'v1.1.1', 'main', 'ghi789jkl012', '1.1.1', 'release_mgr'),
('ARDMS Q2 Release', '1.2.0', 'Q2 feature set including advanced analytics', 'DRAFT', 'MINOR', '2024-06-01 10:00:00', NULL, 'develop', NULL, NULL, 'release_mgr');
