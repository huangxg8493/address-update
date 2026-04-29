# Findings

> 研究与发现记录

---

## 2026-04-30 密码管理接口设计

### 背景
为系统添加两个密码管理接口：
1. **用户修改密码** - 用户自改，需要验证旧密码
2. **管理员重置密码** - 管理员帮用户重置，不验证旧密码

### 现有项目结构

#### 相关文件
- `src/main/java/com/address/model/SysUser.java` - 用户实体，密码字段为 BCrypt 加密
- `src/main/java/com/address/service/AuthService.java` - 认证服务，已有 login/register
- `src/main/java/com/address/service/UserService.java` - 用户服务，已有 query/create/update/delete/assignRoles
- `src/main/java/com/address/controller/UserController.java` - 用户控制器，6 个接口
- `src/main/java/com/address/common/ErrorCode.java` - 错误码，已有序号 101001-101006

#### 技术栈
- Spring Boot 2.7.18
- MyBatis 3.5.13
- JWT 无状态认证
- BCrypt 密码加密
- Java8 + Maven

### 设计决策

#### 接口设计

| 接口 | 路径 | 方法 | 说明 |
|------|------|------|------|
| 用户修改密码 | POST /api/users/password/change | 需认证 | userId 从 JWT 解析，需旧密码验证 |
| 管理员重置密码 | POST /api/users/{userId}/password/reset | 需认证+admin | 管理员重置，无需旧密码 |

#### 请求/响应设计

**用户修改密码请求：**
```json
{
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

**管理员重置密码请求：**
```json
{
  "newPassword": "新密码"
}
```

**响应（两接口一致）：**
```json
{
  "code": 200,
  "message": "密码修改成功/重置成功",
  "data": null
}
```

#### 密码规则
- 长度至少 6 位
- BCrypt 加密存储
- 只返回成功/失败状态，不返回明文

#### 错误码设计

| 错误码 | 说明 |
|--------|------|
| 101007 | 旧密码错误 |
| 101008 | 用户不存在 |
| 101009 | 无权限操作 |

### 规格确认
1. 新密码格式要求：维持现状 ≥6 位
2. 响应内容：只返回成功/失败状态，不返回明文密码
3. 操作日志：暂时不需要

---

## 历史记录

### Phase 22: sys_user 省市区合并设计

### Phase 21: 登录接口返回完整信息设计

### Phase 20: 角色菜单关联设计

### Phase 19: 登录接口返回码归类设计

### Phase 18: SysUser 字段扩展设计

### Phase 17: 菜单管理模块设计

### Phase 16: 手机号登录功能设计

### Phase 15: 单地址维护接口设计

### Phase 14: UI 页面实现

### Phase 13: 地址查询接口实现

### Phase 12: RESTful 接口实现

### Spring Boot 集成实现

### MyBatis 迁移实现

### HikariCP 连接池实现

### MySQL 持久层实现

### seqNo 雪花算法实现

### 重构完成

### 架构决策

### 需求理解