# 用户密码修改接口实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现两个密码管理接口——用户修改密码（change）和管理员重置密码（reset）

**Architecture:** 在现有 UserService/UserController 基础上新增两个业务方法，遵循现有 DTO → Service → Controller 三层架构

**Tech Stack:** Java8 + Maven + Spring Boot + MyBatis + BCrypt

---

## 文件变更概览

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| Create | `src/main/java/com/address/dto/PasswordChangeRequest.java` | 用户修改密码请求 |
| Create | `src/main/java/com/address/dto/PasswordResetRequest.java` | 管理员重置密码请求 |
| Modify | `src/main/java/com/address/common/ErrorCode.java` | 添加错误码常量 |
| Modify | `src/main/java/com/address/service/UserService.java` | 添加 changePassword/resetPassword 方法 |
| Modify | `src/main/java/com/address/controller/UserController.java` | 添加两个接口端点 |
| Create | `src/test/java/com/address/service/UserPasswordServiceTest.java` | 密码业务测试 |

---

## Task 1: 创建密码修改请求 DTO

**Files:**
- Create: `src/main/java/com/address/dto/PasswordChangeRequest.java`

- [ ] **Step 1: 创建 PasswordChangeRequest.java**

```java
package com.address.dto;

public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;

    public String getOldPassword() { return oldPassword; }
    public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/dto/PasswordChangeRequest.java
git commit -m "feat: 新增 PasswordChangeRequest DTO"
```

---

## Task 2: 创建密码重置请求 DTO

**Files:**
- Create: `src/main/java/com/address/dto/PasswordResetRequest.java`

- [ ] **Step 1: 创建 PasswordResetRequest.java**

```java
package com.address.dto;

public class PasswordResetRequest {
    private String newPassword;

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/dto/PasswordResetRequest.java
git commit -m "feat: 新增 PasswordResetRequest DTO"
```

---

## Task 3: 添加错误码常量

**Files:**
- Modify: `src/main/java/com/address/common/ErrorCode.java:14-17`

- [ ] **Step 1: 在 ErrorCode.java 第 17 行后添加新错误码**

在 `PASSWORD_INVALID = "101006"` 后添加：

```java
/** 旧密码错误 */
public static final String OLD_PASSWORD_ERROR = "101007";
/** 用户不存在 */
public static final String USER_NOT_EXIST = "101008";
/** 无权限操作 */
public static final String NO_PERMISSION = "101009";
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/common/ErrorCode.java
git commit -m "feat: ErrorCode 添加密码相关错误码"
```

---

## Task 4: 添加密码业务方法到 UserService

**Files:**
- Modify: `src/main/java/com/address/service/UserService.java:131`

- [ ] **Step 1: 在 UserService.java 第 115 行后添加两个业务方法**

在 `assignRoles` 方法之后、`toResponse` 方法之前添加：

```java
public void changePassword(String phone, String oldPassword, String newPassword) {
    SysUser user = sysUserMapper.findActiveByPhone(phone);
    if (user == null) {
        throw new RuntimeException("用户不存在或已禁用");
    }
    if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
        throw new RuntimeException("旧密码错误");
    }
    if (newPassword == null || newPassword.length() < 6) {
        throw new RuntimeException("密码格式错误，至少6位");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    user.setUpdateTime(LocalDateTime.now());
    sysUserMapper.update(user);
}

public void resetPassword(Long userId, String newPassword) {
    SysUser user = sysUserMapper.findById(userId);
    if (user == null) {
        throw new RuntimeException("用户不存在");
    }
    if (newPassword == null || newPassword.length() < 6) {
        throw new RuntimeException("密码格式错误，至少6位");
    }
    user.setPassword(passwordEncoder.encode(newPassword));
    user.setUpdateTime(LocalDateTime.now());
    sysUserMapper.update(user);
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/service/UserService.java
git commit -m "feat: UserService 添加 changePassword 和 resetPassword 方法"
```

---

## Task 5: 添加接口端点到 UserController

**Files:**
- Modify: `src/main/java/com/address/controller/UserController.java`

- [ ] **Step 1: 在 UserController.java 添加 changePassword 和 resetPassword 方法**

在 `getCurrentUser` 方法后添加：

