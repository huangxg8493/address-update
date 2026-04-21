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

### Phase 6: seqNo 雪花算法实现

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

- [ ] Step 1: 在 pom.xml 添加 slf4j-api 和 logback-classic 依赖
- [ ] Step 2: 验证依赖下载
- [ ] Step 3: 提交

### Task 2: 创建 logback.xml

- [ ] Step 1: 创建 src/main/resources/logback.xml
- [ ] Step 2: 提交

### Task 3: 修改 MyBatisClientAddressRepository 添加日志

- [ ] Step 1: 添加 Logger 和日志语句
- [ ] Step 2: 编译验证
- [ ] Step 3: 提交

### Task 4: 修改 JdbcClientAddressRepository 添加日志

- [ ] Step 1: 添加 Logger 和日志语句
- [ ] Step 2: 编译验证
- [ ] Step 3: 提交

### Task 5: 验证日志输出

- [ ] Step 1: 运行测试
- [ ] Step 2: 检查日志文件
- [ ] Step 3: 提交

---

## 遇到的问题

| 问题 | 解决方案 |
|------|---------|
| Maven surefire 插件版本过旧导致 JUnit5 测试不执行 | 升级 maven-surefire-plugin 至 3.1.2 |
| PriorityMailingAddressStrategy 使用 type.name() 而非 type.getCode() | 修改为使用 getCode() 匹配 addressType |
| 测试数据中修改时间与预期优先级不匹配 | 调整测试数据使时间与优先级顺序一致 |
| testUpdateExistingAddress 失败：返回2条而非1条 | 给已存在的地址设置 seqNo 以便识别 |
