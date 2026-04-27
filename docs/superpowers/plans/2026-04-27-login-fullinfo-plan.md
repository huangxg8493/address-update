# 登录接口返回完整信息 - 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 登录成功返回 token + 用户基本信息 + 角色列表 + 菜单树（含按钮叶子节点）

**Architecture:** 扩展 LoginResponse DTO，新增 UserInfo/RoleInfo/MenuTreeDTO 三个内部类，扩展 SysMenuMapper 支持多角色菜单合并查询，AuthService.login() 组装完整响应

**Tech Stack:** Java8, MyBatis, Spring Boot, JWT

---

## 文件变更总览

| 操作 | 文件路径 |
|------|----------|
| 创建 | `src/main/java/com/address/dto/UserInfo.java` |
| 创建 | `src/main/java/com/address/dto/RoleInfo.java` |
| 创建 | `src/main/java/com/address/dto/MenuTreeDTO.java` |
| 修改 | `src/main/java/com/address/dto/LoginResponse.java` |
| 修改 | `src/main/java/com/address/repository/SysMenuMapper.java` |
| 修改 | `src/main/java/com/address/service/AuthService.java` |
| 创建 | `src/test/java/com/address/service/AuthServiceLoginTest.java` |

---

## Task 1: 创建 DTO 内部类

**Files:**
- Create: `src/main/java/com/address/dto/UserInfo.java`
- Create: `src/main/java/com/address/dto/RoleInfo.java`
- Create: `src/main/java/com/address/dto/MenuTreeDTO.java`

---

### Task 1.1: 创建 UserInfo.java

- [ ] **Step 1: 创建文件**

```java
package com.address.dto;

public class UserInfo {
    private Long userId;
    private String userName;
    private String email;
    private String province;
    private String city;
    private String district;
    private String hobby;
    private String status;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }
    public String getHobby() { return hobby; }
    public void setHobby(String hobby) { this.hobby = hobby; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/dto/UserInfo.java
git commit -m "feat: 新增 UserInfo DTO"
```

---

### Task 1.2: 创建 RoleInfo.java

- [ ] **Step 1: 创建文件**

```java
package com.address.dto;

public class RoleInfo {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String status;

    public Long getRoleId() { return roleId; }
    public void setRoleId(Long roleId) { this.roleId = roleId; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/dto/RoleInfo.java
git commit -m "feat: 新增 RoleInfo DTO"
```

---

### Task 1.3: 创建 MenuTreeDTO.java

- [ ] **Step 1: 创建文件**

```java
package com.address.dto;

import java.util.ArrayList;
import java.util.List;

public class MenuTreeDTO {
    private Long menuId;
    private String menuName;
    private String menuUrl;
    private String icon;
    private Integer sortOrder;
    private String isLeaf;
    private List<MenuTreeDTO> children = new ArrayList<>();

    public Long getMenuId() { return menuId; }
    public void setMenuId(Long menuId) { this.menuId = menuId; }
    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getMenuUrl() { return menuUrl; }
    public void setMenuUrl(String menuUrl) { this.menuUrl = menuUrl; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getIsLeaf() { return isLeaf; }
    public void setIsLeaf(String isLeaf) { this.isLeaf = isLeaf; }
    public List<MenuTreeDTO> getChildren() { return children; }
    public void setChildren(List<MenuTreeDTO> children) { this.children = children; }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/dto/MenuTreeDTO.java
git commit -m "feat: 新增 MenuTreeDTO"
```

---

## Task 2: 扩展 LoginResponse

**Files:**
- Modify: `src/main/java/com/address/dto/LoginResponse.java`

- [ ] **Step 1: 读取现有文件**

```java
package com.address.dto;

public class LoginResponse {
    private String token;
    private String phone;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
```

- [ ] **Step 2: 扩展为完整结构**

```java
package com.address.dto;

import java.util.ArrayList;
import java.util.List;

public class LoginResponse {
    private String token;
    private String phone;
    private UserInfo user;
    private List<RoleInfo> roles = new ArrayList<>();
    private List<MenuTreeDTO> menus = new ArrayList<>();

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public UserInfo getUser() { return user; }
    public void setUser(UserInfo user) { this.user = user; }
    public List<RoleInfo> getRoles() { return roles; }
    public void setRoles(List<RoleInfo> roles) { this.roles = roles; }
    public List<MenuTreeDTO> getMenus() { return menus; }
    public void setMenus(List<MenuTreeDTO> menus) { this.menus = menus; }
}
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/dto/LoginResponse.java
git commit -m "feat: 扩展 LoginResponse 增加 user/roles/menus 字段"
```

---

## Task 3: 扩展 SysMenuMapper（支持多角色菜单查询）

**Files:**
- Modify: `src/main/java/com/address/repository/SysMenuMapper.java`

- [ ] **Step 1: 在 SysMenuMapper.java 中添加新方法（在 findRootMenus 之后）**

