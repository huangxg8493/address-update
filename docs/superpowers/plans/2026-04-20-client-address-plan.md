# 客户地址信息维护 - 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现客户地址信息维护类库，包含地址合并、通讯地址选择、最新地址选择三大核心功能

**Architecture:** 分层设计：Model → Repository → Strategy → Service。Repository 通过接口隔离存储实现，Strategy 通过接口隔离选择逻辑，便于后续扩展 MySQL 存储和替换选择策略

**Tech Stack:** Java8 + Maven

---

## 文件结构

```
src/main/java/com/address/
  ├─ model/
  │   ├─ CifAddress.java          // 地址实体
  │   └─ AddressType.java         // 地址类型枚举（10种）
  │
  ├─ repository/
  │   ├─ ClientAddressRepository.java         // 仓储接口
  │   └─ MemoryClientAddressRepository.java   // 内存实现
  │
  ├─ strategy/
  │   ├─ MailingAddressStrategy.java   // 通讯地址策略接口
  │   ├─ NewestAddressStrategy.java    // 最新地址策略接口
  │   ├─ impl/
  │   │   ├─ PriorityMailingAddressStrategy.java   // 优先级通讯地址策略
  │   │   └─ PriorityNewestAddressStrategy.java    // 优先级最新地址策略
  │   └─ impl/
  │       └─ AddressSelector.java  // 地址选择器（辅助工具）
  │
  ├─ service/
  │   ├─ ClientAddressService.java   // 核心服务
  │   └─ AddressMerger.java          // 合并逻辑
  │
  └─ exception/
        └─ AddressBusinessException.java  // 业务异常（继承RuntimeException）

src/test/java/com/address/
  ├─ model/
  │   └─ CifAddressTest.java
  │
  ├─ repository/
  │   └─ MemoryClientAddressRepositoryTest.java
  │
  ├─ strategy/
  │   ├─ PriorityMailingAddressStrategyTest.java
  │   └─ PriorityNewestAddressStrategyTest.java
  │
  ├─ service/
  │   ├─ AddressMergerTest.java
  │   └─ ClientAddressServiceTest.java
  │
  └─ integration/
        └─ ClientAddressServiceIntegrationTest.java
```

---

## Task 1: Maven 项目初始化

**Files:**
- Create: `pom.xml`
- Create: `src/main/java/com/address/model/CifAddress.java`
- Create: `src/test/java/com/address/model/CifAddressTest.java`

- [ ] **Step 1: 创建 pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.address</groupId>
    <artifactId>client-address-service</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 2: 创建 CifAddress.java**

```java
package com.address.model;

import java.util.Date;

public class CifAddress {
    private String seqNo;
    private String clientNo;
    private String addressType;
    private String addressDetail;
    private Date lastChangeDate;
    private String isMailingAddress;
    private String isNewest;
    private String delFlag;

    public CifAddress() {
    }

    public CifAddress(String clientNo, String addressType, String addressDetail) {
        this.clientNo = clientNo;
        this.addressType = addressType;
        this.addressDetail = addressDetail;
        this.isMailingAddress = "N";
        this.isNewest = "N";
        this.delFlag = "N";
    }

    // Getters and Setters
    public String getSeqNo() { return seqNo; }
    public void setSeqNo(String seqNo) { this.seqNo = seqNo; }
    public String getClientNo() { return clientNo; }
    public void setClientNo(String clientNo) { this.clientNo = clientNo; }
    public String getAddressType() { return addressType; }
    public void setAddressType(String addressType) { this.addressType = addressType; }
    public String getAddressDetail() { return addressDetail; }
    public void setAddressDetail(String addressDetail) { this.addressDetail = addressDetail; }
    public Date getLastChangeDate() { return lastChangeDate; }
    public void setLastChangeDate(Date lastChangeDate) { this.lastChangeDate = lastChangeDate; }
    public String getIsMailingAddress() { return isMailingAddress; }
    public void setIsMailingAddress(String isMailingAddress) { this.isMailingAddress = isMailingAddress; }
    public String getIsNewest() { return isNewest; }
    public void setIsNewest(String isNewest) { this.isNewest = isNewest; }
    public String getDelFlag() { return delFlag; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }

    @Override
    public String toString() {
        return "CifAddress{" +
                "seqNo='" + seqNo + '\'' +
                ", clientNo='" + clientNo + '\'' +
                ", addressType='" + addressType + '\'' +
                ", addressDetail='" + addressDetail + '\'' +
                ", isMailingAddress='" + isMailingAddress + '\'' +
                ", isNewest='" + isNewest + '\'' +
                '}';
    }
}
```

