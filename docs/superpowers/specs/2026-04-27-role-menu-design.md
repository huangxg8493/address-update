# 角色菜单关联表设计

## 1. 概述

新增 `sys_role_menu` 表实现角色与菜单的关联，支持批量分配和查询。

## 2. 数据模型

### 2.1 表结构

```sql
CREATE TABLE sys_role_menu (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_id     BIGINT       NOT NULL COMMENT '角色ID',
    menu_id     BIGINT       NOT NULL COMMENT '菜单ID',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_role_menu (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色菜单关联表';
```

### 2.2 实体类

**文件**：`src/main/java/com/address/model/SysRoleMenu.java`

```java
package com.address.model;

import java.time.LocalDateTime;

/**
 * 角色菜单关联实体
 */
public class SysRoleMenu {
    private Long id;
    private Long roleId;
    private Long menuId;
    private LocalDateTime createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public Long getMenuId() { return menuId; }
    public void setMenuId(Long menuId) { this.menuId = menuId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
```

## 3. 接口设计

### 3.1 批量分配菜单

**请求**：`POST /api/roles/{roleId}/menus`

```json
{
    "menuIds": [1, 2, 3]
}
```

**响应**：成功返回 `000000`

### 3.2 查询角色菜单

**请求**：`GET /api/roles/{roleId}/menus`

**响应**：

```json
{
    "code": "000000",
    "message": "成功",
    "data": [
        {"menuId": 1, "menuName": "用户管理", "menuUrl": "/users", ...},
        {"menuId": 2, "menuName": "角色管理", "menuUrl": "/roles", ...}
    ]
}
```

## 4. 实现文件

| 操作 | 文件路径 |
|-----|---------|
| 新建 | `src/main/java/com/address/model/SysRoleMenu.java` |
| 新建 | `src/main/java/com/address/repository/SysRoleMenuMapper.java` |
| 新建 | `src/main/java/com/address/service/RoleMenuService.java` |
| 修改 | `src/main/java/com/address/controller/RoleController.java` |
| 新建 | `src/test/java/com/address/service/RoleMenuServiceTest.java` |

## 5. 核心逻辑

1. **批量分配**：先删后插（deleteByRoleId → insertBatch）
2. **查询**：selectByRoleId + 关联 SysMenu 查询完整信息
3. **防重复**：uk_role_menu 唯一索引保证不重复插入