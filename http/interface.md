# 接口文档

## 认证接口

### 1. 用户注册
- **接口 URL**: `/api/auth/register`
- **请求方法**: POST
- **描述**: 用户注册
- **请求报文**:
```json
{
  "phone": "string",
  "password": "string"
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```

---

### 2. 用户登录
- **接口 URL**: `/api/auth/login`
- **请求方法**: POST
- **描述**: 用户登录
- **请求报文**:
```json
{
  "phone": "string",
  "password": "string"
}
```
- **响应报文（成功）**:
```json
{
  "code": "000000",
  "message": "成功",
  "data": {
    "token": "string",
    "phone": "string"
  }
}
```
- **响应报文（用户未注册）**:
```json
{
  "code": "101001",
  "message": "用户未注册",
  "data": null
}
```
- **响应报文（用户已禁用）**:
```json
{
  "code": "101002",
  "message": "用户已禁用",
  "data": null
}
```
- **响应报文（密码错误）**:
```json
{
  "code": "101003",
  "message": "密码错误",
  "data": null
}
```

---

### 3. 用户登出
- **接口 URL**: `/api/auth/logout`
- **请求方法**: POST
- **描述**: 用户登出
- **请求报文**: 无
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```

---

## 用户接口

### 4. 查询用户列表
- **接口 URL**: `/api/users/query`
- **请求方法**: POST
- **描述**: 查询用户列表（分页）
- **请求报文**:
```json
{
  "phone": "string",
  "status": "string",
  "pageNum": 1,
  "pageSize": 10
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": [
    {
      "userId": 0,
      "phone": "string",
      "status": "string",
      "createTime": "2026-04-23T00:00:00",
      "updateTime": "2026-04-23T00:00:00"
    }
  ]
}
```

---

### 5. 创建用户
- **接口 URL**: `/api/users/create`
- **请求方法**: POST
- **描述**: 创建用户
- **请求报文**:
```json
{
  "phone": "string",
  "password": "string",
  "status": "string"
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "userId": 0,
    "phone": "string",
    "status": "string",
    "createTime": "2026-04-23T00:00:00",
    "updateTime": "2026-04-23T00:00:00"
  }
}
```

---

### 6. 更新用户
- **接口 URL**: `/api/users/update`
- **请求方法**: POST
- **描述**: 更新用户信息
- **请求报文**:
```json
{
  "userId": 0,
  "phone": "string",
  "password": "string",
  "status": "string"
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "userId": 0,
    "phone": "string",
    "status": "string",
    "createTime": "2026-04-23T00:00:00",
    "updateTime": "2026-04-23T00:00:00"
  }
}
```

---

### 7. 删除用户
- **接口 URL**: `/api/users/delete`
- **请求方法**: POST
- **描述**: 删除用户
- **请求参数**: `userId` (Long)
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```

---

### 8. 分配用户角色
- **接口 URL**: `/api/users/{userId}/roles/assign`
- **请求方法**: POST
- **描述**: 为用户分配角色
- **路径参数**: `userId` (Long)
- **请求报文**:
```json
[1, 2, 3]
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```

---

### 9. 获取当前用户信息
- **接口 URL**: `/api/users/me/get`
- **请求方法**: POST
- **描述**: 获取当前登录用户信息
- **请求报文**: 无
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```

---

## 角色接口

### 10. 查询角色列表
- **接口 URL**: `/api/roles/query`
- **请求方法**: POST
- **描述**: 查询角色列表
- **请求报文**:
```json
{
  "roleCode": "string",
  "roleName": "string",
  "status": "string"
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": [
    {
      "roleId": 0,
      "roleCode": "string",
      "roleName": "string",
      "status": "string",
      "createTime": "2026-04-23T00:00:00"
    }
  ]
}
```

---

### 11. 创建角色
- **接口 URL**: `/api/roles/create`
- **请求方法**: POST
- **描述**: 创建角色
- **请求报文**:
```json
{
  "roleCode": "string",
  "roleName": "string",
  "status": "string"
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "roleId": 0,
    "roleCode": "string",
    "roleName": "string",
    "status": "string",
    "createTime": "2026-04-23T00:00:00"
  }
}
```

---

### 12. 更新角色
- **接口 URL**: `/api/roles/update`
- **请求方法**: POST
- **描述**: 更新角色信息
- **请求报文**:
```json
{
  "roleId": 0,
  "roleCode": "string",
  "roleName": "string",
  "status": "string"
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "roleId": 0,
    "roleCode": "string",
    "roleName": "string",
    "status": "string",
    "createTime": "2026-04-23T00:00:00"
  }
}
```

---

### 13. 删除角色
- **接口 URL**: `/api/roles/delete`
- **请求方法**: POST
- **描述**: 删除角色
- **请求参数**: `roleId` (Long)
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```

---

