# Progress

> 会话日志与测试结果

---

## 2026-04-20

### 会话开始
- 读取 REQUIREMENT.md，理解客户地址信息维护需求
- 确认交付形式：Java 类库/Service
- 确认集成接口：updateAddresses(clientNo, incomingAddresses) → List<CifAddress>
- 确认存量地址由本系统管理（内存存储）
- 确认 10 种地址类型

### 设计阶段
- 完成设计方案讨论（Repository 接口 + 策略模式）
- 写入设计文档：`docs/superpowers/specs/2026-04-20-client-address-design.md`
- 完成实施计划：`docs/superpowers/plans/2026-04-20-client-address-plan.md`

### 规划文件
- 创建 task_plan.md
- 创建 findings.md
- 创建 progress.md
- 追加任务步骤到 task_plan.md, findings.md, progress.md

---

## 任务执行记录

### Task 1: Maven 项目初始化
- 状态：已完成
- 提交：feat: 添加 CifAddress 实体类及基本测试

### Task 2: AddressType 枚举
- 状态：已完成
- 提交：feat: 添加 AddressType 枚举

### Task 3: Repository 层
- 状态：已完成
- 提交：feat: 添加 Repository 层接口及内存实现

### Task 4: 地址选择策略
- 状态：已完成
- 提交：feat: 添加地址选择策略接口及实现

### Task 5: 地址合并逻辑
- 状态：已完成
- 提交：feat: 添加 AddressMerger 地址合并逻辑

### Task 6: ClientAddressService 核心服务
- 状态：已完成
- 提交：feat: 添加 ClientAddressService 核心服务及集成测试

### Task 7: 异常处理
- 状态：已完成
- 提交：feat: 添加 AddressBusinessException 业务异常

### Task 8: Maven 打包验证
- 状态：已完成
- 提交：chore: 配置 Maven 打包
- 生成 jar：target/client-address-service-1.0.0.jar
- 测试结果：16 tests, 0 failures

### Task 9: 删除并重写 ClientAddressService
- 状态：已完成
- 提交：refactor: 简化 PriorityMailingAddressStrategy.select 实现
- 提交：refactor: 简化 PriorityMailingAddressStrategy，移除 for 循环中的 StreamAPI
- 提交：fix: 第8条优先级实现修正
- 提交：feat: 实现 PriorityMailingAddressStrategy.select 通讯地址选择逻辑

### Task 10: 修改 AddressMerger 支持标记删除
- 状态：已完成
- 提交：refactor: 简化 PriorityMailingAddressStrategy.select 实现

### Task 11: 验证和测试
- 状态：已完成
- 提交：feat: 实现 PriorityNewestAddressStrategy.selectByType 最新地址选择逻辑

---

## 2026-04-21

### seqNo 雪花算法实现

#### Task 12: 创建 SnowflakeIdGenerator 工具类
- 状态：已完成
- 提交：feat: 新增 SnowflakeIdGenerator 雪花算法 ID 生成器

#### Task 13: 修改 ClientAddressService 使用 SnowflakeIdGenerator
- 状态：已完成
- 提交：refactor: seqNo 生成改用 Snowflake 算法

### MySQL 持久层实现

#### Task 14: 添加 Maven 依赖
- 状态：已完成
- 提交：deps: 添加 MySQL 和 SnakeYAML 依赖

#### Task 15: 创建配置文件和加载工具
- 状态：已完成
- 提交：feat: 添加数据库配置和加载工具

#### Task 16: 创建 JdbcClientAddressRepository
- 状态：已完成
- 提交：feat: 添加 JdbcClientAddressRepository MySQL 实现

#### Task 17: 添加自动建表逻辑
- 状态：已完成（合并在 Task 16 中）

#### Task 18: 修改 ClientAddressService 使用 JdbcClientAddressRepository
- 状态：已完成
- 提交：refactor: ClientAddressService 支持便捷构造方法使用 JdbcClientAddressRepository

#### Task 19: 创建 JdbcClientAddressRepositoryTest
- 状态：已完成
- 提交：test: 添加 JdbcClientAddressRepository 测试

### HikariCP 连接池实现

#### Task 20: 添加 HikariCP Maven 依赖
- 状态：已完成
- 提交：deps: 添加 HikariCP 连接池依赖

#### Task 21: 更新配置文件
- 状态：已完成
- 提交：feat: 添加 HikariCP 连接池配置

