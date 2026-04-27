# 角色菜单关联实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 新增 sys_role_menu 表和相关接口，实现角色菜单的批量分配与查询

**Architecture:** 新建关联表 + Mapper + Service，复用现有 SysMenu 查询完整菜单信息，批量分配采用先删后插模式

**Tech Stack:** Java8, MyBatis, Spring Boot

---

## Task 1: 新建 SysRoleMenu 实体类

**Files:**
- Create: `src/main/java/com/address/model/SysRoleMenu.java`

- [ ] **Step 1: 编写实体类**

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

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/model/SysRoleMenu.java
git commit -m "feat: 新增 SysRoleMenu 实体类"
```

---

## Task 2: 新建 SysRoleMenuMapper

**Files:**
- Create: `src/main/java/com/address/repository/SysRoleMenuMapper.java`

**注意**: 先查看现有 Mapper 的实现方式（XML 还是注解）

- [ ] **Step 1: 查看现有 SysRoleMapper 实现方式**

```bash
cat src/main/java/com/address/repository/SysRoleMapper.java
```

- [ ] **Step 2: 创建 SysRoleMenuMapper**

```java
package com.address.repository;

import com.address.model.SysRoleMenu;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SysRoleMenuMapper {

    @Delete("DELETE FROM sys_role_menu WHERE role_id = #{roleId}")
    void deleteByRoleId(Long roleId);

    @Insert("<script>" +
            "INSERT INTO sys_role_menu (role_id, menu_id, create_time) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.roleId}, #{item.menuId}, NOW())" +
            "</foreach>" +
            "</script>")
    void insertBatch(@Param("list") List<SysRoleMenu> roleMenus);

    @Select("SELECT menu_id FROM sys_role_menu WHERE role_id = #{roleId}")
    List<Long> selectMenuIdsByRoleId(Long roleId);
}
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/SysRoleMenuMapper.java
git commit -m "feat: 新增 SysRoleMenuMapper"
```

---

## Task 3: 新建 RoleMenuService

**Files:**
- Create: `src/main/java/com/address/service/RoleMenuService.java`

- [ ] **Step 1: 创建 RoleMenuService**

```java
package com.address.service;

import com.address.model.SysMenu;
import com.address.model.SysRoleMenu;
import com.address.repository.SysRoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleMenuService {

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Autowired
    private MenuService menuService;

    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        sysRoleMenuMapper.deleteByRoleId(roleId);
        if (menuIds == null || menuIds.isEmpty()) {
            return;
        }
        List<SysRoleMenu> roleMenus = new ArrayList<>();
        for (Long menuId : menuIds) {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            rm.setCreateTime(LocalDateTime.now());
            roleMenus.add(rm);
        }
        sysRoleMenuMapper.insertBatch(roleMenus);
    }

    public List<SysMenu> getMenusByRoleId(Long roleId) {
        List<Long> menuIds = sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
        if (menuIds == null || menuIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<SysMenu> menus = new ArrayList<>();
        for (Long menuId : menuIds) {
            SysMenu menu = menuService.getById(menuId);
            if (menu != null && !"Y".equals(menu.getDelFlag())) {
                menus.add(menu);
            }
        }
        return menus;
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/service/RoleMenuService.java
git commit -m "feat: 新增 RoleMenuService"
```

---

## Task 4: 修改 RoleController 添加接口

**Files:**
- Modify: `src/main/java/com/address/controller/RoleController.java`

**注意**: 先查看 RoleController 是否存在，若不存在则需确认角色管理接口位置

- [ ] **Step 1: 查看 RoleController 是否存在**

```bash
ls src/main/java/com/address/controller/Role*.java
```

- [ ] **Step 2: 添加接口到 RoleController（假设存在）**

在 RoleController.java 中添加：

```java
@Autowired
private RoleMenuService roleMenuService;

@PostMapping("/api/roles/{roleId}/menus")
public ApiResponse<Void> assignMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
    roleMenuService.assignMenus(roleId, menuIds);
    return ApiResponse.success(null);
}

@GetMapping("/api/roles/{roleId}/menus")
public ApiResponse<List<MenuResponse>> getMenus(@PathVariable Long roleId) {
    List<SysMenu> menus = roleMenuService.getMenusByRoleId(roleId);
    List<MenuResponse> responses = menus.stream().map(menu -> {
        MenuResponse r = new MenuResponse();
        r.setMenuId(menu.getMenuId());
        r.setMenuName(menu.getMenuName());
        r.setMenuUrl(menu.getMenuUrl());
        r.setIcon(menu.getIcon());
        r.setParentId(menu.getParentId());
        return r;
    }).collect(Collectors.toList());
    return ApiResponse.success(responses);
}
```

如 RoleController 不存在，则需要新建或在现有 Controller 中添加这些接口。

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/controller/RoleController.java
git commit -m "feat: RoleController 添加菜单分配和查询接口"
```

---

## Task 5: 编写测试用例

**Files:**
- Create: `src/test/java/com/address/service/RoleMenuServiceTest.java`

- [ ] **Step 1: 编写测试用例**

```java
package com.address.service;

import com.address.model.SysMenu;
import com.address.repository.SysRoleMenuMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RoleMenuServiceTest {

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Test
    @Transactional
    public void testAssignMenus() {
        Long roleId = 1L;
        List<Long> menuIds = Arrays.asList(1L, 2L, 3L);

        roleMenuService.assignMenus(roleId, menuIds);

        List<Long> result = sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }

    @Test
    @Transactional
    public void testAssignMenus_EmptyList() {
        Long roleId = 1L;

        roleMenuService.assignMenus(roleId, Arrays.asList());

        List<Long> result = sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
        assertEquals(0, result.size());
    }

    @Test
    @Transactional
    public void testAssignMenus_ClearOldMenus() {
        Long roleId = 1L;
        roleMenuService.assignMenus(roleId, Arrays.asList(1L, 2L));

        roleMenuService.assignMenus(roleId, Arrays.asList(3L));

        List<Long> result = sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
        assertEquals(1, result.size());
        assertTrue(result.contains(3L));
        assertFalse(result.contains(1L));
        assertFalse(result.contains(2L));
    }
}
```

- [ ] **Step 2: 运行测试**

```bash
mvn test -Dtest=RoleMenuServiceTest
```

- [ ] **Step 3: 提交**

```bash
git add src/test/java/com/address/service/RoleMenuServiceTest.java
git commit -m "test: 新增 RoleMenuService 测试"
```

---

## 依赖关系

1. Task 1 → Task 2（实体类完成后创建 Mapper）
2. Task 2 → Task 3（Mapper 完成后创建 Service）
3. Task 3 → Task 4（Service 完成后添加 Controller 接口）
4. Task 4 → Task 5（接口完成后编写测试）

---

**Plan complete and saved to `docs/superpowers/plans/2026-04-27-role-menu-plan.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**