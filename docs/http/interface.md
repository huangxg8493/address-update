# 接口文档

> **认证说明**: 除 `/api/auth/register` 和 `/api/auth/login` 外，所有接口需要在请求头中携带 Token：
> - Header: `Authorization: Bearer {token}`
> - token 来自登录接口的响应数据

## 认证接口

### 1. 用户注册

- **接口 URL**: `POST /api/auth/register`
- **请求方法**: POST
- **Content-Type**: `application/json`
- **描述**: 用户注册

**请求体**:
```json
{
    "phone": "13900000001",
    "password": "password123",
    "userName": "张三",
    "email": "zhangsan@example.com",
    "province": "广东省",
    "city": "深圳市",
    "district": "南山区",
    "hobby": "篮球"
}
```

**字段说明**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号，11位，1开头 |
| password | String | 是 | 密码，最少6位 |
| userName | String | 否 | 用户名 |
| email | String | 否 | 邮箱 |
| province | String | 否 | 省份 |
| city | String | 否 | 城市 |
| district | String | 否 | 区县 |
| hobby | String | 否 | 爱好 |

**成功响应** (HTTP 200):
```json
{
    "code": "000000",
    "message": "成功",
    "data": null
}
```

**错误响应** (用户已注册):
```json
{
    "code": "101004",
    "message": "手机号已注册",
    "data": null
}
```

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 000000 | 成功 |
| 101004 | 手机号已注册 |
| 101005 | 手机号格式错误 |
| 101006 | 密码格式错误 |

---

### 2. 用户登录

- **接口 URL**: `POST /api/auth/login`
- **请求方法**: POST
- **Content-Type**: `application/json`
- **描述**: 用户登录，返回 JWT 令牌及完整的用户信息

**请求体**:
```json
{
    "phone": "13900000001",
    "password": "password123"
}
```

**字段说明**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号，11位，1开头 |
| password | String | 是 | 密码，最少6位 |

