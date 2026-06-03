package com.ardms.service;

import com.ardms.dto.request.CreateReleaseRequest;
import com.ardms.dto.response.ReleaseResponse;
import com.ardms.entity.Release;
import com.ardms.entity.enums.ReleaseStatus;
import com.ardms.entity.enums.ReleaseType;
import com.ardms.exception.ResourceAlreadyExistsException;
import com.ardms.mapper.ReleaseMapper;
import com.ardms.repository.ReleaseRepository;
import com.ardms.service.impl.ReleaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReleaseServiceTest {

    @Mock
    private ReleaseRepository releaseRepository;

    @Mock
    private ReleaseMapper releaseMapper;

    @InjectMocks
    private ReleaseServiceImpl releaseService;

    private CreateReleaseRequest createRequest;
    private Release release;
    private ReleaseResponse releaseResponse;

    @BeforeEach
    void setUp() {
        createRequest = new CreateReleaseRequest();
        createRequest.setReleaseName("Test Release");
        createRequest.setVersion("1.0.0");
        createRequest.setReleaseType(ReleaseType.MINOR);

        release = Release.builder()
            .id(1L)
            .releaseName("Test Release")
            .version("1.0.0")
            .status(ReleaseStatus.DRAFT)
            .releaseType(ReleaseType.MINOR)
            .createdBy("admin")
            .build();

        releaseResponse = ReleaseResponse.builder()
            .id(1L)
            .releaseName("Test Release")
            .version("1.0.0")
            .status(ReleaseStatus.DRAFT)
            .releaseType(ReleaseType.MINOR)
            .build();
    }

    @Test
    void createRelease_Success() {
        when(releaseRepository.existsByVersion("1.0.0")).thenReturn(false);
        when(releaseMapper.toEntity(any(CreateReleaseRequest.class))).thenReturn(release);
        when(releaseRepository.save(any(Release.class))).thenReturn(release);
        when(releaseMapper.toResponse(any(Release.class))).thenReturn(releaseResponse);

        ReleaseResponse result = releaseService.createRelease(createRequest, "admin");

        assertNotNull(result);
        assertEquals("Test Release", result.getReleaseName());
        assertEquals("1.0.0", result.getVersion());
        verify(releaseRepository, times(1)).save(any(Release.class));
    }

    @Test
    void createRelease_DuplicateVersion_ThrowsException() {
        when(releaseRepository.existsByVersion("1.0.0")).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
            () -> releaseService.createRelease(createRequest, "admin"));

        verify(releaseRepository, never()).save(any(Release.class));
    }

    @Test
    void getReleaseById_NotFound_ThrowsException() {
        when(releaseRepository.findById(99L)).thenReturn(java.util.Optional.empty());

        assertThrows(com.ardms.exception.ResourceNotFoundException.class,
            () -> releaseService.getReleaseById(99L));
    }
}
