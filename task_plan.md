# Task Plan

> 项目：客户地址信息维护类库
> 目标：实现客户地址信息维护功能（地址合并、通讯地址选择、最新地址选择）
> 架构：Model → Repository → Strategy → Service 分层设计
> 技术栈：Java8 + Maven

---

## 阶段与进度

### Phase 1: 基础设施
- [ ] Task 1: Maven 项目初始化
  - [ ] 创建 pom.xml
  - [ ] 创建 CifAddress.java
  - [ ] 创建 CifAddressTest.java
  - [ ] 运行测试验证
  - [ ] 提交

- [ ] Task 2: AddressType 枚举
  - [ ] 创建 AddressType.java
  - [ ] 创建 AddressTypeTest.java
  - [ ] 运行测试验证
  - [ ] 提交

### Phase 2: 存储层
- [ ] Task 3: Repository 层
  - [ ] 创建 ClientAddressRepository.java
  - [ ] 创建 MemoryClientAddressRepository.java
  - [ ] 创建 MemoryClientAddressRepositoryTest.java
  - [ ] 运行测试验证
  - [ ] 提交

### Phase 3: 策略层
- [ ] Task 4: 地址选择策略
  - [ ] 创建 MailingAddressStrategy.java
  - [ ] 创建 NewestAddressStrategy.java
  - [ ] 创建 PriorityMailingAddressStrategy.java
  - [ ] 创建 PriorityNewestAddressStrategy.java
  - [ ] 创建 PriorityMailingAddressStrategyTest.java
  - [ ] 创建 PriorityNewestAddressStrategyTest.java
  - [ ] 运行测试验证
  - [ ] 提交

### Phase 4: 业务层
- [ ] Task 5: 地址合并逻辑
  - [ ] 创建 AddressMerger.java
  - [ ] 创建 AddressMergerTest.java
  - [ ] 运行测试验证
  - [ ] 提交

- [ ] Task 6: ClientAddressService 核心服务
  - [ ] 创建 ClientAddressService.java
  - [ ] 创建 ClientAddressServiceTest.java
  - [ ] 创建 ClientAddressServiceIntegrationTest.java
  - [ ] 运行测试验证
  - [ ] 提交

### Phase 5: 收尾
- [ ] Task 7: 异常处理
  - [ ] 创建 AddressBusinessException.java
  - [ ] 运行测试验证
  - [ ] 提交

- [ ] Task 8: Maven 打包验证
  - [ ] 更新 pom.xml
  - [ ] 运行打包验证
  - [ ] 运行全部测试
  - [ ] 提交

---

## 实施检查清单

- [ ] Task 1: Maven 项目初始化
- [ ] Task 2: AddressType 枚举
- [ ] Task 3: Repository 层
- [ ] Task 4: 地址选择策略
- [ ] Task 5: 地址合并逻辑
- [ ] Task 6: ClientAddressService 核心服务
- [ ] Task 7: 异常处理
- [ ] Task 8: Maven 打包验证

---

## 当前阶段

Phase 1: 基础设施 - 等待开始
