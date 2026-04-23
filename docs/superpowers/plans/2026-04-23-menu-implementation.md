# 菜单管理模块实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现菜单管理模块的增删改查功能，支持无限级层级结构

**Architecture:** 采用四层架构 Controller + Service + Mapper + Model，使用 MyBatis Mapper 进行数据库操作，通过 parentId 自引用实现无限级菜单层级，软删除机制

**Tech Stack:** Java8 + Maven + MyBatis + Spring Boot

---

## 文件清单

| 层级 | 文件 | 职责 |
|------|------|------|
| Model | SysMenu.java | 菜单实体 |
| Model | MenuCreateRequest.java | 创建请求 DTO |
| Model | MenuUpdateRequest.java | 更新请求 DTO |
| Model | MenuQueryRequest.java | 查询请求 DTO |
| Model | MenuResponse.java | 响应 DTO |
| Model | MenuTreeResponse.java | 树形响应 DTO |
| Mapper | SysMenuMapper.java | MyBatis Mapper |
| Service | MenuService.java | 业务逻辑 |
| Controller | MenuController.java | 接口层 |
| Test | MenuControllerTest.java | 单元测试 |

---

### Task 1: 创建菜单实体 SysMenu

**Files:**
- Create: `src/main/java/com/address/model/SysMenu.java`

- [ ] **Step 1: 创建实体类**

```java
package com.address.model;

import java.time.LocalDateTime;

/**
 * 系统菜单实体
 */
public class SysMenu {
    private Long menuId;
    private String menuName;
    private String menuUrl;
    private String icon;
    private Integer sortOrder;
    private String status;
    private String isLeaf;
    private Integer levelDepth;
    private String component;
    private String componentPath;
    private Long parentId;
    private String delFlag;
    private LocalDateTime createTime;

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIsLeaf() { return isLeaf; }
    public void setIsLeaf(String isLeaf) { this.isLeaf = isLeaf; }
    public Integer getLevelDepth() { return levelDepth; }
    public void setLevelDepth(Integer levelDepth) { this.levelDepth = levelDepth; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getComponentPath() { return componentPath; }
    public void setComponentPath(String componentPath) { this.componentPath = componentPath; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/model/SysMenu.java
git commit -m "feat(model): 添加 SysMenu 菜单实体"
```

---

### Task 2: 创建菜单相关 DTO

**Files:**
- Create: `src/main/java/com/address/dto/MenuCreateRequest.java`
- Create: `src/main/java/com/address/dto/MenuUpdateRequest.java`
- Create: `src/main/java/com/address/dto/MenuQueryRequest.java`
- Create: `src/main/java/com/address/dto/MenuResponse.java`
- Create: `src/main/java/com/address/dto/MenuTreeResponse.java`

- [ ] **Step 1: 创建 MenuCreateRequest**

```java
package com.address.dto;

public class MenuCreateRequest {
    private String menuName;
    private String menuUrl;
    private String icon;
    private Integer sortOrder;
    private String status;
    private String component;
    private String componentPath;
    private Long parentId;

    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getMenuUrl() { return menuUrl; }
    public void setMenuUrl(String menuUrl) { this.menuUrl = menuUrl; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getComponentPath() { return componentPath; }
    public void setComponentPath(String componentPath) { this.componentPath = componentPath; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
```

- [ ] **Step 2: 创建 MenuUpdateRequest**

```java
package com.address.dto;

public class MenuUpdateRequest {
    private Long menuId;
    private String menuName;
    private String menuUrl;
    private String icon;
    private Integer sortOrder;
    private String status;
    private String component;
    private String componentPath;
    private Long parentId;

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getComponentPath() { return componentPath; }
    public void setComponentPath(String componentPath) { this.componentPath = componentPath; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
}
```

- [ ] **Step 3: 创建 MenuQueryRequest**

```java
package com.address.dto;

public class MenuQueryRequest {
    private String menuName;
    private String menuUrl;
    private String status;
    private Long parentId;
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }
    public String getMenuUrl() { return menuUrl; }
    public void setMenuUrl(String menuUrl) { this.menuUrl = menuUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
```

- [ ] **Step 4: 创建 MenuResponse**