```java
@Select("SELECT menu_id, menu_name, menu_url, icon, sort_order, status, is_leaf, level_depth, component, component_path, parent_id, del_flag, create_time " +
        "FROM SYS_MENU m WHERE menu_id IN (" +
        "  SELECT DISTINCT rm.menu_id FROM SYS_ROLE_MENU rm WHERE rm.role_id IN (#{roleIds})" +
        ") AND del_flag='N' ORDER BY sort_order")
@Results({
    @Result(property = "menuId", column = "menu_id"),
    @Result(property = "menuName", column = "menu_name"),
    @Result(property = "menuUrl", column = "menu_url"),
    @Result(property = "icon", column = "icon"),
    @Result(property = "sortOrder", column = "sort_order"),
    @Result(property = "status", column = "status"),
    @Result(property = "isLeaf", column = "is_leaf"),
    @Result(property = "levelDepth", column = "level_depth"),
    @Result(property = "component", column = "component"),
    @Result(property = "componentPath", column = "component_path"),
    @Result(property = "parentId", column = "parent_id"),
    @Result(property = "delFlag", column = "del_flag"),
    @Result(property = "createTime", column = "create_time")
})
List<SysMenu> findByRoleIds(@Param("roleIds") List<Long> roleIds);
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/repository/SysMenuMapper.java
git commit -m "feat: SysMenuMapper 新增 findByRoleIds 支持多角色菜单查询"
```

---

## Task 4: 扩展 AuthService.login()

**Files:**
- Modify: `src/main/java/com/address/service/AuthService.java`

- [ ] **Step 1: 读取现有 AuthService.java 完整内容**

- [ ] **Step 2: 新增依赖注入（如果缺失）**

在类顶部注入：
```java
@Autowired
private SysUserRoleMapper sysUserRoleMapper;

@Autowired
private SysRoleMapper sysRoleMapper;

@Autowired
private SysMenuMapper sysMenuMapper;
```

- [ ] **Step 3: 修改 login() 方法返回值**

原 login() 返回 `LoginResult.success(token, phone)`，改为：

```java
public LoginResult login(LoginRequest request) {
    SysUser user = sysUserMapper.findActiveByPhone(request.getPhone());
    if (user == null) {
        SysUser existUser = sysUserMapper.findByPhone(request.getPhone());
        if (existUser == null) {
            return LoginResult.error(ErrorCode.USER_NOT_FOUND, "用户未注册");
        } else {
            return LoginResult.error(ErrorCode.USER_DISABLED, "用户已禁用");
        }
    }
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        return LoginResult.error(ErrorCode.PASSWORD_ERROR, "密码错误");
    }

    String token = jwtUtil.generateToken(user.getUserId(), user.getPhone());

    // 获取用户角色
    List<RoleInfo> roles = getRolesByUserId(user.getUserId());

    // 获取用户菜单树
    List<MenuTreeDTO> menus = getMenusByUserId(user.getUserId());

    // 构建完整响应
    LoginResponse response = new LoginResponse();
    response.setToken(token);
    response.setPhone(user.getPhone());
    response.setUser(buildUserInfo(user));
    response.setRoles(roles);
    response.setMenus(menus);

    return LoginResult.success(response);
}
```

- [ ] **Step 4: 在 login() 方法之后新增三个私有方法**

```java
private List<RoleInfo> getRolesByUserId(Long userId) {
    List<SysUserRole> userRoles = sysUserRoleMapper.findByUserId(userId);
    if (userRoles == null || userRoles.isEmpty()) {
        return new ArrayList<>();
    }
    List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    List<RoleInfo> result = new ArrayList<>();
    for (Long roleId : roleIds) {
        SysRole role = sysRoleMapper.findById(roleId);
        if (role != null && "Y".equals(role.getStatus())) {
            RoleInfo info = new RoleInfo();
            info.setRoleId(role.getRoleId());
            info.setRoleCode(role.getRoleCode());
            info.setRoleName(role.getRoleName());
            info.setStatus(role.getStatus());
            result.add(info);
        }
    }
    return result;
}

private List<MenuTreeDTO> getMenusByUserId(Long userId) {
    List<SysUserRole> userRoles = sysUserRoleMapper.findByUserId(userId);
    if (userRoles == null || userRoles.isEmpty()) {
        return new ArrayList<>();
    }
    List<Long> roleIds = userRoles.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    List<SysMenu> menus = sysMenuMapper.findByRoleIds(roleIds);
    if (menus == null || menus.isEmpty()) {
        return new ArrayList<>();
    }
    return buildMenuTree(menus);
}

private List<MenuTreeDTO> buildMenuTree(List<SysMenu> flatMenus) {
    Map<Long, List<SysMenu>> childrenMap = new HashMap<>();
    List<SysMenu> roots = new ArrayList<>();
    for (SysMenu menu : flatMenus) {
        if (menu.getParentId() == null) {
            roots.add(menu);
        } else {
            childrenMap.computeIfAbsent(menu.getParentId(), k -> new ArrayList<>()).add(menu);
        }
    }
    List<MenuTreeDTO> result = new ArrayList<>();
    for (SysMenu root : roots) {
        result.add(buildMenuTreeNode(root, childrenMap));
    }
    return result;
}

private MenuTreeDTO buildMenuTreeNode(SysMenu menu, Map<Long, List<SysMenu>> childrenMap) {
    MenuTreeDTO node = new MenuTreeDTO();
    node.setMenuId(menu.getMenuId());
    node.setMenuName(menu.getMenuName());
    node.setMenuUrl(menu.getMenuUrl());
    node.setIcon(menu.getIcon());
    node.setSortOrder(menu.getSortOrder());
    node.setIsLeaf(menu.getIsLeaf());
    List<SysMenu> children = childrenMap.get(menu.getMenuId());
    if (children != null && !children.isEmpty()) {
        List<MenuTreeDTO> childNodes = new ArrayList<>();
        children.sort(Comparator.comparing(SysMenu::getSortOrder));
        for (SysMenu child : children) {
            childNodes.add(buildMenuTreeNode(child, childrenMap));
        }
        node.setChildren(childNodes);
    }
    return node;
}

private UserInfo buildUserInfo(SysUser user) {
    UserInfo info = new UserInfo();
    info.setUserId(user.getUserId());
    info.setUserName(user.getUserName());
    info.setEmail(user.getEmail());
    info.setProvince(user.getProvince());
    info.setCity(user.getCity());
    info.setDistrict(user.getDistrict());
    info.setHobby(user.getHobby());
    info.setStatus(user.getStatus());
    return info;
}
```

