package com.ardms.dto.response;

import com.ardms.entity.enums.DeploymentStatus;
import com.ardms.entity.enums.DeploymentType;
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
public class DeploymentResponse {
    private Long id;
    private Long releaseId;
    private String releaseVersion;
    private String releaseName;
    private Long environmentId;
    private String environmentName;
    private String environmentType;
    private DeploymentStatus status;
    private DeploymentType deploymentType;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long durationSeconds;
    private String deployedByUsername;
    private String deployedByFullName;
    private String jenkinsBuildNumber;
    private String jenkinsBuildUrl;
    private String dockerImageTag;
    private String deploymentNotes;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