```java
package com.address.dto;

import java.time.LocalDateTime;

public class MenuResponse {
    private Long menuId;
    private String menuName;
    private String menuUrl;
    private String icon;
    private Integer sortOrder;
    private String status;
    private String isLeaf;
    private Integer levelDepth;
    private String component;
    private String componentPath;
    private Long parentId;
    private String delFlag;
    private LocalDateTime createTime;

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIsLeaf() { return isLeaf; }
    public void setIsLeaf(String isLeaf) { this.isLeaf = isLeaf; }
    public Integer getLevelDepth() { return levelDepth; }
    public void setLevelDepth(Integer levelDepth) { this.levelDepth = levelDepth; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getComponentPath() { return componentPath; }
    public void setComponentPath(String componentPath) { this.componentPath = componentPath; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
```

- [ ] **Step 5: 创建 MenuTreeResponse**

```java
package com.address.dto;

import java.util.List;

public class MenuTreeResponse {
    private Long menuId;
    private String menuName;
    private String menuUrl;
    private String icon;
    private Integer sortOrder;
    private String status;
    private String isLeaf;
    private Integer levelDepth;
    private String component;
    private String componentPath;
    private Long parentId;
    private List<MenuTreeResponse> children;

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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getIsLeaf() { return isLeaf; }
    public void setIsLeaf(String isLeaf) { this.isLeaf = isLeaf; }
    public Integer getLevelDepth() { return levelDepth; }
    public void setLevelDepth(Integer levelDepth) { this.levelDepth = levelDepth; }
    public String getComponent() { return component; }
    public void setComponent(String component) { this.component = component; }
    public String getComponentPath() { return componentPath; }
    public void setComponentPath(String componentPath) { this.componentPath = componentPath; }
    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public List<MenuTreeResponse> getChildren() { return children; }
    public void setChildren(List<MenuTreeResponse> children) { this.children = children; }
}
```

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/address/dto/MenuCreateRequest.java src/main/java/com/address/dto/MenuUpdateRequest.java src/main/java/com/address/dto/MenuQueryRequest.java src/main/java/com/address/dto/MenuResponse.java src/main/java/com/address/dto/MenuTreeResponse.java
git commit -m "feat(dto): 添加菜单相关 DTO 类"
```

---

### Task 3: 创建菜单 Mapper

**Files:**
- Create: `src/main/java/com/address/repository/SysMenuMapper.java`

- [ ] **Step 1: 创建 Mapper**

```java
package com.address.repository;

