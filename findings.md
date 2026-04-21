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

### 正确理解参考算法

1. 获取存量数组
2. 合并上送地址（去重）
3. 标记存量删除项（del_flag=Y）
4. 遍历上送地址找匹配存量
5. 收集两个数组有效地址
6. 挑选 mailing 和 newestByType（仅收集，不设置标识）
7. 重置两个数组所有标识为 N
8. 设置 mailing 标识为 Y
9. 设置 newest 标识为 Y
10. 批量 insert
11. 遍历存量地址用匹配来源覆盖更新

### 关键约束
- 不需要 seqNoMap，直接匹配
- 批量 update 针对所有存量地址
- del_flag=Y 不参与业务规则

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
  ├─ constants/
  │   └─ Constants.java           # 魔法值常量
  └─ utils/
      └─ SnowflakeIdGenerator.java # 雪花算法 ID 生成器

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
  ├─ integration/
  │   └─ ClientAddressServiceIntegrationTest.java
  └─ utils/
      └─ SnowflakeIdGeneratorTest.java
```

---

## 提交记录

- 1c4df40: chore: 添加 CLAUDE.md 代码库指南
- 55148b4: refactor: PriorityNewestAddressStrategy.selectByType 改为空实现
- ec07053: refactor: 简化 PriorityMailingAddressStrategy.select 实现
- 0c5decf: refactor: 简化 PriorityMailingAddressStrategy，移除 for 循环中的 StreamAPI
- bb76ed4: fix: 第8条优先级实现修正
- f514a5e: feat: 实现 PriorityMailingAddressStrategy.select 通讯地址选择逻辑
- c44850b: feat: 实现 PriorityNewestAddressStrategy.selectByType 最新地址选择逻辑

---

## 重构完成

所有 Task 9-11 已完成，系统重构完毕。

---

## seqNo 雪花算法实现

### 背景
CifAddress 的 seqNo 字段用于唯一标识地址记录。当前使用 UUID 截取 16 位生成，存在极低碰撞概率。为保证全局唯一性，改为 Snowflake 算法。

### 设计决策
- **保持 String 类型** - Snowflake ID 转为字符串存储
- **固定 workerId/datacenterId** - workerId=1, datacenterId=1
- **手动实现** - 不依赖外部库，纯 Java 实现
- **时钟回拨处理** - 阻塞等待时钟追上

### 雪花算法结构
```
1 bit  | 41 bits          | 5 bits      | 5 bits       | 12 bits
------|------------------|-------------|--------------|--------------
0     | 时间戳（毫秒）      | workerId=1  | datacenterId=1 | 序列号
```

### 生成示例
```
694252380621615873  (String 类型)
```

### 相关文件
- `src/main/java/com/address/utils/SnowflakeIdGenerator.java` (新增)
- `src/test/java/com/address/utils/SnowflakeIdGeneratorTest.java` (新增)

---

## MySQL 持久层实现

### 背景
当前持久层使用内存存储（MemoryClientAddressRepository），适用于开发测试。为支持生产环境，需切换为 MySQL 数据库存储。

### 架构决策
- **JDBC 原生** - 直接使用 Connection/PreparedStatement，轻量无额外依赖
- **配置文件** - 使用 config.yaml 管理数据库连接配置
- **自动建表** - 启动时检查表是否存在，不存在则创建

### 数据库配置
```yaml
database:
  driver: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://127.0.0.1:3306/testdb?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC
  username: admin
  password: admin
```

### 表结构
```sql
CREATE TABLE IF NOT EXISTS cif_address (
    seq_no VARCHAR(64) PRIMARY KEY,
    client_no VARCHAR(32) NOT NULL,
    address_type VARCHAR(2) NOT NULL,
    address_detail VARCHAR(256) NOT NULL,
    last_change_date DATETIME,
    is_mailing_address CHAR(1) DEFAULT 'N',
    is_newest CHAR(1) DEFAULT 'N',
    del_flag CHAR(1) DEFAULT 'N',
    INDEX idx_client_no (client_no),
    INDEX idx_client_type (client_no, address_type)
);
```

### 相关文件
- `src/main/resources/config.yaml` (新增)
- `src/main/java/com/address/config/DbConfig.java` (新增)
- `src/main/java/com/address/repository/JdbcClientAddressRepository.java` (新增)
- `src/test/java/com/address/repository/JdbcClientAddressRepositoryTest.java` (新增)

---

## HikariCP 连接池实现

### 背景
当前 JdbcClientAddressRepository 每次数据库操作都通过 DriverManager.getConnection() 获取新连接，存在连接创建开销。为提升性能，引入 HikariCP 连接池。

### 架构决策
- **HikariCP** - 目前最流行的连接池，性能最高
- **配置文件** - 在 config.yaml 中添加连接池配置项
- **DataSource 单例** - DbConfig 初始化时创建 DataSource 单例

### 连接池配置
```yaml
hikari:
  maximumPoolSize: 10
  minimumIdle: 2
  connectionTimeout: 30000
  idleTimeout: 600000
  maxLifetime: 1800000
```

### 相关文件
- `pom.xml` (修改)
- `config.yaml` (修改)
- `DbConfig.java` (修改)
- `JdbcClientAddressRepository.java` (修改)