**成功响应** (HTTP 200):
```json
{
    "code": "000000",
    "message": "成功",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "phone": "13900000001",
        "user": {
            "userId": 1,
            "userName": "张三",
            "email": "zhangsan@example.com",
            "province": "广东省",
            "city": "深圳市",
            "district": "南山区",
            "hobby": "篮球",
            "status": "Y"
        },
        "roles": [
            {
                "roleId": 1,
                "roleCode": "admin",
                "roleName": "系统管理员",
                "status": "Y"
            }
        ],
        "menus": [
            {
                "menuId": 1,
                "menuName": "系统管理",
                "menuUrl": "/system",
                "icon": "setting",
                "sortOrder": 1,
                "isLeaf": "N",
                "menuType": "CATALOG",
                "children": [
                    {
                        "menuId": 2,
                        "menuName": "用户管理",
                        "menuUrl": "/system/user",
                        "icon": "user",
                        "sortOrder": 1,
                        "isLeaf": "Y",
                        "menuType": "MENU",
                        "children": []
                    }
                ]
            }
        ]
    }
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| token | String | JWT 令牌，用于后续请求的身份认证 |
| phone | String | 登录手机号 |
| user | Object | 用户详细信息 |
| user.userId | Long | 用户ID |
| user.userName | String | 用户名 |
| user.email | String | 邮箱 |
| user.province | String | 省份 |
| user.city | String | 城市 |
| user.district | String | 区县 |
| user.hobby | String | 爱好 |
| user.status | String | 状态 (Y=正常, N=禁用) |
| roles | Array | 用户角色列表 |
| roles[].roleId | Long | 角色ID |
| roles[].roleCode | String | 角色编码 |
| roles[].roleName | String | 角色名称 |
| roles[].status | String | 角色状态 |
| menus | Array | 用户菜单树（用于前端权限控制） |
| menus[].menuId | Long | 菜单ID |
| menus[].menuName | String | 菜单名称 |
| menus[].menuUrl | String | 菜单URL |
| menus[].icon | String | 菜单图标 |
| menus[].sortOrder | Integer | 排序号 |
| menus[].isLeaf | String | 是否叶子节点 (Y/N) |
| menus[].menuType | String | 菜单类型：MENU-菜单，CATALOG-目录，BUTTON-按钮 |
| menus[].children | Array | 子菜单 |

**错误响应（用户未注册）**:
```json
{
    "code": "101001",
    "message": "用户未注册",
    "data": null
}
```

**错误响应（用户已禁用）**:
```json
{
    "code": "101002",
    "message": "用户已禁用",
    "data": null
}
```

**错误响应（密码错误）**:
```json
{
    "code": "101003",
    "message": "密码错误",
    "data": null
}
```

**错误码**:

| 错误码 | 说明 |
|--------|------|
| 000000 | 成功 |
| 101001 | 用户未注册 |
| 101002 | 用户已禁用 |
| 101003 | 密码错误 |

---

### 3. 用户登出

- **接口 URL**: `POST /api/auth/logout`
- **请求方法**: POST
- **描述**: 用户登出

**请求报文**: 无

**响应报文**:
```json
{
    "code": "000000",
    "message": "成功",
    "data": null
}
```

---

## 用户接口

### 4. 查询用户列表

- **接口 URL**: `POST /api/users/query`
- **请求方法**: POST
- **Content-Type**: `application/json`
- **描述**: 查询用户列表，支持按手机号和状态筛选

**请求体**:
```json
{
    "phone": "13900000001",
    "status": "Y",
    "pageNum": 1,
    "pageSize": 10
}
```

**字段说明**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 否 | 手机号，精确匹配 |
| status | String | 否 | 状态 (Y=正常, N=禁用) |
| pageNum | Integer | 否 | 页码，默认 1 |
| pageSize | Integer | 否 | 每页条数，默认 10 |

**成功响应** (HTTP 200):
```json
{
    "code": "000000",
    "message": "成功",
    "data": [
        {
            "userId": 704134058694807552,
            "phone": "18658127206",
            "status": "Y",
            "userName": "黄小刚",
            "email": "",
            "province": "",
            "city": "",
            "district": "",
            "hobby": "",
            "createTime": "2026-04-28T10:37:30",
            "updateTime": null
        }
    ]
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| userId | Long | 用户ID |
| phone | String | 手机号 |
| status | String | 状态 (Y=正常, N=禁用) |
| userName | String | 用户名 |
| email | String | 邮箱 |
| province | String | 省份 |
| city | String | 城市 |
| district | String | 区县 |
| hobby | String | 爱好 |
| createTime | LocalDateTime | 创建时间 |
| updateTime | LocalDateTime | 更新时间 |

---

### 5. 创建用户

- **接口 URL**: `POST /api/users/create`
- **请求方法**: POST
- **Content-Type**: `application/json`
- **描述**: 创建新用户

**请求体**:
```json
{
    "phone": "13900000002",
    "password": "password123",
    "status": "Y"
}
```

**字段说明**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| phone | String | 是 | 手机号，11位，1开头 |
| password | String | 是 | 密码，最少6位 |
| status | String | 否 | 状态，默认 Y |

**成功响应** (HTTP 200):
```json
{
    "code": "000000",
    "message": "成功",
    "data": {
        "userId": 1234567890,
        "phone": "13900000002",
        "status": "Y",
        "userName": null,
        "email": null,
        "province": null,
        "city": null,
        "district": null,
        "hobby": null,
        "createTime": "2026-04-28T10:37:30",
        "updateTime": null
    }
}
```

---

### 6. 更新用户

- **接口 URL**: `POST /api/users/update`
- **请求方法**: POST
- **Content-Type**: `application/json`
- **描述**: 更新用户信息

**请求体**:
```json
{
    "userId": 1234567890,
    "phone": "13900000002",
    "password": "newpassword123",
    "status": "Y"
}
```

**字段说明**:

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| userId | Long | 是 | 用户ID |
| phone | String | 否 | 手机号 |
| password | String | 否 | 新密码 |
| status | String | 否 | 状态 |

**成功响应** (HTTP 200): 同创建用户

---

### 7. 删除用户

- **接口 URL**: `POST /api/users/delete`
- **请求方法**: POST
- **描述**: 删除用户

**请求参数**: `userId` (Long)

**成功响应** (HTTP 200):
```json
{
    "code": "000000",
    "message": "成功",
    "data": null
}
```

---

### 8. 分配用户角色

- **接口 URL**: `POST /api/users/{userId}/roles/assign`
- **请求方法**: POST
- **描述**: 为用户分配角色
- **路径参数**: `userId` (Long)

**请求体**:
```json
[1, 2, 3]
```

**成功响应** (HTTP 200):
```json
{
    "code": "000000",
    "message": "成功",
    "data": null
}
```

---

### 9. 获取当前用户信息

- **接口 URL**: `POST /api/users/me/get`
- **请求方法**: POST
- **描述**: 获取当前登录用户信息

**请求报文**: 无

**成功响应** (HTTP 200):
```json
{
    "code": "000000",
    "message": "成功",
    "data": null
}
```