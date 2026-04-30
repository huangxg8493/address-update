# Task Plan

> 项目：8位userId生成方法
> 目标：为 SnowflakeIdGenerator 新增 generate8DigitId 方法，生成8位数字用于页面展示
> 架构：6位时间戳 + 2位随机数
> 技术栈：Java8 + SecureRandom + JUnit

---

## 文件变更概览

| 操作 | 文件路径 | 说明 |
|------|----------|------|
| Modify | `src/main/java/com/address/utils/SnowflakeIdGenerator.java` | 添加 generate8DigitId 方法 |
| Modify | `src/test/java/com/address/utils/SnowflakeIdGeneratorTest.java` | 添加单元测试 |

---

## Task 1: 新增 generate8DigitId 方法

**Files:**
- Modify: `src/main/java/com/address/utils/SnowflakeIdGenerator.java`

- [x] **Step 1: 添加 SecureRandom 字段**

```java
private SecureRandom random = new SecureRandom();
```

- [x] **Step 2: 添加 generate8DigitId 方法**

```java
public synchronized String generate8DigitId() {
    long timestamp = System.currentTimeMillis() % 1000000;
    int randomPart = random.nextInt(100);
    return String.format("%06d%02d", timestamp, randomPart);
}
```

- [x] **Step 3: 验证编译**
- [x] **Step 4: 提交**

**Commit:** `0e6e4d6` - feat: 添加 generate8DigitId 方法生成8位数字用于页面展示

**修复 Commit:** `d10d6ce` - fix: 修复 generate8DigitId 时间戳取模错误，确保生成8位数字

---

## Task 2: 添加单元测试

**Files:**
- Modify: `src/test/java/com/address/utils/SnowflakeIdGeneratorTest.java`

- [x] **Step 1: 添加测试方法**

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

- [x] **Step 2: 运行测试验证**
- [x] **Step 3: 提交**

**Commit:** `f53156e` - test: 添加 generate8DigitId 单元测试

---

## 自检清单

- [x] 方法签名正确：`public synchronized String generate8DigitId()`
- [x] 返回类型为 String，避免前端整数溢出
- [x] 使用 SecureRandom 保证随机性
- [x] 时间戳取后6位：`% 1000000`
- [x] 格式化为6位 + 2位：`String.format("%06d%02d", ...)`
- [x] 测试覆盖：非空、长度8、全数字验证

---

## 最终验证

- [x] 所有测试通过：`mvn test -Dtest=SnowflakeIdGeneratorTest` → BUILD SUCCESS
- [x] 代码已提交并推送