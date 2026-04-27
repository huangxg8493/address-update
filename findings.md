# Findings

> 研究与发现记录

---

## 需求理解

### 业务规则摘要
- 地址实体 CifAddress：seqNo, clientNo, addressType, addressDetail, lastChangeDate, isMailingAddress, isNewest, delFlag
- 10种地址类型：OTHER, CONTACT, RESIDENCE, COMPANY, HOUSEHOLD, CERTIFICATE, BUSINESS, REGISTERED, OFFICE, PERMANENT
- 每种类型只能有一个"最新地址"（isNewest='Y'）
- 只能有一个"通讯地址"（isMailingAddress='Y'），且通讯地址必然是最新地址
- del_flag='Y' 的记录为逻辑删除，不参与业务规则

### 通讯地址选择优先级
按顺序：其他 > 联系 > 居住 > 单位 > 户籍 > 证件 > 营业 > 注册 > 办公 > 永久

### 合并规则
- addressType + addressDetail 相同视为重复地址
- 合并时修改时间为当前日期
- isMailingAddress/isNewest 任一个有Y则为Y
- 存量合并后删除被合并的记录

### 新增/更新判定
- addressType + addressDetail 在存量中存在 → 更新（seqNo 继承存量）
- 不存在 → 新增（seqNo 为空）

---

## 架构决策

### Repository 层设计
- ClientAddressRepository 接口隔离存储实现
- MemoryClientAddressRepository 内存实现
- 切换 MySQL 时只需实现同一接口，Service 层代码不变

### 策略模式设计
- MailingAddressStrategy 通讯地址选择策略接口
- NewestAddressStrategy 最新地址选择策略接口
- 便于将来替换和扩展选择逻辑

### 技术约束
- Java8 + Maven
- 使用 RuntimeException，不新建异常类
- 存储层使用 List，不使用 Map
- del_flag='Y' 的记录不参与业务规则

---

## 重构发现

### 正确理解参考算法

1. 获取存量数组
2. 合并上送地址（去重）
3. 标记存量删除项（del_flag=Y）
4. 遍历上送地址找匹配存量
5. 收集两个数组有效地址
6. 挑选 mailing 和 newestByType（仅收集，不设置标识）
7. 重置两个数组所有标识为 N
8. 设置 mailing 标识为 Y
9. 设置 newest 标识为 Y
10. 批量 insert
11. 遍历存量地址用匹配来源覆盖更新

### 关键约束
- 不需要 seqNoMap，直接匹配
- 批量 update 针对所有存量地址
- del_flag=Y 不参与业务规则

---

## 文件结构

```
src/main/java/com/address/
  ├─ model/
  │   ├─ CifAddress.java
  │   └─ AddressType.java
  ├─ repository/
  │   ├─ ClientAddressRepository.java
  │   └─ MemoryClientAddressRepository.java
  ├─ strategy/
  │   ├─ MailingAddressStrategy.java
  │   ├─ NewestAddressStrategy.java
  │   └─ impl/
  │       ├─ PriorityMailingAddressStrategy.java
  │       └─ PriorityNewestAddressStrategy.java
  ├─ service/
  │   ├─ ClientAddressService.java  # 重构核心
  │   └─ AddressMerger.java        # 修改mergeStock
  ├─ constants/
  │   └─ Constants.java           # 魔法值常量
  └─ utils/
      └─ SnowflakeIdGenerator.java # 雪花算法 ID 生成器

src/test/java/com/address/
  ├─ model/
  │   ├─ CifAddressTest.java
  │   └─ AddressTypeTest.java
  ├─ repository/
  │   └─ MemoryClientAddressRepositoryTest.java
  ├─ strategy/
  │   ├─ PriorityMailingAddressStrategyTest.java
  │   └─ PriorityNewestAddressStrategyTest.java
  ├─ service/
  │   ├─ AddressMergerTest.java
  │   └─ ClientAddressServiceTest.java
  ├─ integration/
  │   └─ ClientAddressServiceIntegrationTest.java
  └─ utils/
      └─ SnowflakeIdGeneratorTest.java
```

