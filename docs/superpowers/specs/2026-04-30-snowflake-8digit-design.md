# SnowflakeIdGenerator 8位数字生成方法设计

## 背景

`SnowflakeIdGenerator.nextId()` 返回 64 位 long，传到前端页面会导致整数溢出。需要新增一个生成 8 位数字的方法用于页面展示。

## 设计方案

### 方法签名

```java
public String generate8DigitId()
```

### 生成规则

8位数字 = 6位时间戳 + 2位随机数

| 部分 | 来源 | 位数 |
|------|------|------|
| 时间戳 | `System.currentTimeMillis() % 100000000` 取后6位 | 6位 |
| 随机数 | `SecureRandom.nextInt(100)` 生成 0-99 | 2位 |

### 输出示例

| 时间戳 | 随机数 | 结果 |
|--------|--------|------|
| 123456 | 78 | `12345678` |
| 1 | 23 | `00000123` |

### 返回类型

`String` - 避免前端整数溢出

## 实现

```java
private SecureRandom random = new SecureRandom();

public synchronized String generate8DigitId() {
    long timestamp = System.currentTimeMillis() % 100000000;
    int randomPart = random.nextInt(100);
    return String.format("%06d%02d", timestamp, randomPart);
}
```

## 安全说明

- 随机数使用 `SecureRandom`，密码学安全
- 重复概率：1/100000000（亿分之一）