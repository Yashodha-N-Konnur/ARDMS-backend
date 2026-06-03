package com.ardms.service;

import com.ardms.dto.request.CreateReleaseRequest;
import com.ardms.dto.request.UpdateReleaseRequest;
import com.ardms.dto.response.PagedResponse;
import com.ardms.dto.response.ReleaseResponse;
import com.ardms.entity.enums.ReleaseStatus;
import org.springframework.data.domain.Pageable;

public interface ReleaseService {
    ReleaseResponse createRelease(CreateReleaseRequest request, String createdBy);
    ReleaseResponse updateRelease(Long id, UpdateReleaseRequest request, String updatedBy);
    ReleaseResponse getReleaseById(Long id);
    ReleaseResponse getReleaseByVersion(String version);
    PagedResponse<ReleaseResponse> getAllReleases(Pageable pageable);
    PagedResponse<ReleaseResponse> getReleasesByStatus(ReleaseStatus status, Pageable pageable);
    PagedResponse<ReleaseResponse> searchReleases(String search, Pageable pageable);
    ReleaseResponse updateReleaseStatus(Long id, ReleaseStatus status, String updatedBy);
    void deleteRelease(Long id);
}
