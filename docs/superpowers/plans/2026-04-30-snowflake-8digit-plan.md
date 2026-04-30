# SnowflakeIdGenerator 8位数字生成方法实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**目标:** 为 SnowflakeIdGenerator 新增 generate8DigitId 方法，生成8位数字用于页面展示

**架构:** 基于时间戳后6位 + SecureRandom 2位随机数拼接，避免 long 类型在前端溢出

**技术栈:** Java 8, SecureRandom, JUnit

---

## 文件变更

- Modify: `src/main/java/com/address/utils/SnowflakeIdGenerator.java`
- Modify: `src/test/java/com/address/utils/SnowflakeIdGeneratorTest.java`

---

## Task 1: 新增 generate8DigitId 方法

**Modify:** `src/main/java/com/address/utils/SnowflakeIdGenerator.java`

- [ ] **Step 1: 添加 SecureRandom 字段**

在类成员变量区域添加：
```java
private SecureRandom random = new SecureRandom();
```

- [ ] **Step 2: 添加 generate8DigitId 方法**

在 `nextIdAsString()` 方法后（第73行）添加：
```java
public synchronized String generate8DigitId() {
    long timestamp = System.currentTimeMillis() % 100000000;
    int randomPart = random.nextInt(100);
    return String.format("%06d%02d", timestamp, randomPart);
}
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/utils/SnowflakeIdGenerator.java
git commit -m "feat: 添加 generate8DigitId 方法生成8位数字用于页面展示"
```

---

## Task 2: 添加单元测试

**Modify:** `src/test/java/com/address/utils/SnowflakeIdGeneratorTest.java`

- [ ] **Step 1: 添加测试方法**

在类中添加测试方法：
```java
@Test
public void testGenerate8DigitId() {
    SnowflakeIdGenerator generator = SnowflakeIdGenerator.getInstance();
    String id = generator.generate8DigitId();
    assertNotNull(id);
    assertEquals(8, id.length());
    assertTrue(id.matches("\\d{8}"));
}
```

- [ ] **Step 2: 运行测试验证**

```bash
mvn test -Dtest=SnowflakeIdGeneratorTest
```

预期输出：
```
Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

- [ ] **Step 3: 提交**

```bash
git add src/test/java/com/address/utils/SnowflakeIdGeneratorTest.java
git commit -m "test: 添加 generate8DigitId 单元测试"
```

---

## 自检清单

- [ ] 方法签名正确：`public synchronized String generate8DigitId()`
- [ ] 返回类型为 String，避免前端整数溢出
- [ ] 使用 SecureRandom 保证随机性
- [ ] 时间戳取后6位：`% 100000000`
- [ ] 格式化为6位 + 2位：`String.format("%06d%02d", ...)`
- [ ] 测试覆盖：非空、长度8、全数字验证