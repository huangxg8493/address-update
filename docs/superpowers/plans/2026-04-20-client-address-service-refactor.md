# ClientAddressService 重构实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 按参考算法重构 ClientAddressService，使其正确处理两个数组的合并、新增/更新分类、规则应用和批量入库

**Architecture:** 采用参考算法的9步流程：合并→分类→规则应用→标识重置→标识设置→批量入库。策略接口仅负责挑选，结果存储后再由 Service 统一设置标识

**Tech Stack:** Java8 + Maven，内存存储，策略模式

---

## 文件结构

```
src/main/java/com/address/
├── service/
│   ├── ClientAddressService.java      # 主服务，重构核心
│   └── AddressMerger.java             # 地址合并（已有）
├── strategy/
│   ├── MailingAddressStrategy.java    # 通讯地址策略（已有）
│   ├── NewestAddressStrategy.java     # 最新地址策略（已有）
│   └── impl/
│       ├── PriorityMailingAddressStrategy.java    # 按优先级选通讯地址
│       └── PriorityNewestAddressStrategy.java      # 按类型选最新地址
├── model/
│   └── CifAddress.java                # 地址实体（已有）
└── repository/
    └── ClientAddressRepository.java   # 仓储接口（已有）
```

---

## Task 1: 修改 AddressMerger#mergeStock 支持标记删除

**Files:**
- Modify: `src/main/java/com/address/service/AddressMerger.java`

**目的:** mergeStock 方法需要能够标记哪些地址需要删除（del_flag=Y），而不是物理删除

- [ ] **Step 1: 读取当前 AddressMerger 实现**

确认 mergeStock 和 mergeIncoming 的当前逻辑

- [ ] **Step 2: 修改 mergeStock 返回所有地址，将待删除地址 del_flag 设为 Y**

```java
public List<CifAddress> mergeStock(List<CifAddress> stock, List<CifAddress> toDelete) {
    // 将待删除地址 del_flag 设为 Y，返回所有地址（包括标记删除的）
    for (CifAddress addr : stock) {
        String key = addr.getAddressType() + "_" + addr.getAddressDetail();
        for (CifAddress td : toDelete) {
            String tdKey = td.getAddressType() + "_" + td.getAddressDetail();
            if (key.equals(tdKey)) {
                addr.setDelFlag("Y");
                break;
            }
        }
    }
    return stock;
}
```

- [ ] **Step 3: 运行测试验证**

---

## Task 2: 重构 ClientAddressService#updateAddresses

**Files:**
- Modify: `src/main/java/com/address/service/ClientAddressService.java`

**目的:** 按参考算法9步流程重写主方法

- [ ] **Step 1: 编写新版本 updateAddresses**

