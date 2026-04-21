# HikariCP 连接池实现方案

## 背景

当前 JdbcClientAddressRepository 每次数据库操作都通过 `DriverManager.getConnection()` 获取新连接，存在连接创建开销。为提升性能，引入 HikariCP 连接池。

## 架构

在 `DbConfig` 中新增 HikariCP DataSource 单例，所有数据库操作通过连接池获取连接，替代当前的 `DriverManager.getConnection()`。

## 配置

### config.yaml

```yaml
database:
  driver: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://127.0.0.1:3306/testdb?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC
  username: admin
  password: admin

hikari:
  maximumPoolSize: 10
  minimumIdle: 2
  connectionTimeout: 30000
  idleTimeout: 600000
  maxLifetime: 1800000
```

## 改动文件

| 文件 | 说明 |
|------|------|
| `pom.xml` | 新增 HikariCP 依赖 |
| `config.yaml` | 新增连接池配置 |
| `DbConfig.java` | 新增 DataSource 初始化 |
| `JdbcClientAddressRepository.java` | 改为从 DataSource 获取连接 |

## 核心逻辑

```java
private static DataSource dataSource;

static {
    HikariConfig config = new HikariConfig();
    config.setJdbcUrl(DbConfig.getUrl());
    config.setUsername(DbConfig.getUsername());
    config.setPassword(DbConfig.getPassword());
    config.setMaximumPoolSize(DbConfig.getMaximumPoolSize());
    config.setMinimumIdle(DbConfig.getMinimumIdle());
    config.setConnectionTimeout(DbConfig.getConnectionTimeout());
    config.setIdleTimeout(DbConfig.getIdleTimeout());
    config.setMaxLifetime(DbConfig.getMaxLifetime());
    dataSource = new HikariDataSource(config);
}

private Connection getConnection() throws SQLException {
    return dataSource.getConnection();
}
```

## 依赖

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>
```
