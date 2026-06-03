package com.ardms.dto.request;

import com.ardms.entity.enums.ReleaseStatus;
import com.ardms.entity.enums.ReleaseType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateReleaseRequest {

    @Size(max = 100, message = "Release name must not exceed 100 characters")
    private String releaseName;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    private ReleaseStatus status;

    private ReleaseType releaseType;

    private LocalDateTime plannedDate;

    @Size(max = 10000, message = "Release notes must not exceed 10000 characters")
    private String releaseNotes;

    @Size(max = 100, message = "Git tag must not exceed 100 characters")
    private String gitTag;

    @Size(max = 100, message = "Git branch must not exceed 100 characters")
    private String gitBranch;

    @Size(max = 50, message = "Git commit hash must not exceed 50 characters")
    private String gitCommitHash;

    @Size(max = 500, message = "Artifact path must not exceed 500 characters")
    private String artifactPath;

    @Size(max = 100, message = "Artifact version must not exceed 100 characters")
    private String artifactVersion;
}
