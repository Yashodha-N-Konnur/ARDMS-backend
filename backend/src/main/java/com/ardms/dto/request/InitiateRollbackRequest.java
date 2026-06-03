package com.ardms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitiateRollbackRequest {

    @NotNull(message = "Deployment ID is required")
    private Long deploymentId;

    private Long rollbackToDeploymentId;

    @NotBlank(message = "Reason for rollback is required")
    @Size(max = 5000, message = "Reason must not exceed 5000 characters")
    private String reason;

    @Size(max = 5000, message = "Notes must not exceed 5000 characters")
    private String notes;
}