#### Task 22: 修改 DbConfig 添加 DataSource
- 状态：已完成
- 提交：feat: DbConfig 添加 HikariCP DataSource

#### Task 23: 修改 JdbcClientAddressRepository 使用 DataSource
- 状态：已完成
- 提交：refactor: JdbcClientAddressRepository 使用 HikariCP DataSource

---

## 遇到的问题

| 问题 | 解决方案 |
|------|---------|
| Maven surefire 插件版本过旧导致 JUnit5 测试不执行 | 升级 maven-surefire-plugin 至 3.1.2 |
| PriorityMailingAddressStrategy 使用 type.name() 而非 type.getCode() | 修改为使用 getCode() 匹配 addressType |
| 测试数据中修改时间与预期优先级不匹配 | 调整测试数据使时间与优先级顺序一致 |
| testUpdateExistingAddress 失败：返回2条而非1条 | 给已存在的地址设置 seqNo 以便识别 |

---

## Phase 9: MyBatis 迁移实现

### Task 1: 添加 MyBatis Maven 依赖
- 状态：已完成
- 提交：feat: 添加 MyBatis 依赖

### Task 2: 创建 mybatis-config.xml
- 状态：已完成
- 提交：feat: 添加 MyBatis 配置文件

### Task 3: 创建 CifAddressMapper.xml
- 状态：已完成
- 提交：feat: 添加 CifAddressMapper.xml

### Task 4: 创建 CifAddressMapper.java
- 状态：已完成
- 提交：feat: 添加 CifAddressMapper 接口

### Task 5: 创建 MyBatisConfig.java
- 状态：已完成
- 提交：feat: 添加 MyBatisConfig 配置类

### Task 6: 创建 MyBatisClientAddressRepository.java
- 状态：已完成
- 提交：feat: 添加 MyBatisClientAddressRepository 实现

### Task 7: 创建 MyBatisClientAddressRepositoryTest
- 状态：已完成
- 提交：test: 添加 MyBatisClientAddressRepositoryTest

### Task 8: 完整测试验证
- 状态：已完成
- 提交：feat: 完成 MyBatis 迁移
- 测试结果：26 tests, 0 failures

---

## Phase 10: 日志记录功能实现

### Task 1: 添加 Maven 依赖
- 状态：已完成

### Task 2: 创建 logback.xml
- 状态：已完成

### Task 3: 修改 MyBatisClientAddressRepository 添加日志
- 状态：已完成

### Task 4: 修改 JdbcClientAddressRepository 添加日志
- 状态：已完成

### Task 5: 验证日志输出
- 状态：已完成

---

## Phase 11: Spring Boot 集成实现

### Task 1: pom.xml 添加 Spring Boot 依赖
- 状态：已完成

### Task 2: 创建 Spring Boot 启动类
- 状态：已完成

### Task 3: 创建 application.yml 配置
- 状态：已完成

### Task 4: 创建 JdbcTemplateClientAddressRepository
- 状态：已完成

### Task 5: 创建 JdbcClientClientAddressRepository
- 状态：跳过（JdbcClient 需要 Spring Boot 3.x + Java 17）

### Task 6: 配置 @Primary 并更新 ClientAddressService
- 状态：已完成

### Task 7: 验证测试
- 状态：已完成（26 tests, 0 failures）

---

## Phase 12: RESTful 接口实现

### Task 1: 新建 ApiResponse 统一响应类
- 状态：已完成

### Task 2: 新建 ErrorCode 错误码枚举
- 状态：已完成

### Task 3: 新建 AddressUpdateRequest 请求 DTO
- 状态：已完成

### Task 4: 新建 ClientAddressController
- 状态：已完成

### Task 5: 修改 Application 添加 Controller 扫描
- 状态：已完成（@SpringBootApplication 默认 @ComponentScan，无需修改）

---

## 提交记录

- 00cfa12: test: 验证 Spring Boot 集成测试通过
- 974e274: feat: 配置 @Primary 并更新 ClientAddressService 注入
- 2428d2d: feat: 新增 JdbcTemplateClientAddressRepository
- 4b99888: feat: 添加 Spring Boot 配置文件 application.yml
- 39141d4: feat: 创建 Spring Boot 启动类 Application
- 69c2f5f: feat: 引入 Spring Boot 2.7.18 依赖

---

