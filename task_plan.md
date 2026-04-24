# Task Plan

> 项目：客户地址信息维护类库
> 目标：实现客户地址信息维护功能（地址合并、通讯地址选择、最新地址选择）
> 架构：Model → Repository → Strategy → Service 分层设计
> 技术栈：Java8 + Maven

---

## 阶段与进度

### Phase 1: 基础设施
- [x] Task 1: Maven 项目初始化
  - [x] 创建 pom.xml
  - [x] 创建 CifAddress.java
  - [x] 创建 CifAddressTest.java
  - [x] 运行测试验证
  - [x] 提交

- [x] Task 2: AddressType 枚举
  - [x] 创建 AddressType.java
  - [x] 创建 AddressTypeTest.java
  - [x] 运行测试验证
  - [x] 提交

### Phase 2: 存储层
- [x] Task 3: Repository 层
  - [x] 创建 ClientAddressRepository.java
  - [x] 创建 MemoryClientAddressRepository.java
  - [x] 创建 MemoryClientAddressRepositoryTest.java
  - [x] 运行测试验证
  - [x] 提交

### Phase 3: 策略层
- [x] Task 4: 地址选择策略
  - [x] 创建 MailingAddressStrategy.java
  - [x] 创建 NewestAddressStrategy.java
  - [x] 创建 PriorityMailingAddressStrategy.java
  - [x] 创建 PriorityNewestAddressStrategy.java
  - [x] 创建 PriorityMailingAddressStrategyTest.java
  - [x] 创建 PriorityNewestAddressStrategyTest.java
  - [x] 运行测试验证
  - [x] 提交

### Phase 4: 业务层
- [x] Task 5: 地址合并逻辑
  - [x] 创建 AddressMerger.java
  - [x] 创建 AddressMergerTest.java
  - [x] 运行测试验证
  - [x] 提交

- [x] Task 6: ClientAddressService 核心服务
  - [x] 创建 ClientAddressService.java
  - [x] 创建 ClientAddressServiceTest.java
  - [x] 创建 ClientAddressServiceIntegrationTest.java
  - [x] 运行测试验证
  - [x] 提交

### Phase 5: 收尾
- [x] Task 7: 异常处理
  - [x] 创建 AddressBusinessException.java
  - [x] 运行测试验证
  - [x] 提交

- [x] Task 8: Maven 打包验证
  - [x] 更新 pom.xml
  - [x] 运行打包验证
  - [x] 运行全部测试
  - [x] 提交

---

## 实施检查清单

- [x] Task 1: Maven 项目初始化
- [x] Task 2: AddressType 枚举
- [x] Task 3: Repository 层
- [x] Task 4: 地址选择策略
- [x] Task 5: 地址合并逻辑
- [x] Task 6: ClientAddressService 核心服务
- [x] Task 7: 异常处理
- [x] Task 8: Maven 打包验证

---

## 重构阶段

### Task 9: 删除并重写 ClientAddressService

- [x] Step 1: 删除现有 ClientAddressService.java
- [x] Step 2: 按设计 11 步流程重写

### Task 10: 修改 AddressMerger 支持标记删除

- [x] Step 1: 确保 mergeStock 支持 del_flag 标记删除

### Task 11: 验证和测试

- [x] Step 1: 运行 Maven 测试
- [x] Step 2: 检查所有测试是否通过
- [x] Step 3: 提交变更

---

## 当前阶段

### ✅ 所有 Phase 已完成

#### Task 12: 创建 SnowflakeIdGenerator 工具类

- [x] Step 1: 编写测试用例
- [x] Step 2: 运行测试确认失败
- [x] Step 3: 实现 SnowflakeIdGenerator
- [x] Step 4: 运行测试确认通过
- [x] Step 5: 提交

#### Task 13: 修改 ClientAddressService 使用 SnowflakeIdGenerator

- [x] Step 1: 添加 import
- [x] Step 2: 修改 generateId 方法
- [x] Step 3: 运行所有测试确认通过
- [x] Step 4: 提交