- [ ] **Step 5: 添加必要 import**

```java
import java.util.*;
import java.util.stream.Collectors();
```

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/address/service/AuthService.java
git commit -m "feat: AuthService.login() 返回完整用户信息含菜单树"
```

---

## Task 5: 编写测试

**Files:**
- Create: `src/test/java/com/address/service/AuthServiceLoginTest.java`

- [ ] **Step 1: 创建测试文件**

```java
package com.address.service;

import com.address.dto.LoginRequest;
import com.address.dto.LoginResponse;
import com.address.dto.LoginResult;
import com.address.dto.MenuTreeDTO;
import com.address.dto.RoleInfo;
import com.address.dto.UserInfo;
import com.address.model.SysUser;
import com.address.repository.SysUserMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.junit.Assert.*;

public class AuthServiceLoginTest extends BaseServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testLoginWithFullInfo_success() {
        // 准备测试用户
        Long userId = 1000000001L;
        String phone = "13800001111";
        String password = "test123";

        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus("Y");
        user.setUserName("测试用户");
        user.setEmail("test@example.com");
        user.setProvince("广东省");
        user.setCity("深圳市");
        user.setDistrict("南山区");
        user.setHobby("篮球");
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setUpdateTime(java.time.LocalDateTime.now());
        sysUserMapper.insert(user);

        try {
            LoginRequest request = new LoginRequest();
            request.setPhone(phone);
            request.setPassword(password);

            LoginResult result = authService.login(request);

            assertTrue("登录应成功", result.isSuccess());
            assertNotNull("token不为空", result.getToken());

            LoginResponse response = result.toLoginResponse();
            assertNotNull("user不为空", response.getUser());
            assertEquals("用户姓名正确", "测试用户", response.getUser().getUserName());
            assertNotNull("roles不为空", response.getRoles());
            assertNotNull("menus不为空", response.getMenus());
        } finally {
            // 清理测试数据
            sysUserMapper.deleteById(userId);
        }
    }

    @Test
    public void testLogin_userNotFound() {
        LoginRequest request = new LoginRequest();
        request.setPhone("13999999999");
        request.setPassword("any");

        LoginResult result = authService.login(request);

        assertFalse("用户不存在应失败", result.isSuccess());
        assertEquals("A0002", result.getCode());
    }

    @Test
    public void testLogin_wrongPassword() {
        // 创建用户
        Long userId = 1000000002L;
        String phone = "13800002222";
        String password = "test123";

        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus("Y");
        user.setUserName("测试用户2");
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setUpdateTime(java.time.LocalDateTime.now());
        sysUserMapper.insert(user);

        try {
            LoginRequest request = new LoginRequest();
            request.setPhone(phone);
            request.setPassword("wrongpassword");

            LoginResult result = authService.login(request);

            assertFalse("密码错误应失败", result.isSuccess());
            assertEquals("A0003", result.getCode());
        } finally {
            sysUserMapper.deleteById(userId);
        }
    }
}
```

- [ ] **Step 2: 运行测试验证**

```bash
mvn test -Dtest=AuthServiceLoginTest -pl .
```

- [ ] **Step 3: 提交**

```bash
git add src/test/java/com/address/service/AuthServiceLoginTest.java
git commit -m "test: AuthServiceLoginTest 登录返回完整信息测试"
```

---

## Task 6: 验证（使用 verification-before-completion skill）

- [ ] 运行完整测试套件

```bash
mvn test 2>&1 | grep -E "Tests run|BUILD"
```

- [ ] 确保所有测试通过

---

## 实施检查清单

- [ ] UserInfo.java 创建
- [ ] RoleInfo.java 创建
- [ ] MenuTreeDTO.java 创建
- [ ] LoginResponse.java 扩展
- [ ] SysMenuMapper.findByRoleIds() 新增
- [ ] AuthService.login() 扩展
- [ ] AuthServiceLoginTest 测试通过
- [ ] 所有测试通过