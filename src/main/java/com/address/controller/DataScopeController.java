package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.DataScopeCreateRequest;
import com.address.dto.DataScopeResponse;
import com.address.dto.DataScopeUpdateRequest;
import com.address.service.DataScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DataScopeController {

    @Autowired
    private DataScopeService dataScopeService;

    @PostMapping("/api/dataScopes/query")
    public ApiResponse<List<DataScopeResponse>> query() {
        return ApiResponse.success(dataScopeService.query());
    }

    @PostMapping("/api/dataScopes/create")
    public ApiResponse<DataScopeResponse> create(@RequestBody DataScopeCreateRequest request) {
        return ApiResponse.success(dataScopeService.create(request));
    }

    @PostMapping("/api/dataScopes/update")
    public ApiResponse<DataScopeResponse> update(@RequestBody DataScopeUpdateRequest request) {
        return ApiResponse.success(dataScopeService.update(request));
    }

    @PostMapping("/api/dataScopes/delete")
    public ApiResponse<Void> delete(@RequestParam Long scopeId) {
        dataScopeService.delete(scopeId);
        return ApiResponse.success(null);
    }
}
