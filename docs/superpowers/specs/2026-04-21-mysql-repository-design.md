# MySQL 持久层实现方案

## 背景

当前持久层使用内存存储（MemoryClientAddressRepository），适用于开发测试。为支持生产环境，需切换为 MySQL 数据库存储。

## 架构

新增 `JdbcClientAddressRepository` 实现 `ClientAddressRepository` 接口。Service 层通过接口依赖，切换存储实现无需改动 Service 代码。

```
ClientAddressService
       ↓ 依赖接口
ClientAddressRepository (接口)
       ↑
  ┌────┴────┐
MemoryClientAddressRepository  ← 当前（保留用于测试）
JdbcClientAddressRepository    ← 新增
```

## 数据库配置

新增 `config.yaml`：

```yaml
database:
  driver: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://127.0.0.1:3306/testdb?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC
  username: admin
  password: admin
```

## 表结构

自动创建表 `cif_address`：

| 字段 | 类型 | 说明 |
|------|------|------|
| seq_no | VARCHAR(64) | 主键 |
| client_no | VARCHAR(32) | 客户号 |
| address_type | VARCHAR(2) | 地址类型 |
| address_detail | VARCHAR(256) | 地址详情 |
| last_change_date | DATETIME | 最后修改时间 |
| is_mailing_address | CHAR(1) | 是否通讯地址 |
| is_newest | CHAR(1) | 是否最新地址 |
| del_flag | CHAR(1) | 删除标记 |

## 新增文件

| 文件 | 说明 |
|------|------|
| `src/main/java/com/address/config/DbConfig.java` | YAML 配置加载工具 |
| `src/main/java/com/address/repository/JdbcClientAddressRepository.java` | JDBC 实现 |
| `src/main/resources/config.yaml` | 数据库配置 |
| `src/test/java/com/address/repository/JdbcClientAddressRepositoryTest.java` | JDBC 实现测试 |

## 修改文件

| 文件 | 改动 |
|------|------|
| `pom.xml` | 新增 MySQL JDBC 依赖 |
| `ClientAddressService.java` | 注入改为 Jdbc 实现 |

## 自动建表逻辑

启动时检查表是否存在，不存在则创建：
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

## 依赖

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>2.0</version>
</dependency>
```