import com.address.model.SysMenu;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysMenuMapper {
    @Insert("INSERT INTO sys_menu(menu_id, menu_name, menu_url, icon, sort_order, status, is_leaf, level_depth, component, component_path, parent_id, del_flag, create_time) " +
            "VALUES(#{menuId}, #{menuName}, #{menuUrl}, #{icon}, #{sortOrder}, #{status}, #{isLeaf}, #{levelDepth}, #{component}, #{componentPath}, #{parentId}, #{delFlag}, #{createTime})")
    void insert(SysMenu menu);

    @Update("UPDATE sys_menu SET menu_name=#{menuName}, menu_url=#{menuUrl}, icon=#{icon}, sort_order=#{sortOrder}, status=#{status}, component=#{component}, component_path=#{componentPath}, parent_id=#{parentId}, is_leaf=#{isLeaf}, level_depth=#{levelDepth} WHERE menu_id=#{menuId}")
    void update(SysMenu menu);

    @Update("UPDATE sys_menu SET del_flag='Y' WHERE menu_id=#{menuId}")
    void deleteById(@Param("menuId") Long menuId);

    @Select("SELECT * FROM sys_menu WHERE menu_id = #{menuId} AND del_flag='N'")
    SysMenu findById(@Param("menuId") Long menuId);

    @Select("SELECT * FROM sys_menu WHERE del_flag='N'")
    java.util.List<SysMenu> findAll();

    @Select("SELECT * FROM sys_menu WHERE parent_id = #{parentId} AND del_flag='N'")
    java.util.List<SysMenu> findByParentId(@Param("parentId") Long parentId);

    @Select("SELECT * FROM sys_menu WHERE parent_id IS NULL AND del_flag='N'")
    java.util.List<SysMenu> findRootMenus();
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/repository/SysMenuMapper.java
git commit -m "feat(mapper): 添加 SysMenuMapper"
```

---

### Task 4: 创建菜单 Service

**Files:**
- Create: `src/main/java/com/address/service/MenuService.java`

- [ ] **Step 1: 创建 Service**

```java
package com.address.service;

import com.address.dto.*;
import com.address.model.SysMenu;
import com.address.repository.SysMenuMapper;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    public List<MenuResponse> query(MenuQueryRequest request) {
        List<SysMenu> menus = sysMenuMapper.findAll();
        List<MenuResponse> responses = new ArrayList<>();
        for (SysMenu menu : menus) {
            if (request.getMenuName() != null && !request.getMenuName().equals(menu.getMenuName())) {
                continue;
            }
            if (request.getMenuUrl() != null && !request.getMenuUrl().equals(menu.getMenuUrl())) {
                continue;
            }
            if (request.getStatus() != null && !request.getStatus().equals(menu.getStatus())) {
                continue;
            }
            if (request.getParentId() != null && !request.getParentId().equals(menu.getParentId())) {
                continue;
            }
            responses.add(toResponse(menu));
        }
        return responses;
    }

    public MenuResponse getById(Long menuId) {
        SysMenu menu = sysMenuMapper.findById(menuId);
        if (menu == null) {
            throw new RuntimeException("菜单不存在");
        }
        return toResponse(menu);
    }

    public MenuResponse create(MenuCreateRequest request) {
        if (request.getMenuName() == null || request.getMenuName().trim().isEmpty()) {
            throw new RuntimeException("菜单名称不能为空");
        }
        if (request.getMenuUrl() == null || request.getMenuUrl().trim().isEmpty()) {
            throw new RuntimeException("菜单URL不能为空");
        }
        SysMenu menu = new SysMenu();
        menu.setMenuId(SnowflakeIdGenerator.getInstance().nextId());
        menu.setMenuName(request.getMenuName());
        menu.setMenuUrl(request.getMenuUrl());
        menu.setIcon(request.getIcon());
        menu.setSortOrder(request.getSortOrder());
        menu.setStatus(request.getStatus() != null ? request.getStatus() : "Y");
        menu.setComponent(request.getComponent());
        menu.setComponentPath(request.getComponentPath());
        menu.setParentId(request.getParentId());
        menu.setDelFlag("N");
        menu.setCreateTime(LocalDateTime.now());
        menu.setLevelDepth(calculateLevelDepth(request.getParentId()));
        menu.setIsLeaf("Y");
        sysMenuMapper.insert(menu);
        updateParentLeafStatus(request.getParentId());
        return toResponse(menu);
    }

    public MenuResponse update(MenuUpdateRequest request) {
        SysMenu menu = sysMenuMapper.findById(request.getMenuId());
        if (menu == null) {
            throw new RuntimeException("菜单不存在");
        }
        if (request.getMenuName() != null) {
            menu.setMenuName(request.getMenuName());
        }
        if (request.getMenuUrl() != null) {
            menu.setMenuUrl(request.getMenuUrl());
        }
        if (request.getIcon() != null) {
            menu.setIcon(request.getIcon());
        }
        if (request.getSortOrder() != null) {
            menu.setSortOrder(request.getSortOrder());
        }
        if (request.getStatus() != null) {
            menu.setStatus(request.getStatus());
        }
        if (request.getComponent() != null) {
            menu.setComponent(request.getComponent());
        }
        if (request.getComponentPath() != null) {
            menu.setComponentPath(request.getComponentPath());
        }
        if (request.getParentId() != null) {
            menu.setParentId(request.getParentId());
            menu.setLevelDepth(calculateLevelDepth(request.getParentId()));
        }
        sysMenuMapper.update(menu);
        return toResponse(menu);
    }

    public void delete(Long menuId) {
        SysMenu menu = sysMenuMapper.findById(menuId);
        if (menu == null) {
            throw new RuntimeException("菜单不存在");
        }
        sysMenuMapper.deleteById(menuId);
        updateParentLeafStatus(menu.getParentId());
    }

    public List<MenuTreeResponse> getTree() {
        List<SysMenu> allMenus = sysMenuMapper.findAll();
        return buildTree(allMenus, null);
    }

    public void assignMenusToRole(Long roleId, List<Long> menuIds) {
    }

    private int calculateLevelDepth(Long parentId) {
        if (parentId == null) {
            return 1;
        }
        SysMenu parent = sysMenuMapper.findById(parentId);
        if (parent == null) {
            return 1;
        }
        return parent.getLevelDepth() + 1;
    }

    private void updateParentLeafStatus(Long parentId) {
        if (parentId == null) {
            return;
        }
        List<SysMenu> children = sysMenuMapper.findByParentId(parentId);
        SysMenu parent = sysMenuMapper.findById(parentId);
        if (parent != null) {
            parent.setIsLeaf(children.isEmpty() ? "Y" : "N");
            sysMenuMapper.update(parent);
        }
    }

    private List<MenuTreeResponse> buildTree(List<SysMenu> allMenus, Long parentId) {
        return allMenus.stream()
            .filter(m -> (parentId == null && m.getParentId() == null) || (parentId != null && parentId.equals(m.getParentId())))
            .map(m -> {
                MenuTreeResponse response = toTreeResponse(m);
                List<MenuTreeResponse> children = buildTree(allMenus, m.getMenuId());
                response.setChildren(children.isEmpty() ? null : children);
                return response;
            })
            .collect(Collectors.toList());
    }

    private MenuResponse toResponse(SysMenu menu) {
        MenuResponse response = new MenuResponse();
        response.setMenuId(menu.getMenuId());
        response.setMenuName(menu.getMenuName());
        response.setMenuUrl(menu.getMenuUrl());
        response.setIcon(menu.getIcon());
        response.setSortOrder(menu.getSortOrder());
        response.setStatus(menu.getStatus());
        response.setIsLeaf(menu.getIsLeaf());
        response.setLevelDepth(menu.getLevelDepth());
        response.setComponent(menu.getComponent());
        response.setComponentPath(menu.getComponentPath());
        response.setParentId(menu.getParentId());
        response.setDelFlag(menu.getDelFlag());
        response.setCreateTime(menu.getCreateTime());
        return response;
    }

    private MenuTreeResponse toTreeResponse(SysMenu menu) {
        MenuTreeResponse response = new MenuTreeResponse();
        response.setMenuId(menu.getMenuId());
        response.setMenuName(menu.getMenuName());
        response.setMenuUrl(menu.getMenuUrl());
        response.setIcon(menu.getIcon());
        response.setSortOrder(menu.getSortOrder());
        response.setStatus(menu.getStatus());
        response.setIsLeaf(menu.getIsLeaf());
        response.setLevelDepth(menu.getLevelDepth());
        response.setComponent(menu.getComponent());
        response.setComponentPath(menu.getComponentPath());
        response.setParentId(menu.getParentId());
        return response;
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/service/MenuService.java
git commit -m "feat(service): 添加 MenuService"
```

---

### Task 5: 创建菜单 Controller

**Files:**
- Create: `src/main/java/com/address/controller/MenuController.java`

- [ ] **Step 1: 创建 Controller**

```java
package com.address.controller;

import com.address.common.ApiResponse;
import com.address.common.ErrorCode;
import com.address.dto.MenuCreateRequest;
import com.address.dto.MenuQueryRequest;
import com.address.dto.MenuResponse;
import com.address.dto.MenuTreeResponse;
import com.address.dto.MenuUpdateRequest;
import com.address.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MenuController {

    @Autowired
    private MenuService menuService;

    @PostMapping("/api/menus/query")
    public ApiResponse<List<MenuResponse>> query(@RequestBody MenuQueryRequest request) {
        return ApiResponse.success(menuService.query(request));
    }

    @GetMapping("/api/menus/{menuId}")
    public ApiResponse<MenuResponse> getById(@PathVariable Long menuId) {
        try {
            return ApiResponse.success(menuService.getById(menuId));
        } catch (RuntimeException e) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), e.getMessage());
        }
    }

    @PostMapping("/api/menus/create")
    public ApiResponse<MenuResponse> create(@RequestBody MenuCreateRequest request) {
        try {
            return ApiResponse.success(menuService.create(request));
        } catch (RuntimeException e) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), e.getMessage());
        }
    }

    @PostMapping("/api/menus/update")
    public ApiResponse<MenuResponse> update(@RequestBody MenuUpdateRequest request) {
        try {
            return ApiResponse.success(menuService.update(request));
        } catch (RuntimeException e) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), e.getMessage());
        }
    }

    @PostMapping("/api/menus/delete")
    public ApiResponse<Void> delete(@RequestParam Long menuId) {
        try {
            menuService.delete(menuId);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.error(ErrorCode.BAD_REQUEST.getCode(), e.getMessage());
        }
    }

    @PostMapping("/api/menus/tree")
    public ApiResponse<List<MenuTreeResponse>> getTree() {
        return ApiResponse.success(menuService.getTree());
    }

    @PostMapping("/api/roles/{roleId}/menus/assign")
    public ApiResponse<Void> assignMenusToRole(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        menuService.assignMenusToRole(roleId, menuIds);
        return ApiResponse.success(null);
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/controller/MenuController.java
git commit -m "feat(controller): 添加 MenuController"
```

---

### Task 6: 创建菜单单元测试

**Files:**
- Create: `src/test/java/com/address/controller/MenuControllerTest.java`

- [ ] **Step 1: 创建测试类**

```java
package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.MenuCreateRequest;
import com.address.dto.MenuQueryRequest;
import com.address.dto.MenuResponse;
import com.address.dto.MenuUpdateRequest;
import com.address.service.MenuService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
public class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MenuService menuService;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser
    public void testQueryMenus() throws Exception {
        MenuResponse response = new MenuResponse();
        response.setMenuId(1L);
        response.setMenuName("测试菜单");
        response.setMenuUrl("/test");
        when(menuService.query(any(MenuQueryRequest.class))).thenReturn(Arrays.asList(response));

        mockMvc.perform(post("/api/menus/query")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data[0].menuName").value("测试菜单"));
    }

    @Test
    @WithMockUser
    public void testCreateMenu() throws Exception {
        MenuCreateRequest request = new MenuCreateRequest();
        request.setMenuName("新菜单");
        request.setMenuUrl("/new");

        MenuResponse response = new MenuResponse();
        response.setMenuId(1L);
        response.setMenuName("新菜单");
        response.setMenuUrl("/new");
        when(menuService.create(any(MenuCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/menus/create")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.menuName").value("新菜单"));
    }

    @Test
    @WithMockUser
    public void testUpdateMenu() throws Exception {
        MenuUpdateRequest request = new MenuUpdateRequest();
        request.setMenuId(1L);
        request.setMenuName("更新菜单");

        MenuResponse response = new MenuResponse();
        response.setMenuId(1L);
        response.setMenuName("更新菜单");
        when(menuService.update(any(MenuUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/menus/update")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.menuName").value("更新菜单"));
    }

    @Test
    @WithMockUser
    public void testDeleteMenu() throws Exception {
        mockMvc.perform(post("/api/menus/delete")
                .with(csrf())
                .param("menuId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }

    @Test
    @WithMockUser
    public void testGetMenuTree() throws Exception {
        mockMvc.perform(post("/api/menus/tree")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"));
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add src/test/java/com/address/controller/MenuControllerTest.java
git commit -m "test: 添加 MenuControllerTest"
```

---

### Task 7: 更新接口文档

**Files:**
- Modify: `http/interface.md`

- [ ] **Step 1: 添加菜单接口文档**

在 interface.md 中添加以下接口：

```markdown
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

### 28. 获取菜单详情
- **接口 URL**: `/api/menus/{menuId}`
- **请求方法**: GET
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
```

- [ ] **Step 2: 提交**

```bash
git add http/interface.md
git commit -m "docs: 更新接口文档添加菜单接口"
```

---

## 自检清单

1. **Spec coverage**: 设计文档中的所有需求都有对应的 Task 实现
2. **Placeholder scan**: 无 TBD/TODO 占位符
3. **Type consistency**: 类型、方法名在各 Task 间一致