```java
public List<CifAddress> updateAddresses(String clientNo, List<CifAddress> incoming) {
    // Step 1: 获取存量数组
    List<CifAddress> stock = repository.findByClientNo(clientNo);

    // Step 2: 合并两个数组
    // 2.1 找出待删除地址（存量中有但上送中无的）
    List<CifAddress> toDelete = findDeletedAddresses(stock, incoming);
    // 2.2 合并存量地址（标记删除项 del_flag=Y）
    List<CifAddress> mergedStock = merger.mergeStock(stock, toDelete);
    // 2.3 合并上送地址（去重）
    List<CifAddress> mergedIncoming = merger.mergeIncoming(incoming);

    // Step 3: 应用更新/新增规则（设置seqNo）
    Map<String, String> seqNoMap = buildSeqNoMap(mergedStock, mergedIncoming);
    List<CifAddress> insertList = new ArrayList<>();
    List<CifAddress> updateList = new ArrayList<>();

    for (CifAddress addr : mergedIncoming) {
        String key = addr.getAddressType() + "_" + addr.getAddressDetail();
        String seqNo = seqNoMap.get(key);
        if (seqNo == null) {
            addr.setSeqNo(null);  // 新增
            insertList.add(addr);
        } else {
            addr.setSeqNo(seqNo);  // 更新
            updateList.add(addr);
        }
    }

    // Step 4: 从两个数组（del_flag≠Y）中应用规则，仅收集结果
    List<CifAddress> allActive = new ArrayList<>();
    for (CifAddress addr : mergedStock) {
        if (!"Y".equals(addr.getDelFlag())) {
            allActive.add(addr);
        }
    }
    allActive.addAll(mergedIncoming);

    CifAddress mailing = mailingStrategy.select(allActive);
    Map<String, CifAddress> newestByType = newestStrategy.selectByType(allActive);

    // Step 5: 将两个数组中的标识都设置为 N
    for (CifAddress addr : mergedStock) {
        addr.setIsMailingAddress("N");
        addr.setIsNewest("N");
    }
    for (CifAddress addr : mergedIncoming) {
        addr.setIsMailingAddress("N");
        addr.setIsNewest("N");
    }

    // Step 6: 将挑选出的通讯地址的标识设置为 Y
    if (mailing != null) {
        mailing.setIsMailingAddress("Y");
        mailing.setIsNewest("Y");
    }

    // Step 7: 将挑选出的最新地址的标识设置为 Y
    for (Map.Entry<String, CifAddress> entry : newestByType.entrySet()) {
        if (mailing == null || !entry.getValue().getSeqNo().equals(mailing.getSeqNo())) {
            entry.getValue().setIsNewest("Y");
        }
    }

    // Step 8-9: 批量入库
    if (!insertList.isEmpty()) {
        for (CifAddress addr : insertList) {
            addr.setSeqNo(generateId());
        }
        repository.saveAll(insertList);
    }
    if (!updateList.isEmpty()) {
        repository.updateAll(updateList);
    }

    // 删除已标记的地址
    for (CifAddress addr : toDelete) {
        repository.delete(addr.getSeqNo());
    }

    return repository.findByClientNo(clientNo);
}
```

- [ ] **Step 2: 确保 findDeletedAddresses 方法存在且正确**

```java
private List<CifAddress> findDeletedAddresses(List<CifAddress> stock, List<CifAddress> incoming) {
    List<CifAddress> deleted = new ArrayList<>();
    for (CifAddress s : stock) {
        boolean found = false;
        for (CifAddress i : incoming) {
            if (s.getAddressType().equals(i.getAddressType()) &&
                s.getAddressDetail().equals(i.getAddressDetail())) {
                found = true;
                break;
            }
        }
        if (!found) {
            deleted.add(s);
        }
    }
    return deleted;
}
```

- [ ] **Step 3: 确保 buildSeqNoMap 方法正确构建**

```java
private Map<String, String> buildSeqNoMap(List<CifAddress> stock, List<CifAddress> incoming) {
    Map<String, String> map = new HashMap<>();
    for (CifAddress addr : stock) {
        if (!"Y".equals(addr.getDelFlag())) {
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
            if (!map.containsKey(key)) {
                map.put(key, addr.getSeqNo());
            }
        }
    }
    return map;
}
```

- [ ] **Step 4: 确保 generateId 方法存在**

```java
private String generateId() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
}
```

- [ ] **Step 5: 运行测试验证**

---

## Task 3: 验证和测试

**Files:**
- Test: `src/test/java/com/address/service/ClientAddressServiceTest.java`
- Test: `src/test/java/com/address/integration/ClientAddressServiceIntegrationTest.java`

- [ ] **Step 1: 运行 Maven 测试**

```bash
mvn test -Dtest=ClientAddressServiceTest,ClientAddressServiceIntegrationTest
```

- [ ] **Step 2: 检查所有测试是否通过**

Expected: 所有测试 PASS

- [ ] **Step 3: 提交变更**

---

## Self-Review 检查清单

1. **Spec coverage:** 参考算法9步流程是否全部覆盖？
2. **Placeholder scan:** 无"TBD"、"TODO"等占位符
3. **Type consistency:** 方法签名、字段名是否与现有代码一致？

---

## 执行选项

**1. Subagent-Driven (recommended)** - 使用 superpowers:subagent-driven-development
**2. Inline Execution** - 使用 superpowers:executing-plans

请选择执行方式。
