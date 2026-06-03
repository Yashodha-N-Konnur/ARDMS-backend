package com.ardms.mapper;

import com.ardms.dto.request.CreateEnvironmentRequest;
import com.ardms.dto.response.EnvironmentResponse;
import com.ardms.entity.Environment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EnvironmentMapper {

    EnvironmentMapper INSTANCE = Mappers.getMapper(EnvironmentMapper.class);

    EnvironmentResponse toResponse(Environment environment);

    List<EnvironmentResponse> toResponseList(List<Environment> environments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", constant = "true")
    @Mapping(target = "configJson", ignore = true)
    @Mapping(target = "deployments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Environment toEntity(CreateEnvironmentRequest request);
}
