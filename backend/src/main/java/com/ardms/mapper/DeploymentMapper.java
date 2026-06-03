package com.ardms.mapper;

import com.ardms.dto.response.DeploymentResponse;
import com.ardms.entity.Deployment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeploymentMapper {

    DeploymentMapper INSTANCE = Mappers.getMapper(DeploymentMapper.class);

    @Mapping(target = "releaseId", source = "release.id")
    @Mapping(target = "releaseVersion", source = "release.version")
    @Mapping(target = "releaseName", source = "release.releaseName")
    @Mapping(target = "environmentId", source = "environment.id")
    @Mapping(target = "environmentName", source = "environment.name")
    @Mapping(target = "environmentType", source = "environment.envType")
    @Mapping(target = "deployedByUsername", source = "deployedBy.username")
    @Mapping(target = "deployedByFullName", expression = "java(deployment.getDeployedBy().getFullName())")
    DeploymentResponse toResponse(Deployment deployment);

    List<DeploymentResponse> toResponseList(List<Deployment> deployments);
}