---

## 提交记录

- 1c4df40: chore: 添加 CLAUDE.md 代码库指南
- 55148b4: refactor: PriorityNewestAddressStrategy.selectByType 改为空实现
- ec07053: refactor: 简化 PriorityMailingAddressStrategy.select 实现
- 0c5decf: refactor: 简化 PriorityMailingAddressStrategy，移除 for 循环中的 StreamAPI
- bb76ed4: fix: 第8条优先级实现修正
- f514a5e: feat: 实现 PriorityMailingAddressStrategy.select 通讯地址选择逻辑
- c44850b: feat: 实现 PriorityNewestAddressStrategy.selectByType 最新地址选择逻辑

---

## 重构完成

所有 Task 9-11 已完成。

---

## seqNo 雪花算法实现

### 背景
CifAddress 的 seqNo 字段用于唯一标识地址记录。当前使用 UUID 截取 16 位生成，存在极低碰撞概率。为保证全局唯一性，改为 Snowflake 算法。

### 设计决策
- **保持 String 类型** - Snowflake ID 转为字符串存储
- **固定 workerId/datacenterId** - workerId=1, datacenterId=1
- **手动实现** - 不依赖外部库，纯 Java 实现
- **时钟回拨处理** - 阻塞等待时钟追上

### 雪花算法结构
```
1 bit  | 41 bits          | 5 bits      | 5 bits       | 12 bits
------|------------------|-------------|--------------|--------------
0     | 时间戳（毫秒）      | workerId=1  | datacenterId=1 | 序列号
```

### 生成示例
```
694252380621615873  (String 类型)
```

### 相关文件
- `src/main/java/com/address/utils/SnowflakeIdGenerator.java` (新增)
- `src/test/java/com/address/utils/SnowflakeIdGeneratorTest.java` (新增)

---

## MySQL 持久层实现

### 背景
当前持久层使用内存存储（MemoryClientAddressRepository），适用于开发测试。为支持生产环境，需切换为 MySQL 数据库存储。

### 架构决策
- **JDBC 原生** - 直接使用 Connection/PreparedStatement，轻量无额外依赖
- **配置文件** - 使用 config.yaml 管理数据库连接配置
- **自动建表** - 启动时检查表是否存在，不存在则创建

### 数据库配置
```yaml
database:
  driver: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://127.0.0.1:3306/testdb?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC
  username: admin
  password: admin
```

### 表结构
```sql
CREATE TABLE IF NOT EXISTS cif_address (
    seq_no VARCHAR(64) PRIMARY KEY,
    client_no VARCHAR(32) NOT NULL,
    address_type VARCHAR(2) NOT NULL,
    address_detail VARCHAR(256) NOT NULL,
    last_change_date DATETIME,
    is_mailing_address CHAR(1) DEFAULT 'N',
    is_newest CHAR(1) DEFAULT 'N',
    del_flag CHAR(1) DEFAULT 'N',
    INDEX idx_client_no (client_no),
    INDEX idx_client_type (client_no, address_type)
);
```

### 相关文件
- `src/main/resources/config.yaml` (新增)
- `src/main/java/com/address/config/DbConfig.java` (新增)
- `src/main/java/com/address/repository/JdbcClientAddressRepository.java` (新增)
- `src/test/java/com/address/repository/JdbcClientAddressRepositoryTest.java` (新增)

---

## HikariCP 连接池实现

### 背景
当前 JdbcClientAddressRepository 每次数据库操作都通过 DriverManager.getConnection() 获取新连接，存在连接创建开销。为提升性能，引入 HikariCP 连接池。

### 架构决策
- **HikariCP** - 目前最流行的连接池，性能最高
- **配置文件** - 在 config.yaml 中添加连接池配置项
- **DataSource 单例** - DbConfig 初始化时创建 DataSource 单例

