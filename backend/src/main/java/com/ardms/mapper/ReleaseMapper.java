package com.ardms.mapper;

import com.ardms.dto.request.CreateReleaseRequest;
import com.ardms.dto.response.ReleaseResponse;
import com.ardms.entity.Release;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReleaseMapper {

    ReleaseMapper INSTANCE = Mappers.getMapper(ReleaseMapper.class);

    @Mapping(target = "totalDeployments", expression = "java(release.getDeployments() != null ? release.getDeployments().size() : 0)")
    ReleaseResponse toResponse(Release release);

    List<ReleaseResponse> toResponseList(List<Release> releases);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "actualDate", ignore = true)
    @Mapping(target = "deployments", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    Release toEntity(CreateReleaseRequest request);
}
