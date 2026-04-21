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

## 提交记录

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
- 3a6714d: feat: 添加 Repository 层接口及内存实现
- 9294fe2: feat: 添加地址选择策略接口及实现
- ee24e44: feat: 添加 AddressMerger 地址合并逻辑
- 4dddfe3: feat: 添加 ClientAddressService 核心服务及集成测试
- 609968e: fix: 修复 ClientAddressServiceTest 测试
- cfab2ae: feat: 添加 AddressBusinessException 业务异常
- 1c4df40: chore: 添加 CLAUDE.md 代码库指南