### 连接池配置
```yaml
hikari:
  maximumPoolSize: 10
  minimumIdle: 2
  connectionTimeout: 30000
  idleTimeout: 600000
  maxLifetime: 1800000
```

### 相关文件
- `pom.xml` (修改)
- `config.yaml` (修改)
- `DbConfig.java` (修改)
- `JdbcClientAddressRepository.java` (修改)

---

## MyBatis 迁移实现

### 背景
现有 JdbcClientAddressRepository 使用原生 JDBC 实现，存在样板代码多、SQL 分散等问题。为提升可维护性，引入 MyBatis 持久层框架。

### 设计决策
- **迁移策略** - 保留 JDBC 实现，新建 MyBatis 实现
- **SQL 管理** - 简单 SQL 用注解（delete），复杂 SQL 用 XML
- **事务管理** - 手动管理，Service 层控制
- **DataSource 复用** - 复用 DbConfig 中的 HikariCP DataSource

### MyBatis 配置
- mybatis 3.5.13 + mybatis-spring 2.1.1
- mapUnderscoreToCamelCase: true（下划线转驼峰）
- CifAddress 类型别名

### 文件结构
```
src/main/resources/
  ├─ mybatis-config.xml
  └─ mapper/
      └─ CifAddressMapper.xml

src/main/java/com/address/
  ├─ config/
  │   └─ MyBatisConfig.java
  └─ repository/
      ├─ CifAddressMapper.java
      └─ MyBatisClientAddressRepository.java
```

### 相关文件
- `pom.xml` (修改)
- `src/main/resources/mybatis-config.xml` (新增)
- `src/main/resources/mapper/CifAddressMapper.xml` (新增)
- `src/main/java/com/address/config/MyBatisConfig.java` (新增)
- `src/main/java/com/address/repository/CifAddressMapper.java` (新增)
- `src/main/java/com/address/repository/MyBatisClientAddressRepository.java` (新增)
- `src/test/java/com/address/repository/MyBatisClientAddressRepositoryTest.java` (新增)

---

## Spring Boot 集成实现

### 背景
当前项目手工管理 DataSource 和 MyBatis SqlSessionFactory。为引入依赖注入和 Spring 生态，引入 Spring Boot 容器。

### 设计决策
- **Spring Boot 2.7.18** - 要求 Java 8，与当前项目兼容
- **mybatis-spring-boot-starter 3.0.3** - 官方 Starter，自动配置
- **spring-boot-starter-jdbc** - 包含 JdbcTemplate 和 HikariCP 自动配置
- **JdbcClient** - Spring JDBC 5.x 新增，链式 API

### 架构决策
- **保留 DbConfig.java 和 MyBatisConfig.java** - 不修改，不使用
- **四种 Repository 实现并存** - JdbcTemplate、JdbcClient、MyBatis、Memory
- **@Primary 标记默认实现** - JdbcTemplateClientAddressRepository 为默认

### 约束
- 不修改 DbConfig.java
- 不修改 MyBatisConfig.java
- 不使用 DbConfig.getDataSource()

### 相关文件
- `pom.xml` (修改)
- `src/main/java/com/address/Application.java` (新增)
- `src/main/resources/application.yml` (新增)
- `src/main/java/com/address/repository/JdbcTemplateClientAddressRepository.java` (新增)
- `src/main/java/com/address/repository/JdbcClientClientAddressRepository.java` (新增)
- `src/main/java/com/address/service/ClientAddressService.java` (修改)

---

## 日志记录功能实现

### 背景
为客户地址信息维护系统添加日志记录功能，记录关键业务操作，便于问题排查和系统监控。

### 设计决策
- **日志 API**：SLF4J 2.x（接口层，解耦实现）
- **日志实现**：Logback 1.4（性能好，配置简洁）
- **输出目标**：控制台 + 文件日志

### 技术选型
- SLF4J 2.0.9 (API)
- Logback 1.4.11 (实现)

