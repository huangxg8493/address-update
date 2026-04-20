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

### 参考算法 vs 当前实现差异

| 步骤 | 参考算法 | 当前实现 |
|------|---------|----------|
| 1 | 使用两个数组 | 只有一个上送数组 |
| 2 | 合并两个数组 | 只合并上送 |
| 3 | 应用更新/新增规则 | 应用了，但顺序不对 |
| 4 | 应用通讯地址和最新地址规则 | 只从上送数组选 |
| 5 | 将两个数组标识设置为N | 在规则应用之前就重置了 |
| 6 | 将通讯地址标识设置为Y | 正确 |
| 7 | 将最新地址标识设置为Y | 正确 |
| 8 | 批量insert | 正确 |
| 9 | 批量update | 正确 |

### 关键问题
1. **规则应用范围**：当前只从上送地址中挑选 mailing 和 newest，应该是两个数组都参与
2. **标识重置时机**：当前在规则应用前重置，但参考算法要求在规则应用后
3. **批量update对象**：当前是对上送数组，应该是合并后的存量数组

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
