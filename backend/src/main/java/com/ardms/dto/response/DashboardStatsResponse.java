package com.ardms.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardStatsResponse {
    private long totalReleases;
    private long draftReleases;
    private long plannedReleases;
    private long deployedReleases;
    private long failedReleases;
    private long totalDeployments;
    private long successfulDeployments;
    private long failedDeployments;
    private long pendingDeployments;
    private long deploymentsLast30Days;
    private long totalRollbacks;
    private long successfulRollbacks;
    private long totalEnvironments;
    private long activeEnvironments;
    private long totalUsers;
}
