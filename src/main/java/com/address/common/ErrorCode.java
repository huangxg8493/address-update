package com.address.common;

public class ErrorCode {
    /** 成功 */
    public static final String SUCCESS = "000000";
    /** 用户未注册 */
    public static final String USER_NOT_FOUND = "101001";
    /** 用户已禁用 */
    public static final String USER_DISABLED = "101002";
    /** 密码错误 */
    public static final String PASSWORD_ERROR = "101003";
    /** 手机号已注册 */
    public static final String PHONE_ALREADY_EXISTS = "101004";
    /** 手机号格式错误 */
    public static final String PHONE_FORMAT_ERROR = "101005";
    /** 密码格式错误 */
    public static final String PASSWORD_INVALID = "101006";

    /** 参数错误 */
    public static final String BAD_REQUEST = "400";

    private final String code;
    private final String message;

    public ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
}
