# seqNo 雪花算法实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 CifAddress.seqNo 的生成方式从 UUID 截取改为 Snowflake 算法

**Architecture:** 新增 SnowflakeIdGenerator 工具类，单例模式，固定 workerId=1, datacenterId=1，生成 long 转 String。ClientAddressService.generateId() 委托其生成。

**Tech Stack:** 纯 Java 实现，无外部依赖

---

## 文件结构

| 操作 | 文件路径 |
|------|----------|
| 新增 | `src/main/java/com/address/utils/SnowflakeIdGenerator.java` |
| 修改 | `src/main/java/com/address/service/ClientAddressService.java` |
| 新增 | `src/test/java/com/address/utils/SnowflakeIdGeneratorTest.java` |

---

## Task 1: 创建 SnowflakeIdGenerator 工具类

**Files:**
- Create: `src/main/java/com/address/utils/SnowflakeIdGenerator.java`

- [ ] **Step 1: 编写测试用例**

```java
package com.address.utils;

import org.junit.Test;
import static org.junit.Assert.*;

public class SnowflakeIdGeneratorTest {

    @Test
    public void testNextId_ReturnsPositiveLong() {
        SnowflakeIdGenerator generator = SnowflakeIdGenerator.getInstance();
        long id = generator.nextId();
        assertTrue(id > 0);
    }

    @Test
    public void testNextId_ReturnsUniqueIds() {
        SnowflakeIdGenerator generator = SnowflakeIdGenerator.getInstance();
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        assertNotEquals(id1, id2);
    }

    @Test
    public void testNextIdAsString_ReturnsStringRepresentation() {
        SnowflakeIdGenerator generator = SnowflakeIdGenerator.getInstance();
        String idStr = generator.nextIdAsString();
        assertNotNull(idStr);
        assertTrue(Long.parseLong(idStr) > 0);
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `mvn test -Dtest=SnowflakeIdGeneratorTest`
Expected: FAIL - class does not exist

- [ ] **Step 3: 实现 SnowflakeIdGenerator**

```java
package com.address.utils;

/**
 * 雪花算法 ID 生成器
 * 结构: 1符号位 + 41时间戳 + 5 workerId + 5 datacenterId + 12序列号
 */
public class SnowflakeIdGenerator {

    private static final long START_EPOCH = 1609459200000L; // 2021-01-01 毫秒时间戳

    private static final long WORKER_ID_BITS = 5L;
    private static final long DATACENTER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS); // 31
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS); // 31

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS); // 4095

    private static final SnowflakeIdGenerator INSTANCE = new SnowflakeIdGenerator(1, 1);

    private final long workerId;
    private final long datacenterId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private SnowflakeIdGenerator(long workerId, long datacenterId) {
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("workerId 必须在 0-31 之间");
        }
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId 必须在 0-31 之间");
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public static SnowflakeIdGenerator getInstance() {
        return INSTANCE;
    }

    public synchronized long nextId() {
        long timestamp = timeGen();

        if (timestamp < lastTimestamp) {
            // 时钟回拨，等待追上
            timestamp = waitUntilNextMillis(lastTimestamp);
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                timestamp = waitUntilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - START_EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    public String nextIdAsString() {
        return String.valueOf(nextId());
    }

    private long timeGen() {
        return System.currentTimeMillis();
    }

    private long waitUntilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

Run: `mvn test -Dtest=SnowflakeIdGeneratorTest`
Expected: PASS

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/address/utils/SnowflakeIdGenerator.java src/test/java/com/address/utils/SnowflakeIdGeneratorTest.java
git commit -m "feat: 新增 SnowflakeIdGenerator 雪花算法 ID 生成器"
```

---

## Task 2: 修改 ClientAddressService 使用 SnowflakeIdGenerator

**Files:**
- Modify: `src/main/java/com/address/service/ClientAddressService.java`

- [ ] **Step 1: 添加 import**

在文件顶部添加:
```java
import com.address.utils.SnowflakeIdGenerator;
```

- [ ] **Step 2: 修改 generateId 方法**

将:
```java
private String generateId() {
    return UUID.randomUUID().toString().replace("-", "").substring(0, Constants.UUID_LENGTH);
}
```

改为:
```java
private String generateId() {
    return SnowflakeIdGenerator.getInstance().nextIdAsString();
}
```

- [ ] **Step 3: 运行所有测试确认通过**

Run: `mvn test`
Expected: PASS

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/address/service/ClientAddressService.java
git commit -m "refactor: seqNo 生成改用 Snowflake 算法"
```

---

## 自检清单

- [ ] SnowflakeIdGenerator 是否为单例
- [ ] workerId 和 datacenterId 是否固定为 1
- [ ] 时钟回拨是否有处理（阻塞等待）
- [ ] 返回类型是否为 String
- [ ] 所有测试是否通过
