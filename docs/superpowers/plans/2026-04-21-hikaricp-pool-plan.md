# HikariCP 连接池实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 引入 HikariCP 连接池，替代 DriverManager.getConnection() 每次创建新连接的方式

**Architecture:** 在 DbConfig 中新增 HikariCP DataSource 单例，所有数据库操作通过连接池获取连接

**Tech Stack:** HikariCP 4.0.3 + MySQL

---

## 文件结构

| 操作 | 文件路径 |
|------|----------|
| 修改 | `pom.xml` |
| 修改 | `config.yaml` |
| 修改 | `DbConfig.java` |
| 修改 | `JdbcClientAddressRepository.java` |

---

## Task 1: 添加 HikariCP Maven 依赖

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: 添加 HikariCP 依赖**

在 `<dependencies>` 中添加：

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>
```

- [ ] **Step 2: 验证依赖下载**

Run: `mvn dependency:resolve -q`
Expected: 无错误

- [ ] **Step 3: 提交**

```bash
git add pom.xml
git commit -m "deps: 添加 HikariCP 连接池依赖"
```

---

## Task 2: 更新配置文件

**Files:**
- Modify: `config.yaml`

- [ ] **Step 1: 添加 HikariCP 连接池配置**

将:

```yaml
database:
  driver: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://127.0.0.1:3306/testdb?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC
  username: admin
  password: admin
```

改为:

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

- [ ] **Step 2: 提交**

```bash
git add config.yaml
git commit -m "feat: 添加 HikariCP 连接池配置"
```

---

## Task 3: 修改 DbConfig 添加 DataSource

**Files:**
- Modify: `src/main/java/com/address/config/DbConfig.java`

- [ ] **Step 1: 添加 HikariCP import 和 DataSource**

将:

```java
package com.address.config;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class DbConfig {
    private static final Map<String, Object> config;

    static {
        Yaml yaml = new Yaml();
        try (InputStream in = DbConfig.class.getClassLoader().getResourceAsStream("config.yaml")) {
            config = yaml.load(in);
        } catch (Exception e) {
            throw new RuntimeException("加载 config.yaml 失败", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getDatabase() {
        return (Map<String, Object>) config.get("database");
    }

    public static String getDriver() {
        return (String) getDatabase().get("driver");
    }

    public static String getUrl() {
        return (String) getDatabase().get("url");
    }

    public static String getUsername() {
        return (String) getDatabase().get("username");
    }

    public static String getPassword() {
        return (String) getDatabase().get("password");
    }
}
```

改为:

```java
package com.address.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.yaml.snakeyaml.Yaml;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Map;

public class DbConfig {
    private static final Map<String, Object> config;
    private static final DataSource dataSource;

    static {
        Yaml yaml = new Yaml();
        try (InputStream in = DbConfig.class.getClassLoader().getResourceAsStream("config.yaml")) {
            config = yaml.load(in);
        } catch (Exception e) {
            throw new RuntimeException("加载 config.yaml 失败", e);
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getUrl());
        hikariConfig.setUsername(getUsername());
        hikariConfig.setPassword(getPassword());
        hikariConfig.setMaximumPoolSize(getMaximumPoolSize());
        hikariConfig.setMinimumIdle(getMinimumIdle());
        hikariConfig.setConnectionTimeout(getConnectionTimeout());
        hikariConfig.setIdleTimeout(getIdleTimeout());
        hikariConfig.setMaxLifetime(getMaxLifetime());
        dataSource = new HikariDataSource(hikariConfig);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getDatabase() {
        return (Map<String, Object>) config.get("database");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getHikari() {
        return (Map<String, Object>) config.get("hikari");
    }

    public static String getDriver() {
        return (String) getDatabase().get("driver");
    }

    public static String getUrl() {
        return (String) getDatabase().get("url");
    }

    public static String getUsername() {
        return (String) getDatabase().get("username");
    }

    public static String getPassword() {
        return (String) getDatabase().get("password");
    }

    public static int getMaximumPoolSize() {
        return (Integer) getHikari().getOrDefault("maximumPoolSize", 10);
    }

    public static int getMinimumIdle() {
        return (Integer) getHikari().getOrDefault("minimumIdle", 2);
    }

    public static long getConnectionTimeout() {
        return (Long) getHikari().getOrDefault("connectionTimeout", 30000L);
    }

    public static long getIdleTimeout() {
        return (Long) getHikari().getOrDefault("idleTimeout", 600000L);
    }

    public static long getMaxLifetime() {
        return (Long) getHikari().getOrDefault("maxLifetime", 1800000L);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: 无错误

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/config/DbConfig.java
git commit -m "feat: DbConfig 添加 HikariCP DataSource"
```

---

## Task 4: 修改 JdbcClientAddressRepository 使用 DataSource

**Files:**
- Modify: `src/main/java/com/address/repository/JdbcClientAddressRepository.java`

- [ ] **Step 1: 修改 getConnection 方法**

将:

```java
private Connection getConnection() throws SQLException {
    return DriverManager.getConnection(
        DbConfig.getUrl(),
        DbConfig.getUsername(),
        DbConfig.getPassword()
    );
}
```

改为:

```java
private Connection getConnection() throws SQLException {
    return DbConfig.getDataSource().getConnection();
}
```

- [ ] **Step 2: 修改 createTableIfNotExists 中的连接获取**

将:

```java
try (Connection conn = DriverManager.getConnection(
        DbConfig.getUrl(), DbConfig.getUsername(), DbConfig.getPassword())) {
```

改为:

```java
try (Connection conn = DbConfig.getDataSource().getConnection()) {
```

- [ ] **Step 3: 验证编译**

Run: `mvn compile -q`
Expected: 无错误

- [ ] **Step 4: 运行测试**

Run: `mvn test`
Expected: 21 tests, 0 failures

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/address/repository/JdbcClientAddressRepository.java
git commit -m "refactor: JdbcClientAddressRepository 使用 HikariCP DataSource"
```

---

## 自检清单

- [ ] pom.xml 添加了 HikariCP 依赖
- [ ] config.yaml 包含连接池配置
- [ ] DbConfig 正确初始化 DataSource 单例
- [ ] JdbcClientAddressRepository 使用 DataSource 获取连接
- [ ] 所有测试通过
