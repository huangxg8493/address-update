# 登录接口返回码归类实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为 `/api/auth/login` 接口建立结构化错误码体系，区分用户未注册、用户已禁用、密码错误三种场景

**Architecture:** 通过 LoginResult 封装认证结果（不再抛异常），Controller 根据结果构造 ApiResponse 返回对应错误码

**Tech Stack:** Java8 + Spring Boot + MyBatis

---

## 文件结构

| 操作 | 文件路径 | 职责 |
|-----|---------|------|
| 新建 | `src/main/java/com/address/common/AuthErrorCode.java` | 认证模块错误码常量 |
| 新建 | `src/main/java/com/address/dto/LoginResult.java` | 认证结果封装 |
| 修改 | `src/main/java/com/address/service/AuthService.java` | login() 返回 LoginResult |
| 修改 | `src/main/java/com/address/common/ApiResponse.java` | 添加 success(data, code) 方法 |
| 修改 | `src/main/java/com/address/controller/AuthController.java` | 调用 AuthService.login() 并构造响应 |
| 已有 | `src/main/java/com/address/repository/SysUserMapper.java` | 无需修改（已有 findByPhone 和 findActiveByPhone） |

---

## Task 1: 创建 AuthErrorCode 错误码常量类

**Files:**
- Create: `src/main/java/com/address/common/AuthErrorCode.java`

- [ ] **Step 1: 创建文件**

```java
package com.address.common;

public class AuthErrorCode {
    public static final String SUCCESS = "000000";
    public static final String USER_NOT_FOUND = "101001";
    public static final String USER_DISABLED = "101002";
    public static final String PASSWORD_ERROR = "101003";
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/common/AuthErrorCode.java
git commit -m "feat: 添加认证模块错误码常量类 AuthErrorCode"
```

---

## Task 2: 创建 LoginResult 认证结果封装类

**Files:**
- Create: `src/main/java/com/address/dto/LoginResult.java`

- [ ] **Step 1: 创建文件**

```java
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
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/dto/LoginResult.java
git commit -m "feat: 添加 LoginResult 认证结果封装类"
```

---

## Task 3: 修改 ApiResponse 支持自定义成功码

**Files:**
- Modify: `src/main/java/com/address/common/ApiResponse.java:16-22`

- [ ] **Step 1: 添加 success(data, code) 方法**

在 ApiResponse.java 第 18 行 `success(T data)` 方法后添加：

```java
public static <T> ApiResponse<T> success(T data, String code) {
    return new ApiResponse<>(code, "成功", data);
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/common/ApiResponse.java
git commit -m "feat: ApiResponse 添加支持自定义成功码的 success(data, code) 方法"
```

---

## Task 4: 修改 AuthService.login() 返回 LoginResult

**Files:**
- Modify: `src/main/java/com/address/service/AuthService.java:28-41`

- [ ] **Step 1: 修改 login 方法返回类型和实现**

将 `AuthService.java` 第 28-41 行的 login 方法：

```java
public LoginResponse login(LoginRequest request) {
    SysUser user = sysUserMapper.findActiveByPhone(request.getPhone());
    if (user == null) {
        throw new RuntimeException("用户不存在或已禁用");
    }
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("密码错误");
    }
    String token = jwtUtil.generateToken(user.getUserId(), user.getPhone());
    LoginResponse response = new LoginResponse();
    response.setToken(token);
    response.setPhone(user.getPhone());
    return response;
}
```

替换为：

```java
public LoginResult login(LoginRequest request) {
    SysUser user = sysUserMapper.findActiveByPhone(request.getPhone());
    if (user == null) {
        SysUser existUser = sysUserMapper.findByPhone(request.getPhone());
        if (existUser == null) {
            return LoginResult.error(AuthErrorCode.USER_NOT_FOUND, "用户未注册");
        } else {
            return LoginResult.error(AuthErrorCode.USER_DISABLED, "用户已禁用");
        }
    }
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        return LoginResult.error(AuthErrorCode.PASSWORD_ERROR, "密码错误");
    }
    String token = jwtUtil.generateToken(user.getUserId(), user.getPhone());
    return LoginResult.success(token, user.getPhone());
}
```

- [ ] **Step 2: 添加必要的 import**

文件头部添加：
```java
import com.address.common.AuthErrorCode;
import com.address.dto.LoginResult;
```

- [ ] **Step 3: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/address/service/AuthService.java
git commit -m "feat: AuthService.login() 改为返回 LoginResult，支持细粒度错误码"
```

---

## Task 5: 修改 AuthController.login() 处理 LoginResult

**Files:**
- Modify: `src/main/java/com/address/controller/AuthController.java:23-26`

- [ ] **Step 1: 修改 login 方法**

将 `AuthController.java` 第 23-26 行：

```java
@PostMapping("/api/auth/login")
public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
    return ApiResponse.success(authService.login(request));
}
```

替换为：

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

- [ ] **Step 2: 添加必要的 import**

文件头部添加：
```java
import com.address.dto.LoginResult;
```

- [ ] **Step 3: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/address/controller/AuthController.java
git commit -m "feat: AuthController.login() 根据 LoginResult 返回对应错误码"
```

---

## Task 6: 运行测试验证

**Files:**
- Test: `src/test/java/com/address/service/AuthServiceTest.java`

- [ ] **Step 1: 运行 AuthServiceTest**

Run: `mvn test -Dtest=AuthServiceTest`
Expected: 所有测试通过

- [ ] **Step 2: 运行完整测试套件**

Run: `mvn test 2>&1 | grep -E "Tests run|BUILD"`
Expected: BUILD SUCCESS，无测试失败

- [ ] **Step 3: 提交测试相关修改（如有）**

```bash
git add .
git commit -m "test: 验证登录错误码功能"
```

---

## 自检清单

- [ ] 所有错误码（101001、101002、101003）和成功码（000000）与设计一致
- [ ] LoginResult 正确实现 isSuccess()、toLoginResponse() 方法
- [ ] ApiResponse.success(data, code) 可正确调用
- [ ] AuthService.login() 不再抛出 RuntimeException
- [ ] SysUserMapper 无需修改（已有 findByPhone 和 findActiveByPhone）
- [ ] 测试覆盖用户未注册、用户已禁用、密码错误、登录成功四种场景