---

---

## Phase 7: MySQL 持久层实现

#### Task 14: 添加 Maven 依赖

- [x] Step 1: 添加 MySQL 和 YAML 依赖
- [x] Step 2: 验证依赖下载
- [x] Step 3: 提交

#### Task 15: 创建配置文件和加载工具

- [x] Step 1: 创建 config.yaml
- [x] Step 2: 创建 DbConfig.java
- [x] Step 3: 验证配置加载
- [x] Step 4: 提交

#### Task 16: 创建 JdbcClientAddressRepository

- [x] Step 1: 实现 JdbcClientAddressRepository
- [x] Step 2: 验证编译
- [x] Step 3: 提交

#### Task 17: 添加自动建表逻辑

- [x] Step 1: 在 JdbcClientAddressRepository 中添加建表逻辑
- [x] Step 2: 验证编译
- [x] Step 3: 提交

#### Task 18: 修改 ClientAddressService 使用 JdbcClientAddressRepository

- [x] Step 1: 修改构造函数注入
- [x] Step 2: 验证编译
- [x] Step 3: 提交

#### Task 19: 创建 JdbcClientAddressRepositoryTest

- [x] Step 1: 编写测试用例
- [x] Step 2: 运行测试
- [x] Step 3: 提交

---

---

## Phase 8: HikariCP 连接池实现

#### Task 20: 添加 HikariCP Maven 依赖

- [x] Step 1: 添加 HikariCP 依赖
- [x] Step 2: 验证依赖下载
- [x] Step 3: 提交

#### Task 21: 更新配置文件

- [x] Step 1: 添加 HikariCP 连接池配置
- [x] Step 2: 提交

#### Task 22: 修改 DbConfig 添加 DataSource

- [x] Step 1: 添加 HikariCP import 和 DataSource
- [x] Step 2: 验证编译
- [x] Step 3: 提交

#### Task 23: 修改 JdbcClientAddressRepository 使用 DataSource

- [x] Step 1: 修改 getConnection 方法
- [x] Step 2: 修改 createTableIfNotExists 中的连接获取
- [x] Step 3: 验证编译
- [x] Step 4: 运行测试
- [x] Step 5: 提交

---

所有任务已完成 ✅

---

## Phase 9: MyBatis 迁移实现

### Task 1: 添加 MyBatis Maven 依赖

- [x] Step 1: 在 pom.xml 添加 mybatis 和 mybatis-spring 依赖
- [x] Step 2: 验证依赖添加成功
- [x] Step 3: 提交

### Task 2: 创建 mybatis-config.xml

- [x] Step 1: 创建 mybatis-config.xml
- [x] Step 2: 验证配置文件格式
- [x] Step 3: 提交

### Task 3: 创建 CifAddressMapper.xml

- [x] Step 1: 创建 mapper 目录
- [x] Step 2: 创建 CifAddressMapper.xml
- [x] Step 3: 验证 XML 格式
- [x] Step 4: 提交

### Task 4: 创建 CifAddressMapper.java

- [x] Step 1: 创建 CifAddressMapper.java（使用 @Update 注解实现 delete）
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 5: 创建 MyBatisConfig.java

- [x] Step 1: 创建 MyBatisConfig.java（初始化 SqlSessionFactory）
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 6: 创建 MyBatisClientAddressRepository.java

- [x] Step 1: 创建 MyBatisClientAddressRepository.java
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 7: 创建 MyBatisClientAddressRepositoryTest

- [x] Step 1: 查看 JdbcClientAddressRepositoryTest 作为参考
- [x] Step 2: 创建 MyBatisClientAddressRepositoryTest
- [x] Step 3: 运行测试验证
- [x] Step 4: 提交

### Task 8: 完整测试验证

- [x] Step 1: 运行所有测试
- [x] Step 2: 提交

---

## Phase 10: 日志记录功能实现

### Task 1: 添加 Maven 依赖

- [x] Step 1: 在 pom.xml 添加 slf4j-api 和 logback-classic 依赖
- [x] Step 2: 验证依赖下载
- [x] Step 3: 提交

