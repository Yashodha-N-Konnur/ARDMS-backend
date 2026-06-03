package com.ardms.dto.request;

import com.ardms.entity.enums.DeploymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeploymentRequest {

    @NotNull(message = "Release ID is required")
    private Long releaseId;

    @NotNull(message = "Environment ID is required")
    private Long environmentId;

    @NotNull(message = "Deployment type is required")
    private DeploymentType deploymentType;

    @Size(max = 50, message = "Jenkins build number must not exceed 50 characters")
    private String jenkinsBuildNumber;

    @Size(max = 500, message = "Jenkins build URL must not exceed 500 characters")
    private String jenkinsBuildUrl;

    @Size(max = 255, message = "Docker image tag must not exceed 255 characters")
    private String dockerImageTag;

    @Size(max = 5000, message = "Deployment notes must not exceed 5000 characters")
    private String deploymentNotes;
}
