# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

**地址信息维护 + 权限管理系统（Java8 + Maven + Spring Boot）**

### 客户地址管理
- 一个客户可有多个地址，按类型区分（居住地址、联系地址、单位地址等）
- 每种类型地址只能有一个"最新地址"（isNewest='Y'）
- 只能有一个"通讯地址"（isMailingAddress='Y'），且通讯地址必然是最新的
- 通讯地址按优先级选取：其他地址 > 联系地址 > 居住地址 > 单位地址 > 户籍地址 > 证件地址 > 营业地址 > 注册地址 > 办公地址 > 永久地址

### 权限管理（Phase 16-17）
- 手机号 + JWT 无状态登录
- 角色 + 权限 + 数据范围配置
- 菜单管理（无限级层级结构）

## 常用命令

```bash
# Maven 编译
mvn compile

# 运行单个测试
mvn test -Dtest=测试类名

# 打包
mvn package
```

## Claude Code 命令执行规范 (绝对遵守)
### 禁止使用 cd 复合命令
**绝对禁止**生成类似 `cd <目录> && <命令>` 或 `cd <目录> ; <命令>` 的复合 Bash 命令。
这种格式会触发安全拦截机制，导致流程中断。
### Git 命令规范
- 当前工作目录始终被视为项目根目录，**不需要也不允许**使用 cd 切换目录。
- 直接执行 git 命令，例如：`git add .`、`git commit -m "xxx"`。
- 如果确实需要指定其他目录的 git 仓库，必须使用 git 自带的 `-C` 参数，例如：`git -C D:/other/repo status`。
### 其他命令规范
- 需要在特定目录执行脚本时，不要用 cd，直接传入绝对路径，例如：`node D:/AI/scripts/build.js`。

## 架构要点

### 核心算法（Service 层）
1. **合并阶段**：对上送数组和存量数组分别按"地址类型+地址详情"去重合并
2. **新增/更新标记**：上送数组中 seqNo 为空=新增，有值=更新
3. **地址规则应用**：挑选通讯地址和每种类型的最新地址
4. **批量入库**：新增地址生成 id 并 insert；更新地址按 seqNo update

### 数据模型
- 实体类 `CifAddress`：seqNo, clientNo, addressType, addressDetail, lastChangeDate, isMailingAddress, isNewest, del_flag
- 使用内存代替数据库，**不要使用 Map**

### 技术约束
- Java8 + Maven
- 不新建异常，使用 RuntimeException
- del_flag='Y' 的记录为逻辑删除，不参与业务规则

## 技术栈
- Spring Boot 2.7.18
- MyBatis 3.5.13
- MySQL + HikariCP
- JWT 无状态认证
- BCrypt 密码加密

## 已完成模块

### Phase 12-15: 地址管理
- RESTful 接口：PUT `/client/address/update`
- 分页查询：POST `/client/address/query`
- 单地址维护：POST `/client/address/single/update`

### Phase 16: 手机号登录
- JWT 无状态认证
- 角色 + 权限 + 数据范围管理
- 认证接口：POST `/api/auth/login`, `/api/auth/register`
- 用户管理：POST `/api/users/*`
- 角色管理：POST `/api/roles/*`

### Phase 17: 菜单管理
- 无限级层级结构（parentId 自引用）
- 软删除机制（del_flag='Y'）
- 接口：POST `/api/menus/*`

### Phase 18: SysUser 字段扩展
- 新增字段：userName, email, province, city, district, hobby

## 测试调试技巧
- 单独运行某类测试：`mvn test -Dtest=UserServiceTest`
- 过滤测试输出：`mvn test 2>&1 | grep -E "Tests run|BUILD"`
- 用 `git stash` 确认测试失败是否为预存问题
- 创建 DebugTableTest 用 JdbcTemplate 检查数据库表结构
- Controller 测试 403 错误通常是 Spring Security 配置问题，非代码修改导致

## 测试配置
- 测试数据库自动初始化：`spring.sql.init.mode=always`
- 初始化脚本位置：`src/test/resources/sql/schema.sql`
- 如需手动初始化表，在测试类中用 `jdbcTemplate.execute(CREATE TABLE...)`