## Phase 13: 地址查询接口实现

### Task 1: 创建 AddressQueryRequest DTO
- 状态：已完成

### Task 2: 创建 PageResult DTO
- 状态：已完成

### Task 3: 创建 AddressQueryResponse DTO
- 状态：已完成

### Task 4: 创建 AddressQueryRepository 接口
- 状态：已完成

### Task 5: 修改 CifAddressMapper 添加分页方法
- 状态：已完成

### Task 6: 修改 CifAddressMapper.xml 添加分页 SQL
- 状态：已完成

### Task 7: 创建 MyBatisAddressQueryRepository 实现
- 状态：已完成

### Task 8: 创建 ClientAddressQueryService
- 状态：已完成

### Task 9: 创建 ClientAddressQueryController
- 状态：已完成

### Task 10: 创建 ClientAddressQueryControllerTest
- 状态：已完成

### Task 11: 创建 ClientAddressQueryServiceTest
- 状态：已完成

### Task 12: 最终验证
- 状态：已完成（40 tests, 0 failures）

---

## Phase 14: UI 页面实现

### Task 1: 创建基础 HTML 结构和样式
- 状态：已完成
- 提交：feat(ui): 完成客户地址管理页面实现

### Task 2: 实现 JavaScript 变量和 DOM 元素定义
- 状态：已完成
- 提交：feat(ui): 完成客户地址管理页面实现

### Task 3: 实现 API 调用函数
- 状态：已完成
- 提交：feat(ui): 完成客户地址管理页面实现

### Task 4: 实现渲染和分页逻辑
- 状态：已完成
- 提交：feat(ui): 完成客户地址管理页面实现

### Task 5: 实现事件绑定
- 状态：已完成
- 提交：feat(ui): 完成客户地址管理页面实现

### Task 6: 最终验证
- 状态：已完成
- 提交：feat(ui): 完成客户地址管理页面实现

- 67b23da: refactor: 完善 CifAddressMapper.xml 使用标准 MyBatis 规范
- 3e39750: docs: 更新 task_plan.md Phase 9 完成状态
- 02ed9d2: feat: 完成 MyBatis 迁移
- a4c4feb: refactor: seqNo 生成改用 Snowflake 算法
- b766de1: feat: 新增 SnowflakeIdGenerator 雪花算法 ID 生成器
- c42081c: docs: 添加 seqNo 雪花算法实现计划
- 41bb7ab: docs: 添加 seqNo 雪花算法设计方案
- 0466a46: refactor: 抽取魔法值为 Constants 接口统一管理
- 8495a27: docs: 同步规划文件，标记所有任务完成
- c44850b: feat: 实现 PriorityNewestAddressStrategy.selectByType 最新地址选择逻辑
- 55148b4: refactor: PriorityNewestAddressStrategy.selectByType 改为空实现
- ec07053: refactor: 简化 PriorityMailingAddressStrategy.select 实现
- 0c5decf: refactor: 简化 PriorityMailingAddressStrategy，移除 for 循环中的 StreamAPI
- bb76ed4: fix: 第8条优先级实现修正
- f514a5e: feat: 实现 PriorityMailingAddressStrategy.select 通讯地址选择逻辑
- c20cc7b: chore: 创建实施计划文件
- b0f9625: feat: 添加 CifAddress 实体类及基本测试
- 53ce2f4: feat: 添加 AddressType 枚举

---

## Phase 18: SysUser 字段扩展实施记录

### Task 1: 修改 SysUser 实体
- [x] Step 1: 修改实体类，添加新字段
- [x] Step 2: 添加 getter/setter 方法
- [x] Step 3: 提交

### Task 2: 修改 SysUserMapper
- [x] Step 1: 修改 insert 语句
- [x] Step 2: 修改 update 语句
- [x] Step 3: 修改 findById 查询
- [x] Step 4: 修改 findByPhone 查询
- [x] Step 5: 修改 findAll 查询
- [x] Step 6: 提交

### Task 3: 修改 UserCreateRequest
- [x] Step 1: 添加新字段
- [x] Step 2: 添加 getter/setter
- [x] Step 3: 提交

### Task 4: 修改 UserUpdateRequest
- [x] Step 1: 添加新字段
- [x] Step 2: 添加 getter/setter
- [x] Step 3: 提交

### Task 5: 修改 UserResponse
- [x] Step 1: 添加新字段
- [x] Step 2: 添加 getter/setter
- [x] Step 3: 提交

