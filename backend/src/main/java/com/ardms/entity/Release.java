package com.ardms.entity;

import com.ardms.entity.enums.ReleaseStatus;
import com.ardms.entity.enums.ReleaseType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "releases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Release {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "release_name", nullable = false, length = 100)
    private String releaseName;

    @Column(name = "version", nullable = false, unique = true, length = 50)
    private String version;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ReleaseStatus status = ReleaseStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "release_type", nullable = false)
    @Builder.Default
    private ReleaseType releaseType = ReleaseType.MINOR;

    @Column(name = "planned_date")
    private LocalDateTime plannedDate;

    @Column(name = "actual_date")
    private LocalDateTime actualDate;

    @Column(name = "release_notes", columnDefinition = "TEXT")
    private String releaseNotes;

    @Column(name = "git_tag", length = 100)
    private String gitTag;

    @Column(name = "git_branch", length = 100)
    private String gitBranch;

    @Column(name = "git_commit_hash", length = 50)
    private String gitCommitHash;

    @Column(name = "artifact_path", length = 500)
    private String artifactPath;

    @Column(name = "artifact_version", length = 100)
    private String artifactVersion;

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

    @OneToMany(mappedBy = "release", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Deployment> deployments = new ArrayList<>();
}
