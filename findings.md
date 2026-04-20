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
  └─ exception/  # 已删除 AddressBusinessException

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
  └─ integration/
        └─ ClientAddressServiceIntegrationTest.java
```