### Task 6: 修改 UserService
- [x] Step 1: 修改 toResponse 方法
- [x] Step 2: 提交

### Task 7: 修改 sql/sys_user.sql
- [x] Step 1: 更新建表语句
- [x] Step 2: 提交

### Task 8: 编译验证
- [x] Step 1: 运行编译
- [x] Step 2: 运行测试

### 提交记录
- 8e1ee86: feat(model): SysUser 新增 userName, email, province, city, district, hobby 字段
- eae3b70: feat(mapper): SysUserMapper 新增字段映射
- c78a074: feat(dto): UserCreateRequest 新增字段
- 137ebf3: feat(dto): UserUpdateRequest 新增字段
- 7fec307: feat(dto): UserResponse 新增字段
- d04cc18: feat(service): UserService 新增字段映射
- 5eef839: feat(sql): sys_user 表新增字段
- 90164d8: feat(test): 添加测试配置支持 sys_user 新字段

### 备注
- Controller 测试失败（403）是之前就存在的 Spring Security 配置问题，与本次修改无关
- UserServiceTest 测试通过，证明 sys_user 新字段修改正确

---

## Phase 19: 登录接口返回码归类实施记录

### 任务步骤

#### Task 1: 创建 AuthErrorCode 错误码常量类
- [ ] Step 1: 创建文件
- [ ] Step 2: 验证编译
- [ ] Step 3: 提交

#### Task 2: 创建 LoginResult 认证结果封装类
- [ ] Step 1: 创建文件
- [ ] Step 2: 验证编译
- [ ] Step 3: 提交

#### Task 3: 修改 ApiResponse 支持自定义成功码
- [ ] Step 1: 添加 success(data, code) 方法
- [ ] Step 2: 验证编译
- [ ] Step 3: 提交

#### Task 4: 修改 AuthService.login() 返回 LoginResult
- [ ] Step 1: 修改 login 方法返回类型和实现
- [ ] Step 2: 添加必要的 import
- [ ] Step 3: 验证编译
- [ ] Step 4: 提交

#### Task 5: 修改 AuthController.login() 处理 LoginResult
- [ ] Step 1: 修改 login 方法
- [ ] Step 2: 添加必要的 import
- [ ] Step 3: 验证编译
- [ ] Step 4: 提交

#### Task 6: 运行测试验证
- [ ] Step 1: 运行 AuthServiceTest
- [ ] Step 2: 运行完整测试套件
- [ ] Step 3: 提交测试相关修改（如有）

### 提交记录
- （实施中）

---

## Phase 15: 单地址维护接口实现

### Task 1: 创建 DTO
- [x] 已完成

### Task 2: Service 层添加业务方法
- [x] 已完成

### Task 3: Controller 层添加接口
- [x] 已完成

### Task 4: 编译验证
- [x] 已完成

### 提交记录
- 91292f7: feat(ui): 编辑和删除改用单地址更新接口
- 62e0def: fix: updateSingleAddress 调用合并逻辑重新计算地址标识
- fde1c22: refactor: findBySeqNo 方法入参加上 clientNo
- 1448821: refactor: updateSingleAddress 改用 seqNo 直接查询
- e377d20: feat: 添加单地址更新接口 /client/address/single/update
- 3a6714d: feat: 添加 Repository 层接口及内存实现
- 9294fe2: feat: 添加地址选择策略接口及实现
- ee24e44: feat: 添加 AddressMerger 地址合并逻辑
- 4dddfe3: feat: 添加 ClientAddressService 核心服务及集成测试
- 609968e: fix: 修复 ClientAddressServiceTest 测试
- cfab2ae: feat: 添加 AddressBusinessException 业务异常
- 1c4df40: chore: 添加 CLAUDE.md 代码库指南

---

## Phase 16: 手机号登录功能实现

### 2026-04-23 设计阶段

#### 需求收集
- 用户用手机号登录功能
- 建立角色配置表，角色可配
- 角色+权限控制+数据范围
- 认证+完整信息：验证通过后返回用户完整信息，包括关联的角色、权限、数据范围
- 密码用户自设
- 用户获取方式：自注册和管理员创建
- 接口统一使用 POST
- 复用现有 ApiResponse 统一响应格式

