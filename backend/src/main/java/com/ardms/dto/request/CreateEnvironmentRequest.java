package com.ardms.dto.request;

import com.ardms.entity.enums.EnvironmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnvironmentRequest {

    @NotBlank(message = "Environment name is required")
    @Size(max = 50, message = "Environment name must not exceed 50 characters")
    private String name;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "Environment type is required")
    private EnvironmentType envType;

    @Size(max = 255, message = "Base URL must not exceed 255 characters")
    private String baseUrl;
}
