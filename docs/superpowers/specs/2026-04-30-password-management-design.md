# 用户密码修改接口设计

## 背景

为系统添加两个密码管理接口，分别支持用户自改密码和管理员重置密码。

## 接口设计

### 接口一：用户修改密码（自改）

**路径：** `POST /api/users/password/change`

**认证：** 需要 JWT token（当前登录用户）

**请求体：**
```json
{
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

**业务流程：**
1. 从 JWT token 解析当前用户手机号
2. 根据手机号查询用户，验证旧密码是否正确（BCrypt matches）
3. 新密码格式验证：≥6 位
4. 用 BCrypt 加密新密码，更新到数据库

**响应：**
- 成功：`{ "code": 200, "message": "密码修改成功" }`
- 旧密码错误：`{ "code": xxx, "message": "旧密码错误" }`
- 新密码格式错误：`{ "code": xxx, "message": "密码格式错误，至少6位" }`

---

### 接口二：管理员重置密码

**路径：** `POST /api/users/{userId}/password/reset`

**认证：** 需要 JWT token，且用户有 admin 角色

**请求体：**
```json
{
  "newPassword": "新密码"
}
```

**业务流程：**
1. 验证当前用户有 admin 角色权限
2. 根据 userId 查询目标用户是否存在
3. 新密码格式验证：≥6 位
4. 用 BCrypt 加密新密码，直接更新

**响应：**
- 成功：`{ "code": 200, "message": "密码重置成功" }`
- 无权限：`{ "code": xxx, "message": "无权限操作" }`
- 用户不存在：`{ "code": xxx, "message": "用户不存在" }`

---

## 共用规则

| 项目 | 规则 |
|------|------|
| 密码格式 | 至少 6 位 |
| 加密方式 | BCrypt |
| 响应内容 | 只返回成功/失败状态，不返回明文密码 |

---

## 实现计划

### 文件变更

1. **新增 DTO**
   - `src/main/java/com/address/dto/PasswordChangeRequest.java` - 用户修改密码请求
   - `src/main/java/com/address/dto/PasswordResetRequest.java` - 管理员重置密码请求

2. **新增 Service 方法**
   - `UserService.changePassword(String phone, String oldPassword, String newPassword)` - 用户自改
   - `UserService.resetPassword(Long userId, String newPassword)` - 管理员重置

3. **新增 Controller 方法**
   - `UserController.changePassword()` - 处理 `/api/users/password/change`
   - `UserController.resetPassword()` - 处理 `/api/users/{userId}/password/reset`

### 验证方式

- 单元测试：Service 层逻辑验证
- 集成测试：Controller 层完整流程测试