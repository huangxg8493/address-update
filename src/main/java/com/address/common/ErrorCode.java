package com.address.common;

public enum ErrorCode {
    SUCCESS("200", "成功"),
    BAD_REQUEST("400", "参数错误"),
    NOT_FOUND("404", "客户不存在"),
    CONFLICT("409", "地址冲突"),
    INTERNAL_ERROR("500", "服务器异常");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}