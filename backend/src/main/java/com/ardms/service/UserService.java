package com.ardms.service;

import com.ardms.dto.response.PagedResponse;
import com.ardms.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse getUserByUsername(String username);
    PagedResponse<UserResponse> getAllUsers(Pageable pageable);
    PagedResponse<UserResponse> searchUsers(String search, Pageable pageable);
    UserResponse toggleUserStatus(Long id, String updatedBy);
}
