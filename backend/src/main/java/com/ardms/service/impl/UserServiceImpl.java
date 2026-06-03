package com.ardms.service.impl;

import com.ardms.dto.response.PagedResponse;
import com.ardms.dto.response.UserResponse;
import com.ardms.entity.User;
import com.ardms.exception.ResourceNotFoundException;
import com.ardms.mapper.UserMapper;
import com.ardms.repository.UserRepository;
import com.ardms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> getAllUsers(Pageable pageable) {
        Page<UserResponse> page = userRepository.findAll(pageable).map(userMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<UserResponse> searchUsers(String search, Pageable pageable) {
        Page<UserResponse> page = userRepository.searchActiveUsers(search, pageable).map(userMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    public UserResponse toggleUserStatus(Long id, String updatedBy) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setIsActive(!user.getIsActive());
        user.setUpdatedBy(updatedBy);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }
}
