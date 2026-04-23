package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.RoleCreateRequest;
import com.address.dto.RoleQueryRequest;
import com.address.dto.RoleResponse;
import com.address.dto.RoleUpdateRequest;
import com.address.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/api/roles/query")
    public ApiResponse<List<RoleResponse>> query(@RequestBody RoleQueryRequest request) {
        return ApiResponse.success(roleService.query(request));
    }

    @PostMapping("/api/roles/create")
    public ApiResponse<RoleResponse> create(@RequestBody RoleCreateRequest request) {
        return ApiResponse.success(roleService.create(request));
    }

    @PostMapping("/api/roles/update")
    public ApiResponse<RoleResponse> update(@RequestBody RoleUpdateRequest request) {
        return ApiResponse.success(roleService.update(request));
    }

    @PostMapping("/api/roles/delete")
    public ApiResponse<Void> delete(@RequestParam Long roleId) {
        roleService.delete(roleId);
        return ApiResponse.success(null);
    }

    @PostMapping("/api/roles/{roleId}/permissions/assign")
    public ApiResponse<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return ApiResponse.success(null);
    }

    @PostMapping("/api/roles/{roleId}/dataScopes/assign")
    public ApiResponse<Void> assignDataScopes(@PathVariable Long roleId, @RequestBody List<Long> scopeIds) {
        roleService.assignDataScopes(roleId, scopeIds);
        return ApiResponse.success(null);
    }
}