### Task 2: 创建 logback.xml

- [x] Step 1: 创建 src/main/resources/logback.xml
- [x] Step 2: 提交

### Task 3: 修改 MyBatisClientAddressRepository 添加日志

- [x] Step 1: 添加 Logger 和日志语句
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 4: 修改 JdbcClientAddressRepository 添加日志

- [x] Step 1: 添加 Logger 和日志语句
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 5: 验证日志输出

- [x] Step 1: 运行测试
- [x] Step 2: 检查日志文件
- [x] Step 3: 提交

---

## Phase 11: Spring Boot 集成实现

### Task 1: pom.xml 添加 Spring Boot 依赖

- [x] Step 1: 修改 pom.xml，添加 Spring Boot 2.7.18 parent 和依赖
- [x] Step 2: mvn compile 验证编译
- [x] Step 3: git commit

### Task 2: 创建 Spring Boot 启动类

- [x] Step 1: 创建 Application.java（@SpringBootApplication, @MapperScan）
- [x] Step 2: mvn compile 验证编译
- [x] Step 3: git commit

### Task 3: 创建 application.yml 配置

- [x] Step 1: 创建 application.yml（DataSource + MyBatis 配置）
- [x] Step 2: mvn compile 验证编译
- [x] Step 3: git commit

### Task 4: 创建 JdbcTemplateClientAddressRepository

- [x] Step 1: 创建 JdbcTemplateClientAddressRepository.java
- [x] Step 2: mvn compile 验证编译
- [x] Step 3: git commit

### Task 5: 创建 JdbcClientClientAddressRepository

- [x] Step 1: 创建 JdbcClientClientAddressRepository.java（跳过 - JdbcClient 需要 Spring Boot 3.x + Java 17）
- [x] Step 2: mvn compile 验证编译
- [x] Step 3: git commit

### Task 6: 配置 @Primary 并更新 ClientAddressService

- [x] Step 1: 在 JdbcTemplateClientAddressRepository 添加 @Primary
- [x] Step 2: 更新 ClientAddressService 使用 @Autowired 注入
- [x] Step 3: mvn compile 验证编译
- [x] Step 4: git commit

### Task 7: 验证测试

- [x] Step 1: mvn test 运行所有测试
- [x] Step 2: git commit

---

## Phase 12: RESTful 接口实现

### Task 1: 新建 ApiResponse 统一响应类

- [x] Step 1: 编写测试（ApiResponseTest）
- [x] Step 2: 运行测试验证失败（class not found）
- [x] Step 3: 编写最小实现（ApiResponse.java）
- [x] Step 4: 运行测试验证通过
- [x] Step 5: 提交

### Task 2: 新建 ErrorCode 错误码枚举

- [x] Step 1: 编写最小实现（ErrorCode.java）
- [x] Step 2: 提交

### Task 3: 新建 AddressUpdateRequest 请求 DTO

- [x] Step 1: 编写最小实现（AddressUpdateRequest.java）
- [x] Step 2: 提交

### Task 4: 新建 ClientAddressController

- [x] Step 1: 编写测试（ClientAddressControllerTest）
- [x] Step 2: 运行测试验证通过
- [x] Step 3: 编写 Controller 实现
- [x] Step 4: 运行测试验证通过
- [x] Step 5: 提交

### Task 5: 修改 Application 添加 Controller 扫描

- [x] Step 1: 添加 @ComponentScan
- [x] Step 2: 运行测试验证
- [x] Step 3: 提交

### Task 6: 端到端集成测试（可选）

- [ ] Step 1: 创建 ClientAddressControllerIntegrationTest（如需要）

---

## Phase 13: 地址查询接口实现

### Task 1: 创建 AddressQueryRequest DTO

- [x] Step 1: 创建 AddressQueryRequest.java
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 2: 创建 PageResult DTO

- [x] Step 1: 创建 PageResult.java
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 3: 创建 AddressQueryResponse DTO