#### 设计文档
- 完成设计文档：`docs/superpowers/specs/2026-04-23-login-design.md`
- 完成实现计划：`docs/superpowers/plans/2026-04-23-login-plan.md`

#### 规划文件同步
- task_plan.md：添加 Phase 16 任务步骤
- findings.md：添加登录功能设计详情
- progress.md：添加登录功能实施记录

### 实施计划

#### Task 1: 添加项目依赖
- [ ] Step 1: 添加 JJWT 和 Spring Security 依赖到 pom.xml
- [ ] Step 2: 提交

#### Task 2: 创建用户相关实体和 Mapper
- [ ] Step 1-6: 创建 SysUser, SysRole, SysUserRole 实体和 Mapper
- [ ] Step 7: 提交

#### Task 3: 创建权限和数据范围相关实体和 Mapper
- [ ] Step 1-8: 创建权限和数据范围相关实体和 Mapper
- [ ] Step 9: 提交

#### Task 4: 创建 JWT 工具类和安全配置
- [ ] Step 1-4: 创建 JwtUtil, JwtAuthenticationFilter, UserDetailsServiceImpl, SecurityConfig
- [ ] Step 5: 提交

#### Task 5: 创建认证 DTO 和 Service
- [ ] Step 1-4: 创建 LoginRequest, LoginResponse, RegisterRequest, AuthService
- [ ] Step 5: 提交

#### Task 6: 创建用户管理 DTO、Service、Controller
- [ ] Step 1-6: 创建用户管理相关类
- [ ] Step 7: 提交

#### Task 7: 创建角色管理 DTO、Service、Controller
- [ ] Step 1-6: 创建角色管理相关类
- [ ] Step 7: 提交

#### Task 8: 创建权限和数据范围管理 DTO、Service、Controller
- [ ] Step 1-10: 创建权限和数据范围管理相关类
- [ ] Step 11: 提交

#### Task 9: 创建 AuthController 和配置
- [ ] Step 1-3: 创建 AuthController 和配置
- [ ] Step 4: 提交

#### Task 10: 创建数据库表结构 SQL
- [ ] Step 1-7: 创建 SQL 脚本
- [ ] Step 8: 提交

---

## Phase 17: 菜单管理模块实现

### 2026-04-23 设计阶段

#### 需求收集
- 新建独立菜单模块 sys_menu
- 支持无限级层级结构（parentId 自引用）
- 软删除机制（del_flag='Y'）
- 字段：菜单ID、菜单名称、菜单URL（必填）、图标、排序号、状态（可选）、是否叶子节点、层级深度（计算字段）、组件、组件所在目录

#### 设计文档
- 完成设计文档：`docs/superpowers/specs/2026-04-23-menu-design.md`
- 完成实现计划：`docs/superpowers/plans/2026-04-23-menu-implementation.md`

#### 规划文件同步
- task_plan.md：添加 Phase 17 任务步骤
- findings.md：添加菜单模块设计详情
- progress.md：添加菜单模块实施记录

### 实施计划

#### Task 1: 创建菜单实体 SysMenu
- [ ] Step 1: 创建 src/main/java/com/address/model/SysMenu.java
- [ ] Step 2: 提交

#### Task 2: 创建菜单相关 DTO
- [ ] Step 1: 创建 MenuCreateRequest.java
- [ ] Step 2: 创建 MenuUpdateRequest.java
- [ ] Step 3: 创建 MenuQueryRequest.java
- [ ] Step 4: 创建 MenuResponse.java
- [ ] Step 5: 创建 MenuTreeResponse.java
- [ ] Step 6: 提交

#### Task 3: 创建菜单 Mapper
- [ ] Step 1: 创建 src/main/java/com/address/repository/SysMenuMapper.java
- [ ] Step 2: 提交

#### Task 4: 创建菜单 Service
- [ ] Step 1: 创建 src/main/java/com/address/service/MenuService.java
- [ ] Step 2: 提交

#### Task 5: 创建菜单 Controller
- [ ] Step 1: 创建 src/main/java/com/address/controller/MenuController.java
- [ ] Step 2: 提交

#### Task 6: 创建菜单单元测试
- [ ] Step 1: 创建 src/test/java/com/address/controller/MenuControllerTest.java
- [ ] Step 2: 提交

#### Task 7: 更新接口文档
- [ ] Step 1: 在 http/interface.md 中添加菜单接口
- [ ] Step 2: 提交