- [ ] **Step 3: 创建 CifAddressTest.java**

```java
package com.address.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CifAddressTest {

    @Test
    void testConstructor() {
        CifAddress address = new CifAddress("C001", "01", "北京市朝阳区");
        assertEquals("C001", address.getClientNo());
        assertEquals("01", address.getAddressType());
        assertEquals("北京市朝阳区", address.getAddressDetail());
        assertEquals("N", address.getIsMailingAddress());
        assertEquals("N", address.getIsNewest());
        assertEquals("N", address.getDelFlag());
    }

    @Test
    void testSettersAndGetters() {
        CifAddress address = new CifAddress();
        address.setSeqNo("A001");
        address.setClientNo("C001");
        address.setAddressType("01");
        address.setAddressDetail("北京市朝阳区");
        address.setIsMailingAddress("Y");
        address.setIsNewest("Y");
        address.setDelFlag("N");

        assertEquals("A001", address.getSeqNo());
        assertEquals("C001", address.getClientNo());
        assertEquals("01", address.getAddressType());
        assertEquals("北京市朝阳区", address.getAddressDetail());
        assertEquals("Y", address.getIsMailingAddress());
        assertEquals("Y", address.getIsNewest());
    }
}
```

- [ ] **Step 4: 运行测试验证**

Run: `mvn test -Dtest=CifAddressTest`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
git add pom.xml src/main/java/com/address/model/CifAddress.java src/test/java/com/address/model/CifAddressTest.java
git commit -m "feat: 添加 CifAddress 实体类及基本测试"
```

---

## Task 2: AddressType 枚举

**Files:**
- Create: `src/main/java/com/address/model/AddressType.java`
- Create: `src/test/java/com/address/model/AddressTypeTest.java`

- [ ] **Step 1: 创建 AddressType.java**

```java
package com.address.model;

public enum AddressType {
    OTHER("01"),           // 其他地址
    CONTACT("02"),         // 联系地址
    RESIDENCE("03"),       // 居住地址
    COMPANY("04"),         // 单位地址
    HOUSEHOLD("05"),       // 户籍地址
    CERTIFICATE("06"),     // 证件地址
    BUSINESS("07"),        // 营业地址
    REGISTERED("08"),      // 注册地址
    OFFICE("09"),          // 办公地址
    PERMANENT("10");       // 永久地址

    private final String code;