- [x] Step 1: 创建 AddressQueryResponse.java
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 4: 创建 AddressQueryRepository 接口

- [x] Step 1: 创建 AddressQueryRepository.java
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 5: 修改 CifAddressMapper 添加分页方法

- [x] Step 1: 添加 findPage 和 countPage 方法
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 6: 修改 CifAddressMapper.xml 添加分页 SQL

- [x] Step 1: 添加 findPage 和 countPage SQL
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 7: 创建 MyBatisAddressQueryRepository 实现

- [x] Step 1: 创建 MyBatisAddressQueryRepository.java
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 8: 创建 ClientAddressQueryService

- [x] Step 1: 创建 ClientAddressQueryService.java
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 9: 创建 ClientAddressQueryController

- [x] Step 1: 创建 ClientAddressQueryController.java
- [x] Step 2: 编译验证
- [x] Step 3: 提交

### Task 10: 创建 ClientAddressQueryControllerTest

- [x] Step 1: 创建 ClientAddressQueryControllerTest.java
- [x] Step 2: 运行测试
- [x] Step 3: 提交

### Task 11: 创建 ClientAddressQueryServiceTest

- [x] Step 1: 创建 ClientAddressQueryServiceTest.java
- [x] Step 2: 运行测试
- [x] Step 3: 提交

### Task 12: 最终验证

- [x] Step 1: 运行全部测试
- [x] Step 2: 检查 git 状态

---

## 遇到的问题

| 问题 | 解决方案 |
|------|---------|
| Maven surefire 插件版本过旧导致 JUnit5 测试不执行 | 升级 maven-surefire-plugin 至 3.1.2 |
| PriorityMailingAddressStrategy 使用 type.name() 而非 type.getCode() | 修改为使用 getCode() 匹配 addressType |
| 测试数据中修改时间与预期优先级不匹配 | 调整测试数据使时间与优先级顺序一致 |
| testUpdateExistingAddress 失败：返回2条而非1条 | 给已存在的地址设置 seqNo 以便识别 |
| logback 1.4.11 与 Spring Boot 2.7.18 冲突 | 移除显式版本，使用 Spring Boot 管理的版本 |
| JdbcClient API 仅在 Spring Boot 3.x 支持 | 跳过 JdbcClient 实现 |

---

## Phase 14: UI 页面实现

### Task 1: 创建基础 HTML 结构和样式

- [x] Step 1: 创建 HTML 基础结构
- [x] Step 2: 添加 CSS 样式
- [x] Step 3: 提交

### Task 2: 实现 JavaScript 变量和 DOM 元素定义

- [x] Step 1: 在 `<script>` 标签开头添加变量定义和元素获取
- [x] Step 2: 提交

### Task 3: 实现 API 调用函数

- [x] Step 1: 在变量定义后添加 API 调用函数
- [x] Step 2: 提交

### Task 4: 实现渲染和分页逻辑

- [x] Step 1: 添加渲染和分页函数
- [x] Step 2: 提交

### Task 5: 实现事件绑定

- [x] Step 1: 添加事件绑定函数
- [x] Step 2: 在文件末尾添加初始化调用
- [x] Step 3: 提交

### Task 6: 最终验证

- [x] Step 1: 检查文件完整性
- [x] Step 2: 提交

---

## Phase 15: 单地址维护接口实现

### Task 1: 创建 DTO

- [x] Step 1: 创建 SingleAddressRequest.java
- [x] Step 2: 创建 SingleAddressResponse.java
- [x] Step 3: 提交

### Task 2: Service 层添加业务方法

- [x] Step 1: 添加 updateSingleAddress 方法到 ClientAddressService.java
- [x] Step 2: 添加 import
- [x] Step 3: 提交

### Task 3: Controller 层添加接口

- [x] Step 1: 添加 import
- [x] Step 2: 添加 /client/address/single/update 接口方法
- [x] Step 3: 提交

### Task 4: 编译验证

- [x] Step 1: mvn compile 编译项目
- [x] Step 2: mvn test 运行测试

---

