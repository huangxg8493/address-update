# seqNo 雪花算法生成方案

## 背景

CifAddress 的 seqNo 字段用于唯一标识地址记录。当前使用 UUID 截取 16 位生成，存在极低碰撞概率。为保证全局唯一性，改为 Snowflake 算法。

## 设计

### 核心逻辑

Snowflake ID 构成（64 位）：

```
1 bit  | 41 bits          | 5 bits      | 5 bits       | 12 bits
------|------------------|-------------|--------------|--------------
0     | 时间戳（毫秒）      | workerId=1  | datacenterId=1 | 序列号
```

### 新增文件

**SnowflakeIdGenerator.java** (`src/main/java/com/address/utils/SnowflakeIdGenerator.java`)

- 单例模式
- 固定 workerId=1, datacenterId=1
- 时钟回拨处理：阻塞等待时钟追上
- 生成 long 类型 ID，对外返回 String

### 改动点

| 文件 | 改动 |
|------|------|
| `SnowflakeIdGenerator.java` | 新增工具类 |
| `ClientAddressService.java` | `generateId()` 改为调用 SnowflakeIdGenerator.nextId() |

## 生成示例

```
694252380621615873  (String 类型)
```

## 依赖

无外部依赖，纯 Java 实现。