    AddressType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AddressType fromCode(String code) {
        for (AddressType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new RuntimeException("无效的地址类型编码: " + code);
    }
}
```

- [ ] **Step 2: 创建 AddressTypeTest.java**

```java
package com.address.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AddressTypeTest {

    @Test
    void testEnumValues() {
        assertEquals(10, AddressType.values().length);
    }

    @Test
    void testFromCode() {
        assertEquals(AddressType.CONTACT, AddressType.fromCode("02"));
        assertEquals(AddressType.RESIDENCE, AddressType.fromCode("03"));
    }

    @Test
    void testFromCodeInvalid() {
        try {
            AddressType.fromCode("99");
            fail("应该抛出异常");
        } catch (RuntimeException e) {
            assertTrue(e.getMessage().contains("无效的地址类型编码"));
        }
    }
}
```

- [ ] **Step 3: 运行测试验证**

Run: `mvn test -Dtest=AddressTypeTest`
Expected: PASS

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/address/model/AddressType.java src/test/java/com/address/model/AddressTypeTest.java
git commit -m "feat: 添加 AddressType 枚举"
```

---

## Task 3: Repository 层

**Files:**
- Create: `src/main/java/com/address/repository/ClientAddressRepository.java`
- Create: `src/main/java/com/address/repository/MemoryClientAddressRepository.java`
- Create: `src/test/java/com/address/repository/MemoryClientAddressRepositoryTest.java`

- [ ] **Step 1: 创建 ClientAddressRepository.java**

```java
package com.address.repository;

import com.address.model.CifAddress;
import java.util.List;

public interface ClientAddressRepository {
    List<CifAddress> findByClientNo(String clientNo);
    void save(CifAddress address);
    void update(CifAddress address);
    void saveAll(List<CifAddress> addresses);
    void updateAll(List<CifAddress> addresses);
    void delete(String seqNo);
}
```

- [ ] **Step 2: 创建 MemoryClientAddressRepository.java**

```java
package com.address.repository;

import com.address.model.CifAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemoryClientAddressRepository implements ClientAddressRepository {
    private final List<CifAddress> allAddresses = new ArrayList<>();

    @Override
    public List<CifAddress> findByClientNo(String clientNo) {
        return allAddresses.stream()
                .filter(a -> clientNo.equals(a.getClientNo()))
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .collect(Collectors.toList());
    }

    @Override
    public void save(CifAddress address) {
        allAddresses.add(address);
    }

    @Override
    public void update(CifAddress address) {
        for (int i = 0; i < allAddresses.size(); i++) {
            if (address.getSeqNo().equals(allAddresses.get(i).getSeqNo())) {
                allAddresses.set(i, address);
                return;
            }
        }
    }

    @Override
    public void saveAll(List<CifAddress> addresses) {
        allAddresses.addAll(addresses);
    }

    @Override
    public void updateAll(List<CifAddress> addresses) {
        for (CifAddress address : addresses) {
            update(address);
        }
    }

    @Override
    public void delete(String seqNo) {
        for (int i = 0; i < allAddresses.size(); i++) {
            if (seqNo.equals(allAddresses.get(i).getSeqNo())) {
                allAddresses.get(i).setDelFlag("Y");
                return;
            }
        }
    }

    public void clear() {
        allAddresses.clear();
    }
}
```

- [ ] **Step 3: 创建 MemoryClientAddressRepositoryTest.java**

```java
package com.address.repository;

import com.address.model.CifAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MemoryClientAddressRepositoryTest {

    private MemoryClientAddressRepository repo;

    @BeforeEach
    void setUp() {
        repo = new MemoryClientAddressRepository();
    }

    @Test
    void testSaveAndFind() {
        CifAddress addr = new CifAddress("C001", "01", "北京市");
        addr.setSeqNo("A001");
        repo.save(addr);

        List<CifAddress> result = repo.findByClientNo("C001");
        assertEquals(1, result.size());
        assertEquals("A001", result.get(0).getSeqNo());
    }

    @Test
    void testFindByClientNoExcludesDeleted() {
        CifAddress addr = new CifAddress("C001", "01", "北京市");
        addr.setSeqNo("A001");
        repo.save(addr);
        repo.delete("A001");

        List<CifAddress> result = repo.findByClientNo("C001");
        assertEquals(0, result.size());
    }

    @Test
    void testUpdate() {
        CifAddress addr = new CifAddress("C001", "01", "北京市");
        addr.setSeqNo("A001");
        repo.save(addr);

        addr.setAddressDetail("北京市朝阳区");
        repo.update(addr);

        List<CifAddress> result = repo.findByClientNo("C001");
        assertEquals(1, result.size());
        assertEquals("北京市朝阳区", result.get(0).getAddressDetail());
    }
}
```

- [ ] **Step 4: 运行测试验证**

Run: `mvn test -Dtest=MemoryClientAddressRepositoryTest`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/address/repository/ClientAddressRepository.java
git add src/main/java/com/address/repository/MemoryClientAddressRepository.java
git add src/test/java/com/address/repository/MemoryClientAddressRepositoryTest.java
git commit -m "feat: 添加 Repository 层接口及内存实现"
```

---

## Task 4: 地址选择策略

**Files:**
- Create: `src/main/java/com/address/strategy/MailingAddressStrategy.java`
- Create: `src/main/java/com/address/strategy/NewestAddressStrategy.java`
- Create: `src/main/java/com/address/strategy/impl/PriorityMailingAddressStrategy.java`
- Create: `src/main/java/com/address/strategy/impl/PriorityNewestAddressStrategy.java`
- Create: `src/test/java/com/address/strategy/PriorityMailingAddressStrategyTest.java`
- Create: `src/test/java/com/address/strategy/PriorityNewestAddressStrategyTest.java`

- [ ] **Step 1: 创建 MailingAddressStrategy.java**

```java
package com.address.strategy;

import com.address.model.CifAddress;
import java.util.List;

public interface MailingAddressStrategy {
    CifAddress select(List<CifAddress> addresses);
}
```

- [ ] **Step 2: 创建 NewestAddressStrategy.java**

```java
package com.address.strategy;

import com.address.model.CifAddress;
import java.util.List;
import java.util.Map;

public interface NewestAddressStrategy {
    Map<String, CifAddress> selectByType(List<CifAddress> addresses);
}
```

- [ ] **Step 3: 创建 PriorityMailingAddressStrategy.java**

```java
package com.address.strategy.impl;

import com.address.model.AddressType;
import com.address.model.CifAddress;
import com.address.strategy.MailingAddressStrategy;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class PriorityMailingAddressStrategy implements MailingAddressStrategy {

    @Override
    public CifAddress select(List<CifAddress> addresses) {
        Optional<CifAddress> mailingAddr = addresses.stream()
                .filter(a -> "Y".equals(a.getIsMailingAddress()))
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .max(Comparator.comparing(a -> a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE));

        if (mailingAddr.isPresent()) {
            return mailingAddr.get();
        }

        for (AddressType type : AddressType.values()) {
            Optional<CifAddress> latest = addresses.stream()
                    .filter(a -> type.name().equals(a.getAddressType()))
                    .filter(a -> !"Y".equals(a.getDelFlag()))
                    .max(Comparator.comparing(a -> a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE));

            if (latest.isPresent()) {
                return latest.get();
            }
        }
        return null;
    }
}
```

- [ ] **Step 4: 创建 PriorityNewestAddressStrategy.java**

```java
package com.address.strategy.impl;

import com.address.model.CifAddress;
import com.address.strategy.NewestAddressStrategy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PriorityNewestAddressStrategy implements NewestAddressStrategy {

    @Override
    public Map<String, CifAddress> selectByType(List<CifAddress> addresses) {
        Map<String, CifAddress> result = new HashMap<>();

        Map<String, List<CifAddress>> byType = addresses.stream()
                .filter(a -> !"Y".equals(a.getDelFlag()))
                .collect(Collectors.groupingBy(CifAddress::getAddressType));

        for (Map.Entry<String, List<CifAddress>> entry : byType.entrySet()) {
            Optional<CifAddress> newest = entry.getValue().stream()
                    .max((a, b) -> {
                        long timeA = a.getLastChangeDate() != null ? a.getLastChangeDate().getTime() : Long.MIN_VALUE;
                        long timeB = b.getLastChangeDate() != null ? b.getLastChangeDate().getTime() : Long.MIN_VALUE;
                        return Long.compare(timeA, timeB);
                    });
            newest.ifPresent(addr -> result.put(entry.getKey(), addr));
        }

        return result;
    }
}
```

- [ ] **Step 5: 创建 PriorityMailingAddressStrategyTest.java**

```java
package com.address.strategy;

import com.address.model.CifAddress;
import com.address.strategy.impl.PriorityMailingAddressStrategy;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PriorityMailingAddressStrategyTest {

    private PriorityMailingAddressStrategy strategy = new PriorityMailingAddressStrategy();

    @Test
    void testSelectMailingAddressWhenExists() {
        List<CifAddress> addresses = new ArrayList<>();

        CifAddress addr1 = new CifAddress("C001", "02", "联系地址1");
        addr1.setSeqNo("A001");
        addr1.setIsMailingAddress("Y");
        addr1.setLastChangeDate(new Date(1000));
        addresses.add(addr1);

        CifAddress addr2 = new CifAddress("C001", "03", "居住地址1");
        addr2.setSeqNo("A002");
        addr2.setIsMailingAddress("N");
        addr2.setLastChangeDate(new Date(2000));
        addresses.add(addr2);

        CifAddress result = strategy.select(addresses);
        assertEquals("A001", result.getSeqNo());
    }

    @Test
    void testSelectByPriorityWhenNoMailingAddress() {
        List<CifAddress> addresses = new ArrayList<>();

        CifAddress addr1 = new CifAddress("C001", "01", "其他地址");
        addr1.setSeqNo("A001");
        addr1.setLastChangeDate(new Date(1000));
        addresses.add(addr1);

        CifAddress addr2 = new CifAddress("C001", "02", "联系地址");
        addr2.setSeqNo("A002");
        addr2.setLastChangeDate(new Date(2000));
        addresses.add(addr2);

        CifAddress result = strategy.select(addresses);
        assertEquals("A002", result.getSeqNo());
    }
}
```

- [ ] **Step 6: 创建 PriorityNewestAddressStrategyTest.java**

```java
package com.address.strategy;

import com.address.model.CifAddress;
import com.address.strategy.impl.PriorityNewestAddressStrategy;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PriorityNewestAddressStrategyTest {

    private PriorityNewestAddressStrategy strategy = new PriorityNewestAddressStrategy();

    @Test
    void testSelectByType() {
        List<CifAddress> addresses = new ArrayList<>();

        CifAddress addr1 = new CifAddress("C001", "02", "联系地址1");
        addr1.setSeqNo("A001");
        addr1.setLastChangeDate(new Date(1000));
        addresses.add(addr1);

        CifAddress addr2 = new CifAddress("C001", "02", "联系地址2");
        addr2.setSeqNo("A002");
        addr2.setLastChangeDate(new Date(2000));
        addresses.add(addr2);

        CifAddress addr3 = new CifAddress("C001", "03", "居住地址");
        addr3.setSeqNo("A003");
        addr3.setLastChangeDate(new Date(3000));
        addresses.add(addr3);

        Map<String, CifAddress> result = strategy.selectByType(addresses);
        assertEquals(2, result.size());
        assertEquals("A002", result.get("02").getSeqNo());
        assertEquals("A003", result.get("03").getSeqNo());
    }
}
```

- [ ] **Step 7: 运行测试验证**

Run: `mvn test -Dtest=PriorityMailingAddressStrategyTest,PriorityNewestAddressStrategyTest`
Expected: PASS

- [ ] **Step 8: 提交**

```bash
git add src/main/java/com/address/strategy/MailingAddressStrategy.java
git add src/main/java/com/address/strategy/NewestAddressStrategy.java
git add src/main/java/com/address/strategy/impl/PriorityMailingAddressStrategy.java
git add src/main/java/com/address/strategy/impl/PriorityNewestAddressStrategy.java
git add src/test/java/com/address/strategy/PriorityMailingAddressStrategyTest.java
git add src/test/java/com/address/strategy/PriorityNewestAddressStrategyTest.java
git commit -m "feat: 添加地址选择策略接口及实现"
```

---

## Task 5: 地址合并逻辑

**Files:**
- Create: `src/main/java/com/address/service/AddressMerger.java`
- Create: `src/test/java/com/address/service/AddressMergerTest.java`

- [ ] **Step 1: 创建 AddressMerger.java**

```java
package com.address.service;

import com.address.model.CifAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AddressMerger {

    public List<CifAddress> mergeIncoming(List<CifAddress> incoming) {
        Map<String, CifAddress> merged = new HashMap<>();

        for (CifAddress addr : incoming) {
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
            CifAddress existing = merged.get(key);

            if (existing == null) {
                merged.put(key, addr);
            } else {
                CifAddress mergedAddr = mergeTwo(existing, addr);
                merged.put(key, mergedAddr);
            }
        }

        return new ArrayList<>(merged.values());
    }

    public List<CifAddress> mergeStock(List<CifAddress> stock, List<CifAddress> toDelete) {
        Map<String, CifAddress> stockMap = new HashMap<>();
        for (CifAddress addr : stock) {
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
            stockMap.put(key, addr);
        }

        for (CifAddress addr : toDelete) {
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
            stockMap.remove(key);
        }

        return new ArrayList<>(stockMap.values());
    }

    private CifAddress mergeTwo(CifAddress a, CifAddress b) {
        CifAddress result = new CifAddress();
        result.setSeqNo(a.getSeqNo());
        result.setClientNo(a.getClientNo());
        result.setAddressType(a.getAddressType());
        result.setAddressDetail(a.getAddressDetail());
        result.setLastChangeDate(new Date());

        String mailing = "Y".equals(a.getIsMailingAddress()) || "Y".equals(b.getIsMailingAddress()) ? "Y" : "N";
        String newest = "Y".equals(a.getIsNewest()) || "Y".equals(b.getIsNewest()) ? "Y" : "N";

        result.setIsMailingAddress(mailing);
        result.setIsNewest(newest);
        result.setDelFlag("N");

        return result;
    }
}
```

- [ ] **Step 2: 创建 AddressMergerTest.java**

```java
package com.address.service;

import com.address.model.CifAddress;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AddressMergerTest {

    private AddressMerger merger = new AddressMerger();

    @Test
    void testMergeIncomingRemovesDuplicates() {
        List<CifAddress> incoming = new ArrayList<>();

        CifAddress addr1 = new CifAddress("C001", "02", "联系地址");
        addr1.setSeqNo("A001");
        incoming.add(addr1);

        CifAddress addr2 = new CifAddress("C001", "02", "联系地址");
        addr2.setSeqNo("A002");
        addr2.setIsMailingAddress("Y");
        incoming.add(addr2);

        List<CifAddress> result = merger.mergeIncoming(incoming);
        assertEquals(1, result.size());
        assertEquals("Y", result.get(0).getIsMailingAddress());
    }

    @Test
    void testMergeIncomingKeepsDifferentAddresses() {
        List<CifAddress> incoming = new ArrayList<>();

        CifAddress addr1 = new CifAddress("C001", "02", "联系地址1");
        addr1.setSeqNo("A001");
        incoming.add(addr1);

        CifAddress addr2 = new CifAddress("C001", "03", "居住地址");
        addr2.setSeqNo("A002");
        incoming.add(addr2);

        List<CifAddress> result = merger.mergeIncoming(incoming);
        assertEquals(2, result.size());
    }
}
```

- [ ] **Step 3: 运行测试验证**

Run: `mvn test -Dtest=AddressMergerTest`
Expected: PASS

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/address/service/AddressMerger.java
git add src/test/java/com/address/service/AddressMergerTest.java
git commit -m "feat: 添加 AddressMerger 地址合并逻辑"
```

---

## Task 6: ClientAddressService 核心服务

**Files:**
- Create: `src/main/java/com/address/service/ClientAddressService.java`
- Create: `src/test/java/com/address/service/ClientAddressServiceTest.java`
- Create: `src/test/java/com/address/integration/ClientAddressServiceIntegrationTest.java`

- [ ] **Step 1: 创建 ClientAddressService.java**

```java
package com.address.service;

import com.address.model.CifAddress;
import com.address.repository.ClientAddressRepository;
import com.address.strategy.MailingAddressStrategy;
import com.address.strategy.NewestAddressStrategy;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<CifAddress> stock = repository.findByClientNo(clientNo);

        List<CifAddress> mergedIncoming = merger.mergeIncoming(incoming);

        Map<String, String> seqNoMap = buildSeqNoMap(stock, mergedIncoming);

        List<CifAddress> toInsert = new ArrayList<>();
        List<CifAddress> toUpdate = new ArrayList<>();

        for (CifAddress addr : mergedIncoming) {
            String seqNo = seqNoMap.get(addr.getAddressType() + "_" + addr.getAddressDetail());
            if (seqNo == null) {
                addr.setSeqNo(generateId());
                toInsert.add(addr);
            } else {
                addr.setSeqNo(seqNo);
                toUpdate.add(addr);
            }
        }

        for (CifAddress addr : mergedIncoming) {
            addr.setIsMailingAddress("N");
            addr.setIsNewest("N");
        }

        CifAddress mailing = mailingStrategy.select(mergedIncoming);
        if (mailing != null) {
            mailing.setIsMailingAddress("Y");
            mailing.setIsNewest("Y");
        }

        Map<String, CifAddress> newestByType = newestStrategy.selectByType(mergedIncoming);
        for (Map.Entry<String, CifAddress> entry : newestByType.entrySet()) {
            if (mailing == null || !entry.getValue().getSeqNo().equals(mailing.getSeqNo())) {
                entry.getValue().setIsNewest("Y");
            }
        }

        List<CifAddress> toDelete = findDeletedAddresses(stock, mergedIncoming);
        for (CifAddress addr : toDelete) {
            repository.delete(addr.getSeqNo());
        }

        if (!toInsert.isEmpty()) {
            repository.saveAll(toInsert);
        }

        if (!toUpdate.isEmpty()) {
            repository.updateAll(toUpdate);
        }

        return repository.findByClientNo(clientNo);
    }

    private Map<String, String> buildSeqNoMap(List<CifAddress> stock, List<CifAddress> incoming) {
        Map<String, String> map = new HashMap<>();
        for (CifAddress addr : stock) {
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
            if (!map.containsKey(key)) {
                map.put(key, addr.getSeqNo());
            }
        }
        return map;
    }

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

    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
```

- [ ] **Step 2: 创建 ClientAddressServiceTest.java**

```java
package com.address.service;

import com.address.model.CifAddress;
import com.address.repository.ClientAddressRepository;
import com.address.repository.MemoryClientAddressRepository;
import com.address.strategy.MailingAddressStrategy;
import com.address.strategy.NewestAddressStrategy;
import com.address.strategy.impl.PriorityMailingAddressStrategy;
import com.address.strategy.impl.PriorityNewestAddressStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ClientAddressServiceTest {

    private ClientAddressService service;
    private MemoryClientAddressRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MemoryClientAddressRepository();
        MailingAddressStrategy mailingStrategy = new PriorityMailingAddressStrategy();
        NewestAddressStrategy newestStrategy = new PriorityNewestAddressStrategy();
        service = new ClientAddressService(repository, mailingStrategy, newestStrategy);
    }

    @Test
    void testFirstTimeAddAddress() {
        List<CifAddress> incoming = new ArrayList<>();
        CifAddress addr = new CifAddress("C001", "02", "联系地址");
        incoming.add(addr);

        List<CifAddress> result = service.updateAddresses("C001", incoming);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getSeqNo());
    }

    @Test
    void testUpdateExistingAddress() {
        CifAddress existing = new CifAddress("C001", "02", "联系地址");
        repository.save(existing);

        List<CifAddress> incoming = new ArrayList<>();
        CifAddress addr = new CifAddress("C001", "02", "联系地址");
        incoming.add(addr);

        List<CifAddress> result = service.updateAddresses("C001", incoming);

        assertEquals(1, result.size());
    }
}
```

- [ ] **Step 3: 创建 ClientAddressServiceIntegrationTest.java**

```java
package com.address.integration;

import com.address.model.CifAddress;
import com.address.repository.ClientAddressRepository;
import com.address.repository.MemoryClientAddressRepository;
import com.address.strategy.MailingAddressStrategy;
import com.address.strategy.NewestAddressStrategy;
import com.address.strategy.impl.PriorityMailingAddressStrategy;
import com.address.strategy.impl.PriorityNewestAddressStrategy;
import com.address.service.ClientAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ClientAddressServiceIntegrationTest {

    private ClientAddressService service;
    private ClientAddressRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MemoryClientAddressRepository();
        MailingAddressStrategy mailingStrategy = new PriorityMailingAddressStrategy();
        NewestAddressStrategy newestStrategy = new PriorityNewestAddressStrategy();
        service = new ClientAddressService(repository, mailingStrategy, newestStrategy);
    }

    @Test
    void testFullWorkflow() {
        List<CifAddress> firstIncoming = new ArrayList<>();
        CifAddress addr1 = new CifAddress("C001", "02", "联系地址");
        CifAddress addr2 = new CifAddress("C001", "03", "居住地址");
        firstIncoming.add(addr1);
        firstIncoming.add(addr2);

        List<CifAddress> firstResult = service.updateAddresses("C001", firstIncoming);
        assertEquals(2, firstResult.size());

        long firstMailingCount = firstResult.stream()
                .filter(a -> "Y".equals(a.getIsMailingAddress()))
                .count();
        assertEquals(1, firstMailingCount);
    }
}
```

- [ ] **Step 4: 运行测试验证**

Run: `mvn test -Dtest=ClientAddressServiceTest,ClientAddressServiceIntegrationTest`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/address/service/ClientAddressService.java
git add src/test/java/com/address/service/ClientAddressServiceTest.java
git add src/test/java/com/address/integration/ClientAddressServiceIntegrationTest.java
git commit -m "feat: 添加 ClientAddressService 核心服务及集成测试"
```

---

## Task 7: 异常处理

**Files:**
- Create: `src/main/java/com/address/exception/AddressBusinessException.java`

- [ ] **Step 1: 创建 AddressBusinessException.java**

```java
package com.address.exception;

public class AddressBusinessException extends RuntimeException {

    public AddressBusinessException(String message) {
        super(message);
    }

    public AddressBusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

- [ ] **Step 2: 运行测试验证**

Run: `mvn compile`
Expected: 编译成功

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/exception/AddressBusinessException.java
git commit -m "feat: 添加 AddressBusinessException 业务异常"
```

---

## Task 8: Maven 打包验证

**Files:**
- Modify: `pom.xml` (添加 jar 打包配置)

- [ ] **Step 1: 更新 pom.xml 添加打包配置**

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-jar-plugin</artifactId>
            <version>3.3.0</version>
        </plugin>
    </plugins>
</build>
```

- [ ] **Step 2: 运行打包验证**

Run: `mvn clean package`
Expected: BUILD SUCCESS，生成 `target/client-address-service-1.0.0.jar`

- [ ] **Step 3: 运行全部测试**

Run: `mvn test`
Expected: 所有测试通过

- [ ] **Step 4: 提交**

```bash
git add pom.xml
git commit -m "chore: 配置 Maven 打包"
```

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

**Plan complete and saved to `docs/superpowers/plans/2026-04-20-client-address-plan.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