### 日志配置
- 路径：logs/app.log
- 滚动策略：按日期（每天一个新文件）
- 保留：7 天自动清理
- 编码：UTF-8

### 记录内容
| 操作 | 级别 | 日志内容 |
|------|------|----------|
| save | INFO | 保存单条地址，clientNo |
| saveAll | INFO | 批量保存地址，clientNo，数量 |
| update | INFO | 更新单条地址，seqNo |
| updateAll | INFO | 批量更新地址，clientNo，数量 |
| delete | INFO | 删除地址，seqNo |

### 相关文件
- `pom.xml` (修改)
- `src/main/resources/logback.xml` (新增)
- `src/main/java/com/address/repository/MyBatisClientAddressRepository.java` (修改)
- `src/main/java/com/address/repository/JdbcClientAddressRepository.java` (修改)

---

## Phase 12: RESTful 接口实现

### 背景
为客户地址维护系统添加 RESTful API 接口，支持 PUT `/client/address/update` 更新客户地址。

### 设计决策
- **统一响应包装** - ApiResponse<T> 封装 code/message/data
- **错误码体系** - 200/400/404/409/500 五级错误码
- **参数校验** - clientNo 和 addresses 非空校验
- **Service 复用** - 直接调用现有 ClientAddressService.updateAddresses()

### 相关文件
- `src/main/java/com/address/common/ApiResponse.java` (新增)
- `src/main/java/com/address/common/ErrorCode.java` (新增)
- `src/main/java/com/address/dto/AddressUpdateRequest.java` (新增)
- `src/main/java/com/address/controller/ClientAddressController.java` (新增)

### 接口规格
```
PUT /client/address/update
Content-Type: application/json

Request:
{
  "clientNo": "C001",
  "addresses": [{"addressType": "02", "addressDetail": "联系地址", ...}]
}

Response:
{
  "code": "200",
  "message": "成功",
  "data": [{...}, {...}]
}
```

---

## Phase 13: 地址查询接口实现

### 背景
为客户地址维护系统添加分页查询接口 POST /client/address/query，支持按客户号和地址类型分页查询地址列表。

### 设计决策
- **POST 请求** - 请求参数放入 body 中，支持复杂查询条件
- **分页参数** - pageNum（从1开始，默认1）+ pageSize（默认10）
- **独立 Repository** - 新建 AddressQueryRepository 分离查询和变更职责
- **MyBatis 分页** - 使用 LIMIT OFFSET 语法

### 接口规格
```
POST /client/address/query
Content-Type: application/json

Request:
{
  "clientNo": "C001",
  "addressType": "02",     // 可选
  "pageNum": 1,            // 默认1
  "pageSize": 10           // 默认10
}

Response:
{
  "code": "200",
  "message": "成功",
  "data": {
    "clientNo": "C001",
    "pageNum": 1,
    "pageSize": 10,
    "total": 25,
    "totalPages": 3,
    "list": [...]
  }
}
```

### 相关文件
- `src/main/java/com/address/dto/AddressQueryRequest.java` (新增)
- `src/main/java/com/address/dto/PageResult.java` (新增)
- `src/main/java/com/address/dto/AddressQueryResponse.java` (新增)
- `src/main/java/com/address/repository/AddressQueryRepository.java` (新增)
- `src/main/java/com/address/repository/MyBatisAddressQueryRepository.java` (新增)
- `src/main/java/com/address/service/ClientAddressQueryService.java` (新增)
- `src/main/java/com/address/controller/ClientAddressQueryController.java` (新增)
- `src/main/java/com/address/repository/CifAddressMapper.java` (修改)
- `src/main/resources/mapper/CifAddressMapper.xml` (修改)

---

## Phase 14: UI 页面实现

### 背景
为客户地址维护系统添加 Web UI 页面，实现地址的查询、新增、编辑、删除功能。