## ✅ Phase 15 已完成

- 单地址更新接口 `/client/address/single/update` 已实现
- 前端编辑和删除功能已对接新接口

---

## Phase 16: 手机号登录功能实现

> 目标：为客户地址管理系统增加基于手机号+密码的登录认证功能，支持角色+权限控制+数据范围配置
> 架构：采用 JWT 无状态认证，复用项目已有的 SnowflakeIdGenerator 生成 ID，密码 BCrypt 加密存储，数据模型基于 7 张表实现用户、角色、权限、数据范围的关联配置
> 技术栈：Spring Boot 2.7.18 + MyBatis + MySQL + JJWT + BCrypt

### Task 1: 添加项目依赖

- [x] Step 1: 添加 JJWT 和 Spring Security 依赖到 pom.xml
- [x] Step 2: 提交

### Task 2: 创建用户相关实体和 Mapper

- [x] Step 1: 创建 SysUser.java 实体
- [x] Step 2: 创建 SysRole.java 实体
- [x] Step 3: 创建 SysUserRole.java 实体
- [x] Step 4: 创建 SysUserMapper.java 接口
- [x] Step 5: 创建 SysRoleMapper.java 接口
- [x] Step 6: 创建 SysUserRoleMapper.java 接口
- [x] Step 7: 提交

### Task 3: 创建权限和数据范围相关实体和 Mapper

- [x] Step 1: 创建 SysPermission.java 实体
- [x] Step 2: 创建 SysRolePermission.java 实体
- [x] Step 3: 创建 SysDataScope.java 实体
- [x] Step 4: 创建 SysRoleDataScope.java 实体
- [x] Step 5: 创建 SysPermissionMapper.java 接口
- [x] Step 6: 创建 SysRolePermissionMapper.java 接口
- [x] Step 7: 创建 SysDataScopeMapper.java 接口
- [x] Step 8: 创建 SysRoleDataScopeMapper.java 接口
- [x] Step 9: 提交

### Task 4: 创建 JWT 工具类和安全配置

- [x] Step 1: 创建 JwtUtil.java 工具类
- [x] Step 2: 创建 JwtAuthenticationFilter.java 过滤器
- [x] Step 3: 创建 UserDetailsServiceImpl.java 用户服务
- [x] Step 4: 创建 SecurityConfig.java 安全配置
- [x] Step 5: 提交

### Task 5: 创建认证 DTO 和 Service

- [x] Step 1: 创建 LoginRequest.java
- [x] Step 2: 创建 LoginResponse.java
- [x] Step 3: 创建 RegisterRequest.java
- [x] Step 4: 创建 AuthService.java
- [x] Step 5: 提交

### Task 6: 创建用户管理 DTO、Service、Controller

- [x] Step 1: 创建 UserQueryRequest.java
- [x] Step 2: 创建 UserCreateRequest.java
- [x] Step 3: 创建 UserUpdateRequest.java
- [x] Step 4: 创建 UserResponse.java
- [x] Step 5: 创建 UserService.java
- [x] Step 6: 创建 UserController.java
- [x] Step 7: 提交

### Task 7: 创建角色管理 DTO、Service、Controller

- [x] Step 1: 创建 RoleQueryRequest.java
- [x] Step 2: 创建 RoleCreateRequest.java
- [x] Step 3: 创建 RoleUpdateRequest.java
- [x] Step 4: 创建 RoleResponse.java
- [x] Step 5: 创建 RoleService.java
- [x] Step 6: 创建 RoleController.java
- [x] Step 7: 提交

### Task 8: 创建权限和数据范围管理 DTO、Service、Controller

- [x] Step 1: 创建 PermissionCreateRequest.java
- [x] Step 2: 创建 PermissionUpdateRequest.java
- [x] Step 3: 创建 PermissionResponse.java
- [x] Step 4: 创建 DataScopeCreateRequest.java
- [x] Step 5: 创建 DataScopeUpdateRequest.java
- [x] Step 6: 创建 DataScopeResponse.java
- [x] Step 7: 创建 PermissionService.java
- [x] Step 8: 创建 DataScopeService.java
- [x] Step 9: 创建 PermissionController.java
- [x] Step 10: 创建 DataScopeController.java
- [x] Step 11: 提交

