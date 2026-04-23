package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.LoginRequest;
import com.address.dto.LoginResponse;
import com.address.dto.RegisterRequest;
import com.address.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // JWT 无状态，客户端删除 token 即可
        return ApiResponse.success(null);
    }
}
