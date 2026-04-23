# SysUser 字段扩展设计

## 1. 需求概述

为 sys_user 表扩展用户详细信息字段，支持用户注册时填写更多个人信息。

## 2. 字段变更

### 新增字段

| 字段 | 类型 | 说明 | 约束 |
|------|------|------|------|
| user_name | VARCHAR(100) | 用户名称 | 可选 |
| email | VARCHAR(100) | 邮箱 | 可选，邮箱格式 |
| province | VARCHAR(50) | 省 | 可选 |
| city | VARCHAR(50) | 市 | 可选 |
| district | VARCHAR(50) | 区 | 可选 |
| hobby | VARCHAR(500) | 业余爱好 | 可选，单字符串存储 |

## 3. 数据模型

### 实体类 SysUser 变更

```java
public class SysUser {
    private Long userId;
    private String phone;
    private String password;
    private String status;
    private String userName;      // 新增
    private String email;        // 新增
    private String province;     // 新增
    private String city;         // 新增
    private String district;     // 新增
    private String hobby;        // 新增
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

## 4. 涉及文件

| 文件 | 变更 |
|------|------|
| SysUser.java | 新增字段及 getter/setter |
| SysUserMapper.java | 新增字段映射 |
| UserCreateRequest.java | 新增字段 |
| UserUpdateRequest.java | 新增字段 |
| UserResponse.java | 新增字段 |
| sql/sys_user.sql | 更新建表语句 |

## 5. 接口影响

| 接口 | 影响 |
|------|------|
| /api/users/create | 支持填写新增字段 |
| /api/users/update | 支持修改新增字段 |
| /api/users/query | 返回完整用户信息 |

## 6. 约束

- hobby 字段为单字符串，直接存储用户输入文本
- 所有新增字段均为可选
- 不影响现有接口契约
