# 登录接口返回码归类设计

## 1. 背景

当前 `/api/auth/login` 接口通过抛出 RuntimeException 字符串返回错误信息，无法精确区分错误类型（如"用户不存在或已禁用"合并为一个信息）。需要建立结构化的错误码体系。

## 2. 错误码规划

### 2.1 认证模块错误码定义

| 错误场景 | 错误码 | 说明 |
|---------|--------|------|
| 成功 | `000000` | 登录成功 |
| 用户未注册 | `101001` | 手机号未在系统注册 |
| 用户已禁用 | `101002` | 用户存在但 status='N' |
| 密码错误 | `101003` | 密码不匹配 |

### 2.2 错误码结构

- **成功码**：`000000`
- **认证模块码段**：101xxx（1 = 认证模块，01 = 登录子类型）
- 采用6位数字字符串，便于客户端映射和日志检索

## 3. 实现方案

### 3.1 新建错误码常量类

**文件**：`src/main/java/com/address/common/AuthErrorCode.java`

```java
package com.address.common;

public class AuthErrorCode {
    public static final String SUCCESS = "000000";
    public static final String USER_NOT_FOUND = "101001";
    public static final String USER_DISABLED = "101002";
    public static final String PASSWORD_ERROR = "101003";
}
```

### 3.2 新建 LoginResult 封装类

**文件**：`src/main/java/com/address/dto/LoginResult.java`

```java
package com.address.dto;

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

    // getters...
}
```

### 3.3 修改 AuthService.login()

**文件**：`src/main/java/com/address/service/AuthService.java`

```java
public LoginResult login(LoginRequest request) {
    // 1. 查询用户（检查是否存在且未禁用）
    SysUser user = sysUserMapper.findActiveByPhone(request.getPhone());
    if (user == null) {
        // 2. 用户不存在或已禁用，需要分情况判断
        SysUser existUser = sysUserMapper.findByPhone(request.getPhone());
        if (existUser == null) {
            return LoginResult.error(AuthErrorCode.USER_NOT_FOUND, "用户未注册");
        } else {
            return LoginResult.error(AuthErrorCode.USER_DISABLED, "用户已禁用");
        }
    }
    // 3. 验证密码
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        return LoginResult.error(AuthErrorCode.PASSWORD_ERROR, "密码错误");
    }
    // 4. 生成Token
    String token = jwtUtil.generateToken(user.getUserId(), user.getPhone());
    return LoginResult.success(token, user.getPhone());
}
```

修改点说明：
- `findActiveByPhone` 查询 `status='Y'` 的用户
- `findByPhone` 查询任意状态用户（用于区分未注册/已禁用）
- 不再抛出 RuntimeException，改为返回 LoginResult

### 3.4 修改 ApiResponse

**文件**：`src/main/java/com/address/common/ApiResponse.java`

添加支持自定义成功码的静态方法：

```java
public static <T> ApiResponse<T> success(T data, String code) {
    return new ApiResponse<>(code, "成功", data);
}
```

### 3.5 修改 AuthController.login()

**文件**：`src/main/java/com/address/controller/AuthController.java`

```java
@PostMapping("/api/auth/login")
public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
    LoginResult result = authService.login(request);
    if (result.isSuccess()) {
        return ApiResponse.success(result.toLoginResponse(), result.getCode());
    }
    return ApiResponse.error(result.getCode(), result.getMessage());
}
```

### 3.6 SysUserMapper 增加方法

**文件**：`src/main/java/com/address/repository/SysUserMapper.java`

```java
SysUser findByPhone(String phone);  // 查询任意状态用户
```

**对应 SQL**（如使用 XML）：
```xml
<select id="findByPhone" resultType="SysUser">
    SELECT * FROM sys_user WHERE phone = #{phone}
</select>
```

## 4. 涉及文件清单

| 操作 | 文件路径 |
|-----|---------|
| 新建 | `src/main/java/com/address/common/AuthErrorCode.java` |
| 新建 | `src/main/java/com/address/dto/LoginResult.java` |
| 修改 | `src/main/java/com/address/service/AuthService.java` |
| 修改 | `src/main/java/com/address/common/ApiResponse.java` |
| 修改 | `src/main/java/com/address/controller/AuthController.java` |
| 修改 | `src/main/java/com/address/repository/SysUserMapper.java` |

## 5. 测试验证

- 用户未注册 → 返回 code=101001, message="用户未注册"
- 用户已禁用 → 返回 code=101002, message="用户已禁用"
- 密码错误 → 返回 code=101003, message="密码错误"
- 登录成功 → 返回 code=000000, data={token, phone}
