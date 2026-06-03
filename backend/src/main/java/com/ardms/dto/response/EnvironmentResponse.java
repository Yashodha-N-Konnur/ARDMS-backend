package com.ardms.dto.response;

import com.ardms.entity.enums.EnvironmentType;
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
public class EnvironmentResponse {
    private Long id;
    private String name;
    private String description;
    private EnvironmentType envType;
    private Boolean isActive;
    private String baseUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
}
