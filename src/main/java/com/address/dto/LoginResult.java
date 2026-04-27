package com.address.dto;

import com.address.common.ErrorCode;

public class LoginResult {
    private String code;
    private String message;
    private String token;
    private String phone;
    private LoginResponse loginResponse;

    private LoginResult(String code, String message, String token, String phone) {
        this.code = code;
        this.message = message;
        this.token = token;
        this.phone = phone;
    }

    private LoginResult(String code, String message, LoginResponse loginResponse) {
        this.code = code;
        this.message = message;
        this.token = loginResponse != null ? loginResponse.getToken() : null;
        this.phone = loginResponse != null ? loginResponse.getPhone() : null;
        this.loginResponse = loginResponse;
    }

    public static LoginResult success(String token, String phone) {
        return new LoginResult(ErrorCode.SUCCESS, "成功", token, phone);
    }

    public static LoginResult success(LoginResponse response) {
        return new LoginResult(ErrorCode.SUCCESS, "成功", response);
    }

    public static LoginResult error(String code, String message) {
        return new LoginResult(code, message, null, null);
    }

    public boolean isSuccess() {
        return ErrorCode.SUCCESS.equals(code);
    }

    public LoginResponse toLoginResponse() {
        if (this.loginResponse != null) {
            return this.loginResponse;
        }
        LoginResponse response = new LoginResponse();
        response.setToken(this.token);
        response.setPhone(this.phone);
        return response;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public String getPhone() { return phone; }
    public LoginResponse getLoginResponse() { return loginResponse; }
}