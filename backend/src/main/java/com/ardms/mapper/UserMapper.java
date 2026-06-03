package com.ardms.mapper;

import com.ardms.dto.response.UserResponse;
import com.ardms.entity.Role;
import com.ardms.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "fullName", expression = "java(user.getFullName())")
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToStrings")
    UserResponse toResponse(User user);

    List<UserResponse> toResponseList(List<User> users);

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        if (roles == null) return Set.of();
        return roles.stream().map(Role::getName).collect(Collectors.toSet());
    }
}