### 设计决策
- **纯 HTML + JavaScript** - 无框架、无构建工具，单文件实现
- **调用后端 API** - POST `/client/address/query` 查询，POST `/client/address/update` 更新
- **弹窗编辑** - 新增/编辑使用弹窗表单

### 页面结构
```
顶部：标题 + 客户号输入框 + 查询按钮 + 新增按钮
中部：地址列表（表格）
底部分页：共 X 条 第 Y/Z 页 上一页 下一页
弹窗：地址类型、地址详情、通讯地址、最新地址
```

### 表格列
| 列名 | 字段 |
|------|------|
| 序号 | seqNo |
| 地址类型 | addressType（下拉选择） |
| 地址详情 | addressDetail |
| 修改日期 | lastChangeDate |
| 通讯地址 | isMailingAddress |
| 最新地址 | isNewest |
| 操作 | 编辑、删除按钮 |

### 相关文件
- `src/main/ui/address.html` (新增)

---

## Phase 15: 单地址维护接口实现

### 背景
为客户地址维护系统添加单地址更新接口 POST /client/address/single/update，支持单个地址的修改和逻辑删除。

### 设计决策
- **URL:** POST `/client/address/single/update`
- **请求格式:** 完整 CifAddress 字段（seqNo, clientNo, addressType, addressDetail, isMailingAddress, isNewest, delFlag）
- **业务规则:**
  - delFlag='Y' 时执行逻辑删除（优先于修改）
  - seqNo 有值且 delFlag='N' 时执行修改
  - 修改时自动更新 lastChangeDate 为当前时间
- **响应格式:** 返回更新后的 CifAddress 数据

### 接口规格
```
POST /client/address/single/update
Content-Type: application/json

Request:
{
  "seqNo": "702055748011692032",
  "clientNo": "C001",
  "addressType": "03",
  "addressDetail": "北京市朝阳区建国路88号",
  "lastChangeDate": "2026-04-22 15:18:54",
  "isMailingAddress": "Y",
  "isNewest": "Y",
  "delFlag": "N"
}

Response:
{
  "code": "00000",
  "message": "成功",
  "data": {
    "seqNo": "702055748011692032",
    "clientNo": "C001",
    ...
  }
}
```

### 相关文件
- `src/main/java/com/address/dto/SingleAddressRequest.java` (新增)
- `src/main/java/com/address/dto/SingleAddressResponse.java` (新增)
- `src/main/java/com/address/controller/ClientAddressController.java` (修改)
- `src/main/java/com/address/service/ClientAddressService.java` (修改)

---

## Phase 16: 手机号登录功能设计

### 背景
为客户地址管理系统增加基于手机号+密码的登录认证功能，支持角色+权限控制+数据范围配置。

### 设计决策
- **认证方式**: JWT 无状态认证
- **密码存储**: BCrypt 加密
- **ID 生成**: 复用 SnowflakeIdGenerator
- **数据模型**: 7 张表（用户、角色、权限、数据范围及其关联表）

### 数据模型

#### sys_user（用户表）
| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | bigint | 主键，雪花算法 |
| phone | varchar(20) | 手机号，唯一索引 |
| password | varchar(255) | BCrypt 加密 |
| status | char(1) | Y-启用，N-禁用 |
| create_time | datetime | 创建时间 |
| update_time | datetime | 更新时间 |

#### sys_role（角色表）
| 字段 | 类型 | 说明 |
|------|------|------|
| role_id | bigint | 主键，雪花算法 |
| role_code | varchar(50) | 角色代码，唯一索引 |
| role_name | varchar(100) | 角色名称 |
| status | char(1) | Y-启用，N-禁用 |
| create_time | datetime | 创建时间 |

#### sys_user_role（用户角色关联）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| user_id | bigint | 用户ID |
| role_id | bigint | 角色ID |

#### sys_permission（权限表）
| 字段 | 类型 | 说明 |
|------|------|------|
| permission_id | bigint | 主键，雪花算法 |
| permission_code | varchar(100) | 权限代码，唯一索引 |
| permission_name | varchar(100) | 权限名称 |
| menu_url | varchar(255) | 菜单URL |
| create_time | datetime | 创建时间 |

