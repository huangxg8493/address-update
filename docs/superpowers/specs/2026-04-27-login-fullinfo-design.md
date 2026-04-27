# 登录接口返回完整信息设计

## 1. 概述

**需求**：登录接口 `/api/auth/login` 返回用户的完整信息，包括用户基本信息、拥有的角色、有权限的菜单（含按钮级别，树形层级结构）。

**策略**：单一响应，登录成功返回 token + 用户信息 + 角色列表 + 菜单树，不再保留只返回 token 的旧逻辑。

---

## 2. 响应结构

```json
{
  "code": "000000",
  "message": "成功",
  "data": {
    "token": "eyJhbG...",
    "phone": "13800138000",
    "user": {
      "userId": 1234567890,
      "userName": "张三",
      "email": "zhangsan@example.com",
      "province": "广东省",
      "city": "深圳市",
      "district": "南山区",
      "hobby": "篮球,旅游",
      "status": "Y"
    },
    "roles": [
      { "roleId": 1, "roleCode": "admin", "roleName": "管理员", "status": "Y" },
      { "roleId": 2, "roleCode": "user", "roleName": "普通用户", "status": "Y" }
    ],
    "menus": [
      {
        "menuId": 1,
        "menuName": "系统管理",
        "menuUrl": "/system",
        "icon": "setting",
        "sortOrder": 1,
        "children": [
          {
            "menuId": 2,
            "menuName": "用户管理",
            "menuUrl": "/system/user",
            "icon": "user",
            "sortOrder": 1,
            "children": [
              {
                "menuId": 5,
                "menuName": "新增用户",
                "menuUrl": null,
                "icon": null,
                "sortOrder": 1,
                "isLeaf": true
              },
              {
                "menuId": 6,
                "menuName": "编辑用户",
                "menuUrl": null,
                "icon": null,
                "sortOrder": 2,
                "isLeaf": true
              }
            ]
          }
        ]
      }
    ]
  }
}
```

### 字段说明

| 字段 | 类型 | 说明 |
|------|------|------|
| `token` | string | JWT 令牌 |
| `phone` | string | 登录手机号 |
| `user` | object | 用户基本信息（不含 password） |
| `roles` | array | 用户拥有的角色列表 |
| `menus` | array | 根菜单列表（树形结构，含叶子按钮节点） |
| `menu.children` | array | 子菜单，递归嵌套 |
| `menu.isLeaf` | boolean | 是否叶子节点（按钮） |
| `menu.buttons` | array | 保留字段，恒为空数组（按钮本身无子节点） |

---

## 3. 数据获取逻辑

```
1. 验证用户身份（手机号 + 密码）→ 现有逻辑不变

2. 查询用户角色列表
   └─ SysUserRoleMapper.findByUserId(userId) → List<SysUserRole>
   └─ 根据 roleId 查询 SysRole 明细 → List<SysRole>

3. 查询用户菜单权限
   └─ 多角色场景：合并各角色的菜单（去重）
   └─ 遍历角色列表，获取每个角色的菜单ID集合 → 并集
   └─ 根据菜单ID列表获取菜单实体
   └─ 构建菜单树（parentId 递归组织）

4. 生成 JWT Token → 现有逻辑不变
```

### 菜单树构建算法

```
输入：扁平菜单列表（按 sortOrder 排序）
输出：树形菜单结构

1. 将菜单列表按 parentId 分组
2. 找到 parentId=null 的根菜单
3. 递归构建子树：
   - 叶子节点（isLeaf='Y'）：不包含 children
   - 非叶子节点：children = 子菜单列表（递归）
```

---

## 4. 涉及的组件变更

### 4.1 DTO 变更

**新增 `LoginResponse` 扩展字段**

原 `LoginResponse` 只有 `token` 和 `phone`。扩展为：
```java
public class LoginResponse {
    private String token;
    private String phone;
    private UserInfo user;        // 新增
    private List<RoleInfo> roles; // 新增
    private List<MenuTree> menus; // 新增
}
```

**新增内部类**

