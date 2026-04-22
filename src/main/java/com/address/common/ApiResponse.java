package com.address.common;

public class ApiResponse<T> {
    private String code;
    private String message;
    private T data;

    public ApiResponse() {}

    public ApiResponse(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("200", "成功", data);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}