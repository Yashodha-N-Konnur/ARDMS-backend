package com.ardms.mapper;

import com.ardms.dto.response.RollbackResponse;
import com.ardms.entity.RollbackHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RollbackMapper {

    RollbackMapper INSTANCE = Mappers.getMapper(RollbackMapper.class);

    @Mapping(target = "deploymentId", source = "deployment.id")
    @Mapping(target = "rollbackToDeploymentId", source = "rollbackToDeployment.id")
    @Mapping(target = "initiatedByUsername", source = "initiatedBy.username")
    RollbackResponse toResponse(RollbackHistory rollbackHistory);

    List<RollbackResponse> toResponseList(List<RollbackHistory> rollbackHistories);
}