#### sys_role_permission（角色权限关联）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| role_id | bigint | 角色ID |
| permission_id | bigint | 权限ID |

#### sys_data_scope（数据范围表）
| 字段 | 类型 | 说明 |
|------|------|------|
| scope_id | bigint | 主键，雪花算法 |
| scope_code | varchar(50) | 范围代码 |
| scope_name | varchar(100) | 范围名称 |
| scope_type | varchar(20) | OWN/DEPT/ALL |
| create_time | datetime | 创建时间 |

#### sys_role_data_scope（角色数据范围关联）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键 |
| role_id | bigint | 角色ID |
| scope_id | bigint | 数据范围ID |

### 接口设计（全部 POST）

#### 认证模块
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/auth/register | POST | 用户注册（手机号+密码） |
| /api/auth/login | POST | 用户登录（手机号+密码） |
| /api/auth/logout | POST | 登出 |

#### 用户管理（管理员）
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/users/query | POST | 分页查询用户列表 |
| /api/users/create | POST | 创建用户 |
| /api/users/update | POST | 修改用户 |
| /api/users/delete | POST | 删除用户 |
| /api/users/{userId}/roles/assign | POST | 分配用户角色 |

#### 角色管理（管理员）
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/roles/query | POST | 查询角色列表 |
| /api/roles/create | POST | 创建角色 |
| /api/roles/update | POST | 修改角色 |
| /api/roles/delete | POST | 删除角色 |
| /api/roles/{roleId}/permissions/assign | POST | 分配角色权限 |
| /api/roles/{roleId}/dataScopes/assign | POST | 分配角色数据范围 |

#### 权限管理（管理员）
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/permissions/query | POST | 查询权限列表 |
| /api/permissions/create | POST | 创建权限 |
| /api/permissions/update | POST | 修改权限 |
| /api/permissions/delete | POST | 删除权限 |

#### 数据范围管理（管理员）
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/dataScopes/query | POST | 查询数据范围列表 |
| /api/dataScopes/create | POST | 创建数据范围 |
| /api/dataScopes/update | POST | 修改数据范围 |
| /api/dataScopes/delete | POST | 删除数据范围 |

#### 用户信息
| 接口 | 方法 | 说明 |
|------|------|------|
| /api/users/me/get | POST | 获取当前用户信息（含角色、权限、数据范围） |

### 统一响应格式
复用现有 `ApiResponse`:
```json
{
    "code": "200",
    "message": "成功",
    "data": { ... }
}
```

### 安全设计
- 密码 BCrypt 加密存储
- JWT Token 签名验证
- 敏感接口需携带有效 Token 访问

---

## Phase 17: 菜单管理模块设计

### 背景
为客户地址管理系统添加独立菜单管理模块，支持无限级层级结构，采用软删除机制。

### 设计决策
- **独立模块**：新建 sys_menu 表，与权限（SysPermission）分离
- **无限层级**：使用 parentId 自引用实现，parentId=null 表示顶级菜单
- **软删除**：del_flag='Y' 标记删除，查询时自动过滤
- **计算字段**：levelDepth（层级深度）和 isLeaf（是否叶子节点）由系统维护

### 数据模型

#### sys_menu（菜单表）
| 字段 | 类型 | 说明 |
|------|------|------|
| menu_id | bigint | 主键，雪花算法 |
| menu_name | varchar(100) | 菜单名称（必填） |
| menu_url | varchar(255) | 菜单 URL（必填） |
| icon | varchar(100) | 图标 |
| sort_order | int | 排序号 |
| status | char(1) | 状态 |
| is_leaf | char(1) | 是否叶子节点 Y/N（计算字段） |
| level_depth | int | 层级深度（计算字段） |
| component | varchar(255) | 组件 |
| component_path | varchar(255) | 组件所在目录 |
| parent_id | bigint | 父菜单ID，null表示顶级 |
| del_flag | char(1) | 软删除标记 Y/N |
| create_time | datetime | 创建时间 |

