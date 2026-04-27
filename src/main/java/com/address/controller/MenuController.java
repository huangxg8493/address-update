package com.address.controller;

import com.address.common.ApiResponse;
import com.address.common.ErrorCode;
import com.address.dto.MenuCreateRequest;
import com.address.dto.MenuQueryRequest;
import com.address.dto.MenuResponse;
import com.address.dto.MenuTreeResponse;
import com.address.dto.MenuUpdateRequest;
import com.address.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MenuController {

    @Autowired
    private MenuService menuService;

    @PostMapping("/api/menus/query")
    public ApiResponse<List<MenuResponse>> query(@RequestBody MenuQueryRequest request) {
        return ApiResponse.success(menuService.query(request));
    }

    @PostMapping("/api/menus/{menuId}")
    public ApiResponse<MenuResponse> getById(@PathVariable Long menuId) {
        try {
            return ApiResponse.success(menuService.getById(menuId));
        } catch (RuntimeException e) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/api/menus/create")
    public ApiResponse<MenuResponse> create(@RequestBody MenuCreateRequest request) {
        try {
            return ApiResponse.success(menuService.create(request));
        } catch (RuntimeException e) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/api/menus/update")
    public ApiResponse<MenuResponse> update(@RequestBody MenuUpdateRequest request) {
        try {
            return ApiResponse.success(menuService.update(request));
        } catch (RuntimeException e) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/api/menus/delete")
    public ApiResponse<Void> delete(@RequestParam Long menuId) {
        try {
            menuService.delete(menuId);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/api/menus/tree")
    public ApiResponse<List<MenuTreeResponse>> getTree() {
        return ApiResponse.success(menuService.getTree());
    }

    @PostMapping("/api/roles/{roleId}/menus/assign")
    public ApiResponse<Void> assignMenusToRole(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        menuService.assignMenusToRole(roleId, menuIds);
        return ApiResponse.success(null);
    }
}
