package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.PermissionCreateRequest;
import com.address.dto.PermissionResponse;
import com.address.dto.PermissionUpdateRequest;
import com.address.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/api/permissions/query")
    public ApiResponse<List<PermissionResponse>> query() {
        return ApiResponse.success(permissionService.query());
    }

    @PostMapping("/api/permissions/create")
    public ApiResponse<PermissionResponse> create(@RequestBody PermissionCreateRequest request) {
        return ApiResponse.success(permissionService.create(request));
    }

    @PostMapping("/api/permissions/update")
    public ApiResponse<PermissionResponse> update(@RequestBody PermissionUpdateRequest request) {
        return ApiResponse.success(permissionService.update(request));
    }

    @PostMapping("/api/permissions/delete")
    public ApiResponse<Void> delete(@RequestParam Long permissionId) {
        permissionService.delete(permissionId);
        return ApiResponse.success(null);
    }
}
