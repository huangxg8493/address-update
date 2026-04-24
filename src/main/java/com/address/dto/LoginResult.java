package com.address.dto;

import com.address.common.AuthErrorCode;

public class LoginResult {
    private String code;
    private String message;
    private String token;
    private String phone;

    private LoginResult(String code, String message, String token, String phone) {
        this.code = code;
        this.message = message;
        this.token = token;
        this.phone = phone;
    }

    public static LoginResult success(String token, String phone) {
        return new LoginResult(AuthErrorCode.SUCCESS, "成功", token, phone);
    }

    public static LoginResult error(String code, String message) {
        return new LoginResult(code, message, null, null);
    }

    public boolean isSuccess() {
        return AuthErrorCode.SUCCESS.equals(code);
    }

    public LoginResponse toLoginResponse() {
        LoginResponse response = new LoginResponse();
        response.setToken(this.token);
        response.setPhone(this.phone);
        return response;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public String getPhone() { return phone; }
}
