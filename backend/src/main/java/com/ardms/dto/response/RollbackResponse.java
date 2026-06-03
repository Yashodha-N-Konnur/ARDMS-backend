package com.ardms.dto.response;

import com.ardms.entity.enums.RollbackStatus;
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
public class RollbackResponse {
    private Long id;
    private Long deploymentId;
    private Long rollbackToDeploymentId;
    private String initiatedByUsername;
    private String reason;
    private RollbackStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long durationSeconds;
    private String jenkinsBuildNumber;
    private String errorMessage;
    private String notes;
    private LocalDateTime createdAt;
}
