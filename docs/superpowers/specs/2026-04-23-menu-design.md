# 菜单管理模块设计

## 1. 需求概述

新建独立菜单模块，支持无限级层级结构，采用软删除机制，实现完整的增删改查功能。

## 2. 数据模型

### 2.1 数据库表 sys_menu

| 字段 | 类型 | 说明 |
|------|------|------|
| menu_id | Long | 主键，自增 |
| menu_name | String | 菜单名称（必填） |
| menu_url | String | 菜单 URL（必填） |
| icon | String | 图标（可选） |
| sort_order | Integer | 排序号（可选） |
| status | String | 状态（可选） |
| is_leaf | String | 是否叶子节点 Y/N（计算字段） |
| level_depth | Integer | 层级深度（计算字段） |
| component | String | 组件 |
| component_path | String | 组件所在目录 |
| parent_id | Long | 父菜单ID，null表示顶级 |
| del_flag | String | 软删除标记 Y/N，默认 N |
| create_time | LocalDateTime | 创建时间 |

### 2.2 实体类 SysMenu

```java
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
}
```

## 3. 接口设计

| 接口 | URL | 方法 | 说明 |
|------|-----|------|------|
| 菜单查询 | /api/menus/query | POST | 分页/条件查询 |
| 菜单详情 | /api/menus/{menuId} | GET | 获取单个菜单 |
| 菜单创建 | /api/menus/create | POST | 新增菜单 |
| 菜单更新 | /api/menus/update | POST | 更新菜单 |
| 菜单删除 | /api/menus/delete | POST | 软删除 |
| 获取树形菜单 | /api/menus/tree | POST | 获取完整菜单树 |
| 分配菜单权限 | /api/roles/{roleId}/menus/assign | POST | 角色分配菜单 |

### 3.1 请求/响应 DTO

**MenuQueryRequest**
- menuName, menuUrl, status, parentId（可选）
- pageNum, pageSize

**MenuCreateRequest**
- menuName, menuUrl（必填）
- icon, sortOrder, status, component, componentPath, parentId（可选）

**MenuUpdateRequest**
- menuId（必填）
- menuName, menuUrl, icon, sortOrder, status, component, componentPath, parentId（可选）

**MenuResponse**
- 包含所有菜单字段

**MenuTreeResponse**
- 树形结构，包含 children 子节点列表

## 4. 核心算法

### 4.1 层级深度计算
- 顶级菜单：levelDepth = 1
- 子菜单：levelDepth = parent.levelDepth + 1
- 查询时递归计算或存储时维护

### 4.2 叶子节点计算
- 根据是否存在子菜单自动计算
- 有子菜单 isLeaf='N'，无子菜单 isLeaf='Y'

### 4.3 软删除
- 删除时设置 del_flag='Y'
- 查询时自动过滤 del_flag='Y' 的记录

## 5. 四层架构

```
Controller层：MenuController
  - 参数校验
  - 调用 Service
  - 返回 ApiResponse

Service层：MenuService
  - 业务逻辑处理
  - 层级深度计算
  - 叶子节点计算

Repository层：MenuRepository
  - 内存数据存储
  - CRUD 操作
  - 树形结构查询

Model层：SysMenu
  - 实体类定义
  - Getter/Setter
```

## 6. 文件清单

| 层级 | 文件 |
|------|------|
| Model | SysMenu.java |
| Model | MenuCreateRequest.java |
| Model | MenuUpdateRequest.java |
| Model | MenuQueryRequest.java |
| Model | MenuResponse.java |
| Model | MenuTreeResponse.java |
| Repository | MenuRepository.java |
| Service | MenuService.java |
| Controller | MenuController.java |
| Test | MenuControllerTest.java |

## 7. 约束与规则

1. 菜单名称、菜单URL 为必填字段
2. 删除操作仅标记 del_flag='Y'，不物理删除
3. 顶级菜单 parentId 为 null
4. levelDepth 和 isLeaf 为计算字段，由系统维护
