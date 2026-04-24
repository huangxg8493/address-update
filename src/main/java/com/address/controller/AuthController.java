package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.LoginRequest;
import com.address.dto.LoginResponse;
import com.address.dto.LoginResult;
import com.address.dto.RegisterRequest;
import com.address.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/api/auth/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/api/auth/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResult result = authService.login(request);
        if (result.isSuccess()) {
            return ApiResponse.success(result.toLoginResponse(), result.getCode());
        }
        return ApiResponse.error(result.getCode(), result.getMessage());
    }

    @PostMapping("/api/auth/logout")
    public ApiResponse<Void> logout() {
        return ApiResponse.success(null);
    }
}
