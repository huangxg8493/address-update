# 客户地址信息维护 - 设计文档

## 1. 项目概述

客户地址信息维护系统（Java8 + Maven），以类库形式提供核心业务逻辑，供其他系统调用。

### 1.1 地址类型枚举（10种）

按优先级排序：其他地址 > 联系地址 > 居住地址 > 单位地址 > 户籍地址 > 证件地址 > 营业地址 > 注册地址 > 办公地址 > 永久地址

### 1.2 业务规则

- 一个客户可有多个地址，按类型区分
- 每种类型地址只能有一个"最新地址"（isNewest='Y'）
- 只能有一个"通讯地址"（isMailingAddress='Y'），且通讯地址必然是最新的
- del_flag='Y' 的记录为逻辑删除，不参与业务规则

## 2. 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                      ClientAddressService                    │
│  (单例，通过 updateAddresses(clientNo, incoming) 暴露)      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌─────────────────┐    ┌─────────────────┐                │
│  │    Strategy     │    │   Repository    │                │
│  │                 │    │                 │                │
│  │ MailingAddress  │    │ ClientAddress   │                │
│  │ Strategy       │    │ Repository      │                │
│  │                 │    │ (interface)     │                │
│  │ NewestAddress   │    │                 │                │
│  │ Strategy       │    │ ┌─────────────┐ │                │
│  │                 │    │ │  Memory    │ │                │
│  └─────────────────┘    │ │  Impl      │ │                │
│         ↑              │ └─────────────┘ │                │
│         │              └─────────────────┘                │
│         │                                                 │
└─────────┼─────────────────────────────────────────────────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────┐
│                      核心算法（九步）                        │
├─────────────────────────────────────────────────────────────┤
│ 1. 入参 List incoming + 存量 List stock                      │
│ 2. 各自按 addressType+addressDetail 合并去重                 │
│ 3. 标记 seqNo：空→新增，有值→更新                             │
│ 4. 应用通讯地址和最新地址规则                                │
│ 5. 重置 all isMailingAddress='N', isNewest='N'               │
│ 6. 通讯地址标记 isMailingAddress='Y'                          │
│ 7. 最新地址标记 isNewest='Y'                                 │
│ 8. seqNo为空 → 生成id，批量insert                            │
│ 9. seqNo不为空 → 按seqNo update                              │
└─────────────────────────────────────────────────────────────┘
```

## 3. 模块设计

### 3.1 model/

**CifAddress.java** — 实体类

| 字段 | 类型 | 说明 |
|------|------|------|
| seqNo | String | 地址ID |
| clientNo | String | 客户ID |
| addressType | String | 地址类型 |
| addressDetail | String | 地址详情 |
| lastChangeDate | Date | 修改时间 |
| isMailingAddress | String | 'Y'/'N' 是否通讯地址 |
| isNewest | String | 'Y'/'N' 是否最新地址 |
| delFlag | String | 'Y'/'N' 逻辑删除标识 |

**AddressType.java** — 地址类型枚举

10种类型常量，按优先级定义顺序。

### 3.2 repository/

**ClientAddressRepository.java** — 仓储接口

```java
interface ClientAddressRepository {
    List<CifAddress> findByClientNo(String clientNo);
    void save(CifAddress address);
    void update(CifAddress address);
    void saveAll(List<CifAddress> addresses);
    void updateAll(List<CifAddress> addresses);
    void delete(CifAddress address);
}
```

**MemoryClientAddressRepository.java** — 内存实现

```java
class MemoryClientAddressRepository implements ClientAddressRepository {
    List<CifAddress> allAddresses = new ArrayList<>();
    // 按 clientNo 过滤实现各方法
}
```

切换 MySQL 时：新增 `MySqlClientAddressRepository` 实现同一接口，Service 层代码不变。

### 3.3 strategy/

**MailingAddressStrategy.java** — 通讯地址选择策略接口

```java
interface MailingAddressStrategy {
    CifAddress select(List<CifAddress> addresses);
}
```

**NewestAddressStrategy.java** — 最新地址选择策略接口

```java
interface NewestAddressStrategy {
    Map<String, CifAddress> selectByType(List<CifAddress> addresses);
}
```

### 3.4 strategy/impl/

**PriorityMailingAddressStrategy.java** — 按优先级选通讯地址

优先级顺序：其他 > 联系 > 居住 > 单位 > 户籍 > 证件 > 营业 > 注册 > 办公 > 永久

**PriorityNewestAddressStrategy.java** — 按类型选最新地址

每种类型选修改时间最大的，修改时间为空则视为最小。

### 3.5 service/

**ClientAddressService.java** — 核心业务服务

```java
class ClientAddressService {
    private ClientAddressRepository repo;
    private MailingAddressStrategy mailingStrategy;
    private NewestAddressStrategy newestStrategy;

    List<CifAddress> updateAddresses(String clientNo, List<CifAddress> incoming);
}
```

**AddressMerger.java** — 地址合并逻辑

按 addressType + addressDetail 去重合并。

## 4. 核心算法详解

### 4.1 地址合并规则

上送地址合并：修改时间为当前日期；通讯地址/最新地址任一个有Y则为Y

存量地址合并：修改时间为当前日期；通讯地址/最新地址任一个有Y则为Y；合并后删除被合并的记录

### 4.2 通讯地址选择规则

优先级顺序选择；若有修改时间则选最大，为空则视为最小

### 4.3 最新地址选择规则

每种类型选修改时间最大的；修改时间为空则视为最小

### 4.4 新增/更新判定

- addressType + addressDetail 在存量中存在 → 更新（seqNo 继承存量）
- 不存在 → 新增（seqNo 为空）

## 5. 技术约束

- Java8 + Maven
- 使用 RuntimeException，不新建异常类
- 存储层使用 List，不使用 Map
- del_flag='Y' 的记录不参与业务规则
