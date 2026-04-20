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

- [ ] Step 1: 删除现有 ClientAddressService.java
- [ ] Step 2: 按设计 11 步流程重写

### Task 10: 修改 AddressMerger 支持标记删除

- [ ] Step 1: 确保 mergeStock 支持 del_flag 标记删除

### Task 11: 验证和测试

- [ ] Step 1: 运行 Maven 测试
- [ ] Step 2: 检查所有测试是否通过
- [ ] Step 3: 提交变更

---

## 当前阶段

重构阶段 - Task 9 待开始

---

## 遇到的问题

| 问题 | 解决方案 |
|------|---------|
| Maven surefire 插件版本过旧导致 JUnit5 测试不执行 | 升级 maven-surefire-plugin 至 3.1.2 |
| PriorityMailingAddressStrategy 使用 type.name() 而非 type.getCode() | 修改为使用 getCode() 匹配 addressType |
| 测试数据中修改时间与预期优先级不匹配 | 调整测试数据使时间与优先级顺序一致 |
| testUpdateExistingAddress 失败：返回2条而非1条 | 给已存在的地址设置 seqNo 以便识别 |
