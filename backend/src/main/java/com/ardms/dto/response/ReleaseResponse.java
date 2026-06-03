package com.ardms.dto.response;

import com.ardms.entity.enums.ReleaseStatus;
import com.ardms.entity.enums.ReleaseType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReleaseResponse {
    private Long id;
    private String releaseName;
    private String version;
    private String description;
    private ReleaseStatus status;
    private ReleaseType releaseType;
    private LocalDateTime plannedDate;
    private LocalDateTime actualDate;
    private String releaseNotes;
    private String gitTag;
    private String gitBranch;
    private String gitCommitHash;
    private String artifactPath;
    private String artifactVersion;
    private int totalDeployments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
