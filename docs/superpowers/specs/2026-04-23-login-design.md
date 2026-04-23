# 手机号登录功能设计

## 1. 概述

为客户地址管理系统增加基于手机号+密码的登录认证功能，支持角色+权限控制+数据范围配置。

## 2. 技术方案

### 认证方式
- **JWT 无状态认证**
- 登录成功后返回 Token（JWT），后续请求通过 Token 解析获取身份
- Token 签名密钥存储在配置中

### 密码存储
- BCrypt 加密存储

### ID 生成
- 复用项目已有的 `SnowflakeIdGenerator`（雪花算法）

## 3. 数据模型

### 3.1 sys_user（用户表）
| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | bigint | 主键，雪花算法 |
| phone | varchar(20) | 手机号，唯一索引 |
| password | varchar(255) | BCrypt 加密 |
| status | char(1) | Y-启用，N-禁用 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

### 3.2 sys_role（角色表）
| 字段 | 类型 | 说明 |
|------|------|------|
| role_id | bigint | 主键，雪花算法 |
| role_code | varchar(50) | 角色代码，唯一索引 |
| role_name | varchar(100) | 角色名称 |
| status | char(1) | Y-启用，N-禁用 |
| create_time | datetime | 创建时间 |

### 3.3 sys_user_role（用户角色关联）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户ID |
| role_id | bigint | 角色ID |

### 3.4 sys_permission（权限表）
| 字段 | 类型 | 说明 |
|------|------|------|
| permission_id | bigint | 主键，雪花算法 |
| permission_code | varchar(100) | 权限代码，唯一索引 |
| permission_name | varchar(100) | 权限名称 |
| menu_url | varchar(255) | 菜单URL |
| create_time | datetime | 创建时间 |

### 3.5 sys_role_permission（角色权限关联）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| role_id | bigint | 角色ID |
| permission_id | bigint | 权限ID |

### 3.6 sys_data_scope（数据范围表）
| 字段 | 类型 | 说明 |
|------|------|------|
| scope_id | bigint | 主键，雪花算法 |
| scope_code | varchar(50) | 范围代码 |
| scope_name | varchar(100) | 范围名称 |
| scope_type | varchar(20) | OWN/DEPT/ALL |
| create_time | datetime | 创建时间 |

### 3.7 sys_role_data_scope（角色数据范围关联）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| role_id | bigint | 角色ID |
| scope_id | bigint | 数据范围ID |

### 3.8 数据范围类型（scope_type）
- `OWN`: 只能访问自己的数据
- `DEPT`: 部门数据
- `ALL`: 全部数据

## 4. 接口设计

### 4.1 认证模块
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/auth/register | POST | 用户注册（手机号+密码） |
| /api/auth/login | POST | 用户登录（手机号+密码） |
| /api/auth/logout | POST | 登出 |

### 4.2 用户管理（管理员）
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/users/query | POST | 分页查询用户列表 |
| /api/users/create | POST | 创建用户 |
| /api/users/update | POST | 修改用户 |
| /api/users/delete | POST | 删除用户 |
| /api/users/{userId}/roles/assign | POST | 分配用户角色 |

### 4.3 角色管理（管理员）
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/roles/query | POST | 查询角色列表 |
| /api/roles/create | POST | 创建角色 |
| /api/roles/update | POST | 修改角色 |
| /api/roles/delete | POST | 删除角色 |
| /api/roles/{roleId}/permissions/assign | POST | 分配角色权限 |
| /api/roles/{roleId}/dataScopes/assign | POST | 分配角色数据范围 |

### 4.4 权限管理（管理员）
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/permissions/query | POST | 查询权限列表 |
| /api/permissions/create | POST | 创建权限 |
| /api/permissions/update | POST | 修改权限 |
| /api/permissions/delete | POST | 删除权限 |

### 4.5 数据范围管理（管理员）
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/dataScopes/query | POST | 查询数据范围列表 |
| /api/dataScopes/create | POST | 创建数据范围 |
| /api/dataScopes/update | POST | 修改数据范围 |
| /api/dataScopes/delete | POST | 删除数据范围 |

### 4.6 用户信息
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/users/me/get | POST | 获取当前用户信息（含角色、权限、数据范围） |

## 5. 统一响应格式

复用现有 `ApiResponse`：
```json
{
    "code": "200",
    "message": "成功",
    "data": { ... }
}
```

## 6. 安全设计

- 密码 BCrypt 加密存储
- JWT Token 签名验证
- 敏感接口需携带有效 Token 访问