### 14. 分配角色权限
- **接口 URL**: `/api/roles/{roleId}/permissions/assign`
- **请求方法**: POST
- **描述**: 为角色分配权限
- **路径参数**: `roleId` (Long)
- **请求报文**:
```json
[1, 2, 3]
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```

---

### 15. 分配角色数据范围
- **接口 URL**: `/api/roles/{roleId}/dataScopes/assign`
- **请求方法**: POST
- **描述**: 为角色分配数据范围
- **路径参数**: `roleId` (Long)
- **请求报文**:
```json
[1, 2, 3]
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```

---

## 权限接口

### 16. 查询权限列表
- **接口 URL**: `/api/permissions/query`
- **请求方法**: POST
- **描述**: 查询权限列表
- **请求报文**: 无
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": [
    {
      "permissionId": 0,
      "permissionCode": "string",
      "permissionName": "string",
      "menuUrl": "string",
      "createTime": "2026-04-23T00:00:00"
    }
  ]
}
```

---

### 17. 创建权限
- **接口 URL**: `/api/permissions/create`
- **请求方法**: POST
- **描述**: 创建权限
- **请求报文**:
```json
{
  "permissionCode": "string",
  "permissionName": "string",
  "menuUrl": "string"
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "permissionId": 0,
    "permissionCode": "string",
    "permissionName": "string",
    "menuUrl": "string",
    "createTime": "2026-04-23T00:00:00"
  }
}
```

---

### 18. 更新权限
- **接口 URL**: `/api/permissions/update`
- **请求方法**: POST
- **描述**: 更新权限信息
- **请求报文**:
```json
{
  "permissionId": 0,
  "permissionCode": "string",
  "permissionName": "string",
  "menuUrl": "string"
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "permissionId": 0,
    "permissionCode": "string",
    "permissionName": "string",
    "menuUrl": "string",
    "createTime": "2026-04-23T00:00:00"
  }
}
```

---

### 19. 删除权限
- **接口 URL**: `/api/permissions/delete`
- **请求方法**: POST
- **描述**: 删除权限
- **请求参数**: `permissionId` (Long)
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```

---

## 数据范围接口

### 20. 查询数据范围列表
- **接口 URL**: `/api/dataScopes/query`
- **请求方法**: POST
- **描述**: 查询数据范围列表
- **请求报文**: 无
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": [
    {
      "scopeId": 0,
      "scopeCode": "string",
      "scopeName": "string",
      "scopeType": "string",
      "createTime": "2026-04-23T00:00:00"
    }
  ]
}
```

---

### 21. 创建数据范围
- **接口 URL**: `/api/dataScopes/create`
- **请求方法**: POST
- **描述**: 创建数据范围
- **请求报文**:
```json
{
  "scopeCode": "string",
  "scopeName": "string",
  "scopeType": "string"
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "scopeId": 0,
    "scopeCode": "string",
    "scopeName": "string",
    "scopeType": "string",
    "createTime": "2026-04-23T00:00:00"
  }
}
```

---

### 22. 更新数据范围
- **接口 URL**: `/api/dataScopes/update`
- **请求方法**: POST
- **描述**: 更新数据范围信息
- **请求报文**:
```json
{
  "scopeId": 0,
  "scopeCode": "string",
  "scopeName": "string",
  "scopeType": "string"
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "scopeId": 0,
    "scopeCode": "string",
    "scopeName": "string",
    "scopeType": "string",
    "createTime": "2026-04-23T00:00:00"
  }
}
```

---

### 23. 删除数据范围
- **接口 URL**: `/api/dataScopes/delete`
- **请求方法**: POST
- **描述**: 删除数据范围
- **请求参数**: `scopeId` (Long)
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```

---

## 客户地址接口

### 24. 批量更新客户地址
- **接口 URL**: `/api/client/address/update`
- **请求方法**: POST
- **描述**: 批量新增或更新客户地址
- **请求报文**:
```json
{
  "clientNo": "string",
  "addresses": [
    {
      "seqNo": "string",
      "addressType": "string",
      "addressDetail": "string",
      "isMailingAddress": "string",
      "isNewest": "string"
    }
  ]
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "clientNo": "string",
    "addresses": [
      {
        "seqNo": "string",
        "addressType": "string",
        "addressDetail": "string",
        "lastChangeDate": "2026-04-23 00:00:00",
        "isMailingAddress": "string",
        "isNewest": "string"
      }
    ]
  }
}
```

---

### 25. 单条更新客户地址
- **接口 URL**: `/api/client/address/single/update`
- **请求方法**: POST
- **描述**: 单条更新客户地址
- **请求报文**:
```json
{
  "seqNo": "string",
  "clientNo": "string",
  "addressType": "string",
  "addressDetail": "string",
  "lastChangeDate": "string",
  "isMailingAddress": "string",
  "isNewest": "string",
  "delFlag": "string"
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "seqNo": "string",
    "clientNo": "string",
    "addressType": "string",
    "addressDetail": "string",
    "lastChangeDate": "2026-04-23 00:00:00",
    "isMailingAddress": "string",
    "isNewest": "string",
    "delFlag": "string"
  }
}
```