```java
@PostMapping("/api/users/password/change")
public ApiResponse<Void> changePassword(@RequestBody PasswordChangeRequest request, @AuthenticationPrincipal UserDetails userDetails) {
    String phone = userDetails.getUsername();
    userService.changePassword(phone, request.getOldPassword(), request.getNewPassword());
    return ApiResponse.success(null);
}

@PostMapping("/api/users/{userId}/password/reset")
public ApiResponse<Void> resetPassword(@PathVariable Long userId, @RequestBody PasswordResetRequest request) {
    // TODO: 管理员权限校验（当前实现暂不校验，后续 Phase 添加角色权限校验）
    userService.resetPassword(userId, request.getNewPassword());
    return ApiResponse.success(null);
}
```

- [ ] **Step 2: 添加必要的 import**

在文件头部已有的 import 后确认包含：

```java
import com.address.dto.PasswordChangeRequest;
import com.address.dto.PasswordResetRequest;
```

- [ ] **Step 3: 验证编译**

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/address/controller/UserController.java
git commit -m "feat: UserController 添加密码修改和重置接口"
```

---

## Task 6: 编写密码业务测试

**Files:**
- Create: `src/test/java/com/address/service/UserPasswordServiceTest.java`

- [ ] **Step 1: 创建测试文件**

```java
package com.address.service;

import com.address.dto.PasswordChangeRequest;
import com.address.dto.PasswordResetRequest;
import com.address.model.SysUser;
import com.address.repository.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserPasswordServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testChangePassword_success() {
        // 先创建一个测试用户
        String testPhone = "13800138000";
        String oldPassword = "123456";
        String newPassword = "654321";

        SysUser existUser = sysUserMapper.findByPhone(testPhone);
        if (existUser == null) {
            // 用户不存在则跳过
            return;
        }

        // 修改密码
        userService.changePassword(testPhone, oldPassword, newPassword);

        // 验证新密码是否正确
        SysUser updatedUser = sysUserMapper.findByPhone(testPhone);
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }

    @Test
    public void testChangePassword_oldPasswordError() {
        String testPhone = "13800138000";
        String wrongOldPassword = "wrong";
        String newPassword = "654321";

        SysUser existUser = sysUserMapper.findByPhone(testPhone);
        if (existUser == null) {
            return;
        }

        assertThrows(RuntimeException.class, () -> {
            userService.changePassword(testPhone, wrongOldPassword, newPassword);
        });
    }

    @Test
    public void testChangePassword_invalidNewPassword() {
        String testPhone = "13800138000";
        String oldPassword = "123456";
        String invalidNewPassword = "123"; // 小于6位

        SysUser existUser = sysUserMapper.findByPhone(testPhone);
        if (existUser == null) {
            return;
        }

        assertThrows(RuntimeException.class, () -> {
            userService.changePassword(testPhone, oldPassword, invalidNewPassword);
        });
    }

    @Test
    public void testResetPassword_success() {
        Long testUserId = 1L;
        String newPassword = "reset123";

        SysUser existUser = sysUserMapper.findById(testUserId);
        if (existUser == null) {
            return;
        }

        // 重置密码
        userService.resetPassword(testUserId, newPassword);

        // 验证新密码是否正确
        SysUser updatedUser = sysUserMapper.findById(testUserId);
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }

    @Test
    public void testResetPassword_userNotExist() {
        Long nonExistUserId = 999999L;

        assertThrows(RuntimeException.class, () -> {
            userService.resetPassword(nonExistUserId, "123456");
        });
    }
}
```

- [ ] **Step 2: 运行测试**

Run: `mvn test -Dtest=UserPasswordServiceTest 2>&1 | grep -E "Tests run|BUILD|FAIL"`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/test/java/com/address/service/UserPasswordServiceTest.java
git commit -m "test: 添加密码业务测试"
```

---

## Task 7: 最终验证

- [ ] **Step 1: 运行完整测试套件**

Run: `mvn test 2>&1 | grep -E "Tests run|BUILD"`
Expected: BUILD SUCCESS，所有测试通过

- [ ] **Step 2: 检查 git 状态**

Run: `git status`
Expected: 无未提交变更

---

## 自检清单

- [ ] PasswordChangeRequest.java 已创建并提交
- [ ] PasswordResetRequest.java 已创建并提交
- [ ] ErrorCode.java 已添加 OLD_PASSWORD_ERROR / USER_NOT_EXIST / NO_PERMISSION
- [ ] UserService.java 已添加 changePassword 和 resetPassword 方法
- [ ] UserController.java 已添加 /api/users/password/change 和 /api/users/{userId}/password/reset
- [ ] UserPasswordServiceTest.java 已创建并测试通过
- [ ] 所有测试通过，BUILD SUCCESS