### 接口设计（全部 POST）

| 接口 | 方法 | 说明 |
|------|------|------|
| /api/menus/query | POST | 分页/条件查询菜单 |
| /api/menus/{menuId} | GET | 获取单个菜单 |
| /api/menus/create | POST | 新增菜单 |
| /api/menus/update | POST | 更新菜单 |
| /api/menus/delete | POST | 软删除 |
| /api/menus/tree | POST | 获取完整菜单树 |
| /api/roles/{roleId}/menus/assign | POST | 角色分配菜单 |

---

## Phase 18: SysUser 字段扩展设计

### 背景
为 sys_user 表新增 6 个字段：userName, email, province, city, district, hobby

### 设计决策
- **字段命名:** 使用下划线命名（user_name, email, province, city, district, hobby）
- **Java 命名:** 使用驼峰命名（userName, email, province, city, district, hobby）
- **Mapper 映射:** 使用 @Results 注解进行下划线到驼峰的映射

### 数据模型

#### sys_user（修改后）
| 字段 | 类型 | 说明 |
|------|------|------|
| user_name | varchar(100) | 用户名称 |
| email | varchar(100) | 邮箱 |
| province | varchar(50) | 省 |
| city | varchar(50) | 市 |
| district | varchar(50) | 区 |
| hobby | varchar(500) | 业余爱好 |

### 相关文件
- `src/main/java/com/address/model/SysUser.java` (修改)
- `src/main/java/com/address/repository/SysUserMapper.java` (修改)
- `src/main/java/com/address/dto/UserCreateRequest.java` (修改)
- `src/main/java/com/address/dto/UserUpdateRequest.java` (修改)
- `src/main/java/com/address/dto/UserResponse.java` (修改)
- `src/main/java/com/address/service/UserService.java` (修改)
- `sql/sys_user.sql` (修改)

---

## Phase 19: 登录接口返回码归类设计

### 背景
当前 `/api/auth/login` 接口通过抛出 RuntimeException 字符串返回错误信息，无法精确区分错误类型（如"用户不存在或已禁用"合并为一个信息）。需要建立结构化的错误码体系。

### 设计决策
- **错误码结构**：成功码 000000，认证模块码段 101xxx（1=认证模块，01=登录子类型）
- **实现方式**：AuthService 直接返回错误码，不再抛出异常，Controller 统一构造 ApiResponse
- **封装类**：LoginResult 封装 code/message/token/phone

### 错误码定义
| 错误场景 | 错误码 | 说明 |
|---------|--------|------|
| 成功 | `000000` | 登录成功 |
| 用户未注册 | `101001` | 手机号未在系统注册 |
| 用户已禁用 | `101002` | 用户存在但 status='N' |
| 密码错误 | `101003` | 密码不匹配 |

### 相关文件
- `src/main/java/com/address/common/AuthErrorCode.java` (新增)
- `src/main/java/com/address/dto/LoginResult.java` (新增)
- `src/main/java/com/address/service/AuthService.java` (修改)
- `src/main/java/com/address/common/ApiResponse.java` (修改)
- `src/main/java/com/address/controller/AuthController.java` (修改)

---

## Phase 20: 角色菜单关联设计

### 背景
新增 `sys_role_menu` 表实现角色与菜单的关联，支持批量分配和查询。

### 数据模型

#### sys_role_menu（角色菜单关联表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | bigint | 主键ID，自增 |
| role_id | bigint | 角色ID |
| menu_id | bigint | 菜单ID |
| create_time | datetime | 创建时间 |

**唯一索引**: uk_role_menu(role_id, menu_id)

### 实体类

**文件**：`src/main/java/com/address/model/SysRoleMenu.java`

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

### 接口设计

