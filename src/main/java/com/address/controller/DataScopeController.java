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
@RequestMapping("/api/dataScopes")
public class DataScopeController {

    @Autowired
    private DataScopeService dataScopeService;

    @PostMapping("/query")
    public ApiResponse<List<DataScopeResponse>> query() {
        return ApiResponse.success(dataScopeService.query());
    }

    @PostMapping("/create")
    public ApiResponse<DataScopeResponse> create(@RequestBody DataScopeCreateRequest request) {
        return ApiResponse.success(dataScopeService.create(request));
    }

    @PostMapping("/update")
    public ApiResponse<DataScopeResponse> update(@RequestBody DataScopeUpdateRequest request) {
        return ApiResponse.success(dataScopeService.update(request));
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam Long scopeId) {
        dataScopeService.delete(scopeId);
        return ApiResponse.success(null);
    }
}