### Task 9: 创建 AuthController 和配置

- [x] Step 1: 创建 AuthController.java
- [x] Step 2: 添加认证错误码到 ErrorCode.java
- [x] Step 3: 添加 JWT 配置到 application.yml
- [x] Step 4: 提交

### Task 10: 创建数据库表结构 SQL

- [x] Step 1: 创建 sql/sys_user.sql
- [x] Step 2: 创建 sql/sys_role.sql
- [x] Step 3: 创建 sql/sys_user_role.sql
- [x] Step 4: 创建 sql/sys_permission.sql
- [x] Step 5: 创建 sql/sys_role_permission.sql
- [x] Step 6: 创建 sql/sys_data_scope.sql
- [x] Step 7: 创建 sql/sys_role_data_scope.sql
- [x] Step 8: 提交

---

## ✅ Phase 16 已完成

- 手机号登录功能已实现
- JWT 无状态认证已配置
- 角色+权限+数据范围管理已实现

---

## Phase 17: 菜单管理模块实现

> 目标：实现菜单管理模块的增删改查功能，支持无限级层级结构
> 架构：四层架构 Controller + Service + Mapper + Model
> 技术栈：Java8 + Maven + MyBatis + Spring Boot

### Task 1: 创建菜单实体 SysMenu

- [x] Step 1: 创建 src/main/java/com/address/model/SysMenu.java
- [x] Step 2: 提交

### Task 2: 创建菜单相关 DTO

- [x] Step 1: 创建 MenuCreateRequest.java
- [x] Step 2: 创建 MenuUpdateRequest.java
- [x] Step 3: 创建 MenuQueryRequest.java
- [x] Step 4: 创建 MenuResponse.java
- [x] Step 5: 创建 MenuTreeResponse.java
- [x] Step 6: 提交

### Task 3: 创建菜单 Mapper

- [x] Step 1: 创建 src/main/java/com/address/repository/SysMenuMapper.java
- [x] Step 2: 提交

### Task 4: 创建菜单 Service

- [x] Step 1: 创建 src/main/java/com/address/service/MenuService.java
- [x] Step 2: 提交

### Task 5: 创建菜单 Controller

- [x] Step 1: 创建 src/main/java/com/address/controller/MenuController.java
- [x] Step 2: 提交

### Task 6: 创建菜单单元测试

- [x] Step 1: 创建 src/test/java/com/address/controller/MenuControllerTest.java
- [x] Step 2: 提交

### Task 7: 更新接口文档

- [x] Step 1: 在 http/interface.md 中添加菜单接口
- [x] Step 2: 提交

---

## ✅ Phase 17 已完成

- 菜单管理模块已实现
- 支持无限级层级结构（parentId 自引用）
- 软删除机制（del_flag='Y'）
- 7 个接口：查询、详情、创建、更新、删除、菜单树、角色分配菜单

---

## Phase 18: SysUser 字段扩展实现

> **目标:** 为 sys_user 表新增 6 个字段（userName, email, province, city, district, hobby）
> **架构:** 修改现有实体类和 DTO，新增字段映射，保持接口兼容
> **技术栈:** Java8 + Maven + MyBatis + Spring Boot

### Task 1: 修改 SysUser 实体

- [ ] Step 1: 修改实体类，添加新字段（userName, email, province, city, district, hobby）
- [ ] Step 2: 添加 getter/setter 方法
- [ ] Step 3: 提交

### Task 2: 修改 SysUserMapper

- [ ] Step 1: 修改 insert 语句
- [ ] Step 2: 修改 update 语句
- [ ] Step 3: 修改 findById 查询，添加字段映射
- [ ] Step 4: 修改 findByPhone 查询，添加字段映射
- [ ] Step 5: 修改 findAll 查询，添加字段映射
- [ ] Step 6: 提交