---

### 26. 查询客户地址
- **接口 URL**: `/api/client/address/query`
- **请求方法**: POST
- **描述**: 分页查询客户地址
- **请求报文**:
```json
{
  "clientNo": "string",
  "addressType": "string",
  "pageNum": 1,
  "pageSize": 10
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "clientNo": "string",
    "pageNum": 1,
    "pageSize": 10,
    "total": 0,
    "totalPages": 0,
    "list": [
      {
        "seqNo": "string",
        "addressType": "string",
        "addressDetail": "string",
        "lastChangeDate": "string",
        "isMailingAddress": "string",
        "isNewest": "string"
      }
    ]
  }
}
```

---

## 菜单接口

### 27. 查询菜单列表
- **接口 URL**: `/api/menus/query`
- **请求方法**: POST
- **描述**: 分页/条件查询菜单
- **请求报文**:
```json
{
  "menuName": "string",
  "menuUrl": "string",
  "status": "string",
  "parentId": 0,
  "pageNum": 1,
  "pageSize": 10
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": [
    {
      "menuId": 0,
      "menuName": "string",
      "menuUrl": "string",
      "icon": "string",
      "sortOrder": 0,
      "status": "string",
      "isLeaf": "string",
      "levelDepth": 1,
      "component": "string",
      "componentPath": "string",
      "parentId": 0,
      "delFlag": "string",
      "createTime": "2026-04-23T00:00:00"
    }
  ]
}
```

---

### 28. 获取菜单详情
- **接口 URL**: `/api/menus/{menuId}`
- **请求方法**: POST
- **描述**: 获取单个菜单详情
- **路径参数**: `menuId` (Long)
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "menuId": 0,
    "menuName": "string",
    "menuUrl": "string",
    "icon": "string",
    "sortOrder": 0,
    "status": "string",
    "isLeaf": "string",
    "levelDepth": 1,
    "component": "string",
    "componentPath": "string",
    "parentId": 0,
    "delFlag": "string",
    "createTime": "2026-04-23T00:00:00"
  }
}
```

---

### 29. 创建菜单
- **接口 URL**: `/api/menus/create`
- **请求方法**: POST
- **描述**: 新增菜单
- **请求报文**:
```json
{
  "menuName": "string",
  "menuUrl": "string",
  "icon": "string",
  "sortOrder": 0,
  "status": "string",
  "component": "string",
  "componentPath": "string",
  "parentId": 0
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "menuId": 0,
    "menuName": "string",
    "menuUrl": "string",
    "icon": "string",
    "sortOrder": 0,
    "status": "string",
    "isLeaf": "string",
    "levelDepth": 1,
    "component": "string",
    "componentPath": "string",
    "parentId": 0,
    "delFlag": "string",
    "createTime": "2026-04-23T00:00:00"
  }
}
```

---

### 30. 更新菜单
- **接口 URL**: `/api/menus/update`
- **请求方法**: POST
- **描述**: 更新菜单
- **请求报文**:
```json
{
  "menuId": 0,
  "menuName": "string",
  "menuUrl": "string",
  "icon": "string",
  "sortOrder": 0,
  "status": "string",
  "component": "string",
  "componentPath": "string",
  "parentId": 0
}
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": {
    "menuId": 0,
    "menuName": "string",
    "menuUrl": "string",
    "icon": "string",
    "sortOrder": 0,
    "status": "string",
    "isLeaf": "string",
    "levelDepth": 1,
    "component": "string",
    "componentPath": "string",
    "parentId": 0,
    "delFlag": "string",
    "createTime": "2026-04-23T00:00:00"
  }
}
```

---

### 31. 删除菜单
- **接口 URL**: `/api/menus/delete`
- **请求方法**: POST
- **描述**: 软删除菜单
- **请求参数**: `menuId` (Long)
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```

---

### 32. 获取菜单树
- **接口 URL**: `/api/menus/tree`
- **请求方法**: POST
- **描述**: 获取完整菜单树
- **请求报文**: 无
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": [
    {
      "menuId": 0,
      "menuName": "string",
      "menuUrl": "string",
      "icon": "string",
      "sortOrder": 0,
      "status": "string",
      "isLeaf": "string",
      "levelDepth": 1,
      "component": "string",
      "componentPath": "string",
      "parentId": 0,
      "children": []
    }
  ]
}
```

---

### 33. 角色分配菜单
- **接口 URL**: `/api/roles/{roleId}/menus/assign`
- **请求方法**: POST
- **描述**: 为角色分配菜单权限
- **路径参数**: `roleId` (Long)
- **请求报文**:
```json
[1, 2, 3]
```
- **响应报文**:
```json
{
  "code": "200",
  "message": "成功",
  "data": null
}
```