#### 批量分配菜单
- **URL**: `POST /api/roles/{roleId}/menus`
- **请求体**: `{"menuIds": [1, 2, 3]}`
- **响应**: 成功返回 `000000`

#### 查询角色菜单
- **URL**: `GET /api/roles/{roleId}/menus`
- **响应**: 返回菜单完整信息列表

### 实现文件
| 操作 | 文件路径 |
|-----|---------|
| 新建 | `src/main/java/com/address/model/SysRoleMenu.java` |
| 新建 | `src/main/java/com/address/repository/SysRoleMenuMapper.java` |
| 新建 | `src/main/java/com/address/service/RoleMenuService.java` |
| 修改 | `src/main/java/com/address/controller/RoleController.java` |
| 新建 | `src/test/java/com/address/service/RoleMenuServiceTest.java` |

### 核心逻辑
1. **批量分配**：先删后插（deleteByRoleId → insertBatch）
2. **查询**：selectByRoleId + 关联 SysMenu 查询完整信息
3. **防重复**：uk_role_menu 唯一索引保证不重复插入

---

## Phase 21: 登录接口返回完整信息设计

### 背景
登录接口 `/api/auth/login` 需要返回用户的完整信息，包括用户基本信息、拥有的角色、有权限的菜单（含按钮级别，树形层级结构）。

### 设计决策
- **策略**：单一响应，登录成功返回 token + 用户信息 + 角色列表 + 菜单树
- **按钮级别**：叶子节点（isLeaf='Y'）作为按钮，通过 parentId 挂在父菜单下
- **菜单树**：使用 children 递归嵌套，叶子节点 isLeaf=true
- **多角色合并**：SQL 层 DISTINCT 去重

### 响应结构

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
      { "roleId": 1, "roleCode": "admin", "roleName": "管理员", "status": "Y" }
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
              { "menuId": 5, "menuName": "新增用户", "isLeaf": true, "children": [] }
            ]
          }
        ]
      }
    ]
  }
}
```

### 数据获取逻辑

```
1. 验证用户身份（手机号 + 密码）→ 现有逻辑不变
2. 查询用户角色列表：SysUserRoleMapper.findByUserId → List<SysUserRole> → SysRoleMapper.findById
3. 查询用户菜单权限：SysUserRoleMapper.findByUserId → roleIds → SysMenuMapper.findByRoleIds
4. 构建菜单树：扁平菜单列表按 parentId 递归组织为树形结构
5. 生成 JWT Token → 现有逻辑不变
```

### 涉及的组件变更

| 文件 | 操作 | 说明 |
|------|------|------|
| `src/main/java/com/address/dto/UserInfo.java` | 新增 | 用户基本信息 DTO |
| `src/main/java/com/address/dto/RoleInfo.java` | 新增 | 角色信息 DTO |
| `src/main/java/com/address/dto/MenuTreeDTO.java` | 新增 | 菜单树节点 DTO |
| `src/main/java/com/address/dto/LoginResponse.java` | 修改 | 扩展字段 user/roles/menus |
| `src/main/java/com/address/repository/SysMenuMapper.java` | 修改 | 新增 findByRoleIds 批量查询 |
| `src/main/java/com/address/service/AuthService.java` | 修改 | login() 组装完整响应 |

### 菜单树构建算法

```
输入：扁平菜单列表（按 sortOrder 排序）
输出：树形菜单结构

1. 将菜单列表按 parentId 分组
2. 找到 parentId=null 的根菜单
3. 递归构建子树：
   - 叶子节点（isLeaf='Y'）：children = []
   - 非叶子节点：children = 子菜单列表（递归）
```

### 安全性
- `LoginResponse.user` 不包含 `password` 字段
- `SysUser` 查询时不加载 password

---

### 性能考虑
- 用户角色和菜单信息可缓存（Redis），避免每次登录查询
- 缓存策略（可选，后续实现）：用户信息缓存 30 分钟，菜单树缓存 1 小时