### Task 3: 修改 UserCreateRequest

- [ ] Step 1: 添加新字段
- [ ] Step 2: 添加 getter/setter
- [ ] Step 3: 提交

### Task 4: 修改 UserUpdateRequest

- [ ] Step 1: 添加新字段
- [ ] Step 2: 添加 getter/setter
- [ ] Step 3: 提交

### Task 5: 修改 UserResponse

- [ ] Step 1: 添加新字段
- [ ] Step 2: 添加 getter/setter
- [ ] Step 3: 提交

### Task 6: 修改 UserService

- [ ] Step 1: 修改 toResponse 方法，添加新字段映射
- [ ] Step 2: 提交

### Task 7: 修改 sql/sys_user.sql

- [ ] Step 1: 更新建表语句，添加新字段
- [ ] Step 2: 提交

### Task 8: 编译验证

- [ ] Step 1: 运行编译
- [ ] Step 2: 运行测试

---

## Phase 19: 登录接口返回码归类实现

> **目标:** 为 `/api/auth/login` 接口建立结构化错误码体系，区分用户未注册、用户已禁用、密码错误三种场景
> **架构:** 通过 LoginResult 封装认证结果（不再抛异常），Controller 根据结果构造 ApiResponse 返回对应错误码
> **技术栈:** Java8 + Maven + Spring Boot + MyBatis

### Task 1: 创建 AuthErrorCode 错误码常量类

**Files:**
- Create: `src/main/java/com/address/common/AuthErrorCode.java`

- [ ] Step 1: 创建文件

```java
package com.address.common;

public class AuthErrorCode {
    public static final String SUCCESS = "000000";
    public static final String USER_NOT_FOUND = "101001";
    public static final String USER_DISABLED = "101002";
    public static final String PASSWORD_ERROR = "101003";
}
```

- [ ] Step 2: 验证编译

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] Step 3: 提交

```bash
git add src/main/java/com/address/common/AuthErrorCode.java
git commit -m "feat: 添加认证模块错误码常量类 AuthErrorCode"
```

---

### Task 2: 创建 LoginResult 认证结果封装类

**Files:**
- Create: `src/main/java/com/address/dto/LoginResult.java`

- [ ] Step 1: 创建文件

```java
package com.address.dto;

import com.address.common.AuthErrorCode;

public class LoginResult {
    private String code;
    private String message;
    private String token;
    private String phone;

    private LoginResult(String code, String message, String token, String phone) {
        this.code = code;
        this.message = message;
        this.token = token;
        this.phone = phone;
    }

    public static LoginResult success(String token, String phone) {
        return new LoginResult(AuthErrorCode.SUCCESS, "成功", token, phone);
    }

    public static LoginResult error(String code, String message) {
        return new LoginResult(code, message, null, null);
    }

    public boolean isSuccess() {
        return AuthErrorCode.SUCCESS.equals(code);
    }

    public LoginResponse toLoginResponse() {
        LoginResponse response = new LoginResponse();
        response.setToken(this.token);
        response.setPhone(this.phone);
        return response;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public String getPhone() { return phone; }
}
```

- [ ] Step 2: 验证编译

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] Step 3: 提交

```bash
git add src/main/java/com/address/dto/LoginResult.java
git commit -m "feat: 添加 LoginResult 认证结果封装类"
```

---

### Task 3: 修改 ApiResponse 支持自定义成功码

**Files:**
- Modify: `src/main/java/com/address/common/ApiResponse.java`

- [ ] Step 1: 添加 success(data, code) 方法

在 ApiResponse.java 第 18 行 `success(T data)` 方法后添加：

```java
public static <T> ApiResponse<T> success(T data, String code) {
    return new ApiResponse<>(code, "成功", data);
}
```

- [ ] Step 2: 验证编译

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] Step 3: 提交

```bash
git add src/main/java/com/address/common/ApiResponse.java
git commit -m "feat: ApiResponse 添加支持自定义成功码的 success(data, code) 方法"
```

---

