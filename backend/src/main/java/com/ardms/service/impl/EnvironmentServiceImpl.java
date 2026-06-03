package com.ardms.service.impl;

import com.ardms.dto.request.CreateEnvironmentRequest;
import com.ardms.dto.response.EnvironmentResponse;
import com.ardms.dto.response.PagedResponse;
import com.ardms.entity.Environment;
import com.ardms.exception.ResourceAlreadyExistsException;
import com.ardms.exception.ResourceNotFoundException;
import com.ardms.mapper.EnvironmentMapper;
import com.ardms.repository.EnvironmentRepository;
import com.ardms.service.EnvironmentService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnvironmentServiceImpl implements EnvironmentService {

    private static final Logger logger = LogManager.getLogger(EnvironmentServiceImpl.class);

    @Autowired private EnvironmentRepository environmentRepository;
    @Autowired private EnvironmentMapper environmentMapper;

    @Override
    public EnvironmentResponse createEnvironment(CreateEnvironmentRequest request, String createdBy) {
        if (environmentRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistsException("Environment", "name", request.getName());
        }

        Environment environment = environmentMapper.toEntity(request);
        environment.setCreatedBy(createdBy);
        environment = environmentRepository.save(environment);

        logger.info("Environment created: {} type: {} by {}", environment.getName(), environment.getEnvType(), createdBy);
        return environmentMapper.toResponse(environment);
    }

    @Override
    @Transactional(readOnly = true)
    public EnvironmentResponse getEnvironmentById(Long id) {
        return environmentMapper.toResponse(findEnvironmentById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnvironmentResponse> getAllActiveEnvironments() {
        return environmentMapper.toResponseList(environmentRepository.findByIsActiveTrue());
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<EnvironmentResponse> getAllEnvironments(Pageable pageable) {
        Page<EnvironmentResponse> page = environmentRepository.findAll(pageable)
            .map(environmentMapper::toResponse);
        return PagedResponse.from(page);
    }

    @Override
    public EnvironmentResponse toggleEnvironmentStatus(Long id, String updatedBy) {
        Environment environment = findEnvironmentById(id);
        environment.setIsActive(!environment.getIsActive());
        environment.setUpdatedBy(updatedBy);
        environment = environmentRepository.save(environment);
        logger.info("Environment {} toggled to active={} by {}", environment.getName(), environment.getIsActive(), updatedBy);
        return environmentMapper.toResponse(environment);
    }

    private Environment findEnvironmentById(Long id) {
        return environmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Environment", "id", id));
    }
}
