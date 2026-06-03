package com.ardms.entity;

import com.ardms.entity.enums.DeploymentStatus;
import com.ardms.entity.enums.DeploymentType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "deployments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Deployment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "release_id", nullable = false)
    private Release release;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "environment_id", nullable = false)
    private Environment environment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private DeploymentStatus status = DeploymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "deployment_type", nullable = false)
    @Builder.Default
    private DeploymentType deploymentType = DeploymentType.FULL;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "duration_seconds")
    private Long durationSeconds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deployed_by", nullable = false)
    private User deployedBy;

    @Column(name = "jenkins_build_number", length = 50)
    private String jenkinsBuildNumber;

    @Column(name = "jenkins_build_url", length = 500)
    private String jenkinsBuildUrl;

    @Column(name = "docker_image_tag", length = 255)
    private String dockerImageTag;

    @Column(name = "deployment_notes", columnDefinition = "TEXT")
    private String deploymentNotes;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "config_snapshot", columnDefinition = "TEXT")
    private String configSnapshot;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    @OneToMany(mappedBy = "deployment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<RollbackHistory> rollbackHistories = new ArrayList<>();
}