```java
public class UserInfo {
    private Long userId;
    private String userName;
    private String email;
    private String province;
    private String city;
    private String district;
    private String hobby;
    private String status;
}

public class RoleInfo {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String status;
}

public class MenuTree {
    private Long menuId;
    private String menuName;
    private String menuUrl;
    private String icon;
    private Integer sortOrder;
    private String isLeaf;
    private List<MenuTree> children;
    private List<Object> buttons; // 恒为空，占位用
}
```

### 4.2 Service 变更

**AuthService.login() 扩展**

原 login() 只返回 `LoginResult.success(token, phone)`，改为：

```java
public LoginResult login(LoginRequest request) {
    // 现有身份验证逻辑不变...

    // 新增：获取用户角色
    List<SysRole> roles = getRolesByUserId(userId);

    // 新增：获取用户菜单树
    List<MenuTree> menus = getMenusByUserId(userId);

    // 构建完整响应
    LoginResponse response = new LoginResponse();
    response.setToken(token);
    response.setPhone(phone);
    response.setUser(buildUserInfo(user));
    response.setRoles(buildRoleInfo(roles));
    response.setMenus(menus);

    return LoginResult.success(response);
}
```

**新增私有方法**

- `getRolesByUserId(Long userId)`：查询用户角色
- `getMenusByUserId(Long userId)`：查询用户菜单权限（多角色合并）
- `buildMenuTree(List<SysMenu> menus)`：将扁平菜单构建为树形结构

### 4.3 Repository 新增/变更

**SysUserRoleMapper.findByUserId**（已有） → 返回用户所有角色关联

**SysRoleMapper**（已有）：
- `findById(Long roleId)`：查角色明细
- `findByIds(List<Long> roleIds)`：批量查询

**SysMenuMapper 新增**：
```java
// 根据多个角色ID查询合并后的菜单（去重）
@Select("SELECT DISTINCT m.* FROM SYS_MENU m " +
        "JOIN SYS_ROLE_MENU rm ON m.menu_id = rm.menu_id " +
        "WHERE rm.role_id IN (#{roleIds}) AND m.del_flag='N' " +
        "ORDER BY m.sort_order")
List<SysMenu> findByRoleIds(@Param("roleIds") List<Long> roleIds);
```

### 4.4 新增 Service

**UserSessionService**（可选，职责分离）

如果 AuthService 膨胀，可抽取独立服务：
```java
@Service
public class UserSessionService {
    public LoginResponse buildSession(SysUser user, List<SysRole> roles, List<SysMenu> menus);
}
```

---

## 5. 多角色菜单合并策略

场景：一个用户有 `admin` 和 `user` 两个角色，各角色菜单可能重叠。

**处理方式**：
- SQL 层：`SELECT DISTINCT` 去重
- Java 层：合并后去重（HashSet）
- 最终返回给前端的菜单树不重复

---

## 6. 错误处理

| 场景 | 返回 |
|------|------|
| 用户未注册 | `USER_NOT_FOUND` |
| 用户已禁用 | `USER_DISABLED` |
| 密码错误 | `PASSWORD_ERROR` |
| 查询角色/菜单失败 | 记录日志，返回 `SYSTEM_ERROR` |

---

## 7. 安全性

- `LoginResponse.user` 不包含 `password` 字段
- `SysUser` 查询时不加载 password（在 SQL 中排除）
- Token 生成逻辑不变

---

## 8. 性能考虑

- 用户角色和菜单信息可缓存（Redis），避免每次登录查询
- 缓存策略（可选，后续实现）：
  - 用户信息缓存 30 分钟
  - 菜单树缓存 1 小时

---

## 9. 测试要点

1. **正常登录**：返回完整信息，验证 user/roles/menus 结构
2. **单角色用户**：验证只有一个角色
3. **多角色用户**：验证菜单去重正确
4. **无角色用户**：roles=[]，menus=[]
5. **菜单树结构**：验证 isLeaf/children 正确嵌套
6. **按钮节点**：isLeaf=true 且无 children

---

## 10. 实施步骤（待 writing-plans 细化）

1. 新增/修改 DTO 类
2. 扩展 SysMenuMapper（新增批量查询接口）
3. 扩展 AuthService.login()
4. 编写单元测试