### Task 4: 修改 AuthService.login() 返回 LoginResult

**Files:**
- Modify: `src/main/java/com/address/service/AuthService.java`

- [ ] Step 1: 修改 login 方法返回类型和实现

将 login 方法：

```java
public LoginResponse login(LoginRequest request) {
    SysUser user = sysUserMapper.findActiveByPhone(request.getPhone());
    if (user == null) {
        throw new RuntimeException("用户不存在或已禁用");
    }
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        throw new RuntimeException("密码错误");
    }
    String token = jwtUtil.generateToken(user.getUserId(), user.getPhone());
    LoginResponse response = new LoginResponse();
    response.setToken(token);
    response.setPhone(user.getPhone());
    return response;
}
```

替换为：

```java
public LoginResult login(LoginRequest request) {
    SysUser user = sysUserMapper.findActiveByPhone(request.getPhone());
    if (user == null) {
        SysUser existUser = sysUserMapper.findByPhone(request.getPhone());
        if (existUser == null) {
            return LoginResult.error(AuthErrorCode.USER_NOT_FOUND, "用户未注册");
        } else {
            return LoginResult.error(AuthErrorCode.USER_DISABLED, "用户已禁用");
        }
    }
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
        return LoginResult.error(AuthErrorCode.PASSWORD_ERROR, "密码错误");
    }
    String token = jwtUtil.generateToken(user.getUserId(), user.getPhone());
    return LoginResult.success(token, user.getPhone());
}
```

- [ ] Step 2: 添加必要的 import

文件头部添加：
```java
import com.address.common.AuthErrorCode;
import com.address.dto.LoginResult;
```

- [ ] Step 3: 验证编译

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] Step 4: 提交

```bash
git add src/main/java/com/address/service/AuthService.java
git commit -m "feat: AuthService.login() 改为返回 LoginResult，支持细粒度错误码"
```

---

### Task 5: 修改 AuthController.login() 处理 LoginResult

**Files:**
- Modify: `src/main/java/com/address/controller/AuthController.java`

- [ ] Step 1: 修改 login 方法

将：

```java
@PostMapping("/api/auth/login")
public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
    return ApiResponse.success(authService.login(request));
}
```

替换为：

```java
@PostMapping("/api/auth/login")
public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
    LoginResult result = authService.login(request);
    if (result.isSuccess()) {
        return ApiResponse.success(result.toLoginResponse(), result.getCode());
    }
    return ApiResponse.error(result.getCode(), result.getMessage());
}
```

- [ ] Step 2: 添加必要的 import

```java
import com.address.dto.LoginResult;
```

- [ ] Step 3: 验证编译

Run: `mvn compile -q`
Expected: BUILD SUCCESS

- [ ] Step 4: 提交

```bash
git add src/main/java/com/address/controller/AuthController.java
git commit -m "feat: AuthController.login() 根据 LoginResult 返回对应错误码"
```

---

### Task 6: 运行测试验证

**Files:**
- Test: `src/test/java/com/address/service/AuthServiceTest.java`

- [ ] Step 1: 运行 AuthServiceTest

Run: `mvn test -Dtest=AuthServiceTest`
Expected: 所有测试通过

- [ ] Step 2: 运行完整测试套件

Run: `mvn test 2>&1 | grep -E "Tests run|BUILD"`
Expected: BUILD SUCCESS，无测试失败

- [ ] Step 3: 提交测试相关修改（如有）

```bash
git add .
git commit -m "test: 验证登录错误码功能"
```

---

### 自检清单

- [ ] 所有错误码（101001、101002、101003）和成功码（000000）与设计一致
- [ ] LoginResult 正确实现 isSuccess()、toLoginResponse() 方法
- [ ] ApiResponse.success(data, code) 可正确调用
- [ ] AuthService.login() 不再抛出 RuntimeException
- [ ] SysUserMapper 无需修改（已有 findByPhone 和 findActiveByPhone）
- [ ] 测试覆盖用户未注册、用户已禁用、密码错误、登录成功四种场景
