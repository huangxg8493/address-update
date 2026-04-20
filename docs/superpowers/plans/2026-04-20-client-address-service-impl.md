# ClientAddressService 重构实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 按参考算法11步流程重构 ClientAddressService

**Architecture:** 单方法 Service，直接匹配地址，策略接口处理选择规则

**Tech Stack:** Java8 + Maven

---

## 文件结构

```
src/main/java/com/address/
├── service/
│   └── ClientAddressService.java      # 重写
├── strategy/
│   ├── MailingAddressStrategy.java    # 已有
│   ├── NewestAddressStrategy.java     # 已有
│   └── impl/
│       ├── PriorityMailingAddressStrategy.java
│       └── PriorityNewestAddressStrategy.java
└── repository/
    └── ClientAddressRepository.java   # 已有
```

---

## Task 1: 删除并重写 ClientAddressService

**Files:**
- Delete: `src/main/java/com/address/service/ClientAddressService.java`
- Create: `src/main/java/com/address/service/ClientAddressService.java`

- [ ] **Step 1: 删除现有 ClientAddressService.java**

```bash
rm D:/AI/address-update/src/main/java/com/address/service/ClientAddressService.java
```

- [ ] **Step 2: 编写新版本 ClientAddressService.java**

```java
package com.address.service;

import com.address.model.CifAddress;
import com.address.repository.ClientAddressRepository;
import com.address.strategy.MailingAddressStrategy;
import com.address.strategy.NewestAddressStrategy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClientAddressService {
    private final ClientAddressRepository repository;
    private final MailingAddressStrategy mailingStrategy;
    private final NewestAddressStrategy newestStrategy;
    private final AddressMerger merger;

    public ClientAddressService(ClientAddressRepository repository,
                                MailingAddressStrategy mailingStrategy,
                                NewestAddressStrategy newestStrategy) {
        this.repository = repository;
        this.mailingStrategy = mailingStrategy;
        this.newestStrategy = newestStrategy;
        this.merger = new AddressMerger();
    }

    public List<CifAddress> updateAddresses(String clientNo, List<CifAddress> incoming) {
        // Step 1: 获取存量数组
        List<CifAddress> stock = repository.findByClientNo(clientNo);

        // Step 2: 合并上送地址（去重）
        List<CifAddress> mergedIncoming = merger.mergeIncoming(incoming);

        // Step 3: 标记存量删除项（del_flag=Y）
        List<CifAddress> mergedStock = merger.mergeStock(stock, mergedIncoming);

        // Step 4: 遍历上送地址找匹配存量
        List<CifAddress> insertList = new ArrayList<>();
        for (CifAddress addr : mergedIncoming) {
            CifAddress matched = findMatchedStock(addr, mergedStock);
            if (matched != null) {
                addr.setSeqNo(matched.getSeqNo());
            } else {
                addr.setSeqNo(null);
                insertList.add(addr);
            }
        }

        // Step 5: 收集两个数组有效地址
        List<CifAddress> allActive = new ArrayList<>();
        for (CifAddress addr : mergedStock) {
            if (!"Y".equals(addr.getDelFlag())) {
                allActive.add(addr);
            }
        }
        allActive.addAll(mergedIncoming);

        // Step 6: 挑选 mailing 和 newestByType（仅收集，不设置标识）
        CifAddress mailing = mailingStrategy.select(allActive);
        java.util.Map<String, CifAddress> newestByType = newestStrategy.selectByType(allActive);

        // Step 7: 重置两个数组所有标识为 N
        for (CifAddress addr : mergedStock) {
            addr.setIsMailingAddress("N");
            addr.setIsNewest("N");
        }
        for (CifAddress addr : mergedIncoming) {
            addr.setIsMailingAddress("N");
            addr.setIsNewest("N");
        }

        // Step 8: 设置 mailing 标识为 Y
        if (mailing != null) {
            mailing.setIsMailingAddress("Y");
            mailing.setIsNewest("Y");
        }

        // Step 9: 设置 newest 标识为 Y
        for (java.util.Map.Entry<String, CifAddress> entry : newestByType.entrySet()) {
            CifAddress newestAddr = entry.getValue();
            if (!Objects.equals(mailing, newestAddr)) {
                newestAddr.setIsNewest("Y");
            }
        }

        // Step 10: 批量 insert
        for (CifAddress addr : insertList) {
            addr.setSeqNo(generateId());
        }
        if (!insertList.isEmpty()) {
            repository.saveAll(insertList);
        }

        // Step 11: 遍历存量地址，用匹配来源覆盖更新
        for (CifAddress stockAddr : mergedStock) {
            if (stockAddr.getSeqNo() != null) {
                CifAddress source = findBySeqNo(stockAddr.getSeqNo(), mergedIncoming);
                if (source != null) {
                    stockAddr.setAddressType(source.getAddressType());
                    stockAddr.setAddressDetail(source.getAddressDetail());
                    stockAddr.setLastChangeDate(new Date());
                    stockAddr.setIsMailingAddress(source.getIsMailingAddress());
                    stockAddr.setIsNewest(source.getIsNewest());
                }
                repository.update(stockAddr);
            }
        }

        return repository.findByClientNo(clientNo);
    }

    private CifAddress findMatchedStock(CifAddress addr, List<CifAddress> stock) {
        for (CifAddress s : stock) {
            if (!"Y".equals(s.getDelFlag()) &&
                Objects.equals(s.getAddressType(), addr.getAddressType()) &&
                Objects.equals(s.getAddressDetail(), addr.getAddressDetail())) {
                return s;
            }
        }
        return null;
    }

    private CifAddress findBySeqNo(String seqNo, List<CifAddress> addresses) {
        for (CifAddress addr : addresses) {
            if (Objects.equals(addr.getSeqNo(), seqNo)) {
                return addr;
            }
        }
        return null;
    }

    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
```

- [ ] **Step 3: 运行测试验证**

```bash
mvn test -Dtest=ClientAddressServiceTest,ClientAddressServiceIntegrationTest
```

Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

---

## Task 2: 验证 AddressMerger.mergeStock

**Files:**
- Check: `src/main/java/com/address/service/AddressMerger.java`

- [ ] **Step 1: 确认 mergeStock 支持 del_flag 标记删除**

检查 mergeStock 方法是否已按之前修改支持标记删除。如果不是，修改它。

- [ ] **Step 2: 运行测试验证**

```bash
mvn test -Dtest=AddressMergerTest
```

Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

---

## Task 3: 全部测试验证

**Files:**
- Test: `src/test/java/com/address/**/*.java`

- [ ] **Step 1: 运行全部测试**

```bash
mvn test
```

Expected: 15 tests, 0 failures, BUILD SUCCESS

- [ ] **Step 2: 提交**

---

## Self-Review 检查清单

1. **Spec coverage:** 11 步流程是否全部覆盖？
2. **Placeholder scan:** 无"TBD"、"TODO"等占位符
3. **Type consistency:** 方法签名、字段名是否与现有代码一致？

---

## 执行选项

**1. Subagent-Driven (recommended)** - 使用 superpowers:subagent-driven-development
**2. Inline Execution** - 使用 superpowers:executing-plans

请选择执行方式。
