package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.UserCreateRequest;
import com.address.dto.UserQueryRequest;
import com.address.dto.UserResponse;
import com.address.dto.UserUpdateRequest;
import com.address.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/api/users/query")
    public ApiResponse<List<UserResponse>> query(@RequestBody UserQueryRequest request) {
        return ApiResponse.success(userService.query(request));
    }

    @PostMapping("/api/users/create")
    public ApiResponse<UserResponse> create(@RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.create(request));
    }

    @PostMapping("/api/users/update")
    public ApiResponse<UserResponse> update(@RequestBody UserUpdateRequest request) {
        return ApiResponse.success(userService.update(request));
    }

    @PostMapping("/api/users/delete")
    public ApiResponse<Void> delete(@RequestParam Long userId) {
        userService.delete(userId);
        return ApiResponse.success(null);
    }

    @PostMapping("/api/users/{userId}/roles/assign")
    public ApiResponse<Void> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return ApiResponse.success(null);
    }

    @PostMapping("/api/users/me/get")
    public ApiResponse<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(null);
    }
}
