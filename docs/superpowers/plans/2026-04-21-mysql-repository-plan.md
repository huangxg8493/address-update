# MySQL 持久层实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 新增 JdbcClientAddressRepository 实现 MySQL 持久层，替换内存存储

**Architecture:** 新增 JdbcClientAddressRepository 实现 ClientAddressRepository 接口，Service 层通过接口依赖无需改动。启动时自动检查/创建表。

**Tech Stack:** JDBC 原生 + MySQL Connector + SnakeYAML

---

## 文件结构

| 操作 | 文件路径 |
|------|----------|
| 新增 | `src/main/resources/config.yaml` |
| 新增 | `src/main/java/com/address/config/DbConfig.java` |
| 新增 | `src/main/java/com/address/repository/JdbcClientAddressRepository.java` |
| 新增 | `src/test/java/com/address/repository/JdbcClientAddressRepositoryTest.java` |
| 修改 | `pom.xml` |
| 修改 | `ClientAddressService.java` |

---

## Task 1: 添加 Maven 依赖

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: 添加 MySQL 和 YAML 依赖**

在 `<dependencies>` 中添加：

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

- [ ] **Step 2: 验证依赖下载**

Run: `mvn dependency:resolve -q`
Expected: 无错误

- [ ] **Step 3: 提交**

```bash
git add pom.xml
git commit -m "deps: 添加 MySQL 和 SnakeYAML 依赖"
```

---

## Task 2: 创建配置文件和加载工具

**Files:**
- Create: `src/main/resources/config.yaml`
- Create: `src/main/java/com/address/config/DbConfig.java`

- [ ] **Step 1: 创建 config.yaml**

```yaml
database:
  driver: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://127.0.0.1:3306/testdb?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC
  username: admin
  password: admin
```

- [ ] **Step 2: 创建 DbConfig.java**

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

- [ ] **Step 3: 验证配置加载**

Run: `mvn compile -q`
Expected: 无错误

- [ ] **Step 4: 提交**

```bash
git add src/main/resources/config.yaml src/main/java/com/address/config/DbConfig.java
git commit -m "feat: 添加数据库配置和加载工具"
```

---

## Task 3: 创建 JdbcClientAddressRepository

**Files:**
- Create: `src/main/java/com/address/repository/JdbcClientAddressRepository.java`

- [ ] **Step 1: 实现 JdbcClientAddressRepository**

```java
package com.address.repository;

import com.address.config.DbConfig;
import com.address.constants.Constants;
import com.address.model.CifAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcClientAddressRepository implements ClientAddressRepository {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            DbConfig.getUrl(),
            DbConfig.getUsername(),
            DbConfig.getPassword()
        );
    }

    @Override
    public List<CifAddress> findByClientNo(String clientNo) {
        List<CifAddress> result = new ArrayList<>();
        String sql = "SELECT * FROM cif_address WHERE client_no = ? AND del_flag = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, clientNo);
            ps.setString(2, Constants.NO);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询地址失败", e);
        }
        return result;
    }

    @Override
    public void save(CifAddress address) {
        String sql = "INSERT INTO cif_address (seq_no, client_no, address_type, address_detail, " +
                     "last_change_date, is_mailing_address, is_newest, del_flag) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            setParams(ps, address);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("保存地址失败", e);
        }
    }

    @Override
    public void update(CifAddress address) {
        String sql = "UPDATE cif_address SET address_type = ?, address_detail = ?, " +
                     "last_change_date = ?, is_mailing_address = ?, is_newest = ?, del_flag = ? " +
                     "WHERE seq_no = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, address.getAddressType());
            ps.setString(2, address.getAddressDetail());
            ps.setTimestamp(3, address.getLastChangeDate() != null ?
                    new Timestamp(address.getLastChangeDate().getTime()) : null);
            ps.setString(4, address.getIsMailingAddress());
            ps.setString(5, address.getIsNewest());
            ps.setString(6, address.getDelFlag());
            ps.setString(7, address.getSeqNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新地址失败", e);
        }
    }

    @Override
    public void saveAll(List<CifAddress> addresses) {
        String sql = "INSERT INTO cif_address (seq_no, client_no, address_type, address_detail, " +
                     "last_change_date, is_mailing_address, is_newest, del_flag) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CifAddress address : addresses) {
                setParams(ps, address);
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("批量保存地址失败", e);
        }
    }

    @Override
    public void updateAll(List<CifAddress> addresses) {
        for (CifAddress address : addresses) {
            update(address);
        }
    }

    @Override
    public void delete(String seqNo) {
        String sql = "UPDATE cif_address SET del_flag = ? WHERE seq_no = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Constants.YES);
            ps.setString(2, seqNo);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除地址失败", e);
        }
    }

    private void setParams(PreparedStatement ps, CifAddress address) throws SQLException {
        ps.setString(1, address.getSeqNo());
        ps.setString(2, address.getClientNo());
        ps.setString(3, address.getAddressType());
        ps.setString(4, address.getAddressDetail());
        ps.setTimestamp(5, address.getLastChangeDate() != null ?
                new Timestamp(address.getLastChangeDate().getTime()) : null);
        ps.setString(6, address.getIsMailingAddress());
        ps.setString(7, address.getIsNewest());
        ps.setString(8, address.getDelFlag());
    }

    private CifAddress mapRow(ResultSet rs) throws SQLException {
        CifAddress address = new CifAddress();
        address.setSeqNo(rs.getString("seq_no"));
        address.setClientNo(rs.getString("client_no"));
        address.setAddressType(rs.getString("address_type"));
        address.setAddressDetail(rs.getString("address_detail"));
        Timestamp ts = rs.getTimestamp("last_change_date");
        if (ts != null) {
            address.setLastChangeDate(new Date(ts.getTime()));
        }
        address.setIsMailingAddress(rs.getString("is_mailing_address"));
        address.setIsNewest(rs.getString("is_newest"));
        address.setDelFlag(rs.getString("del_flag"));
        return address;
    }
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: 无错误

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/JdbcClientAddressRepository.java
git commit -m "feat: 添加 JdbcClientAddressRepository MySQL 实现"
```

---

## Task 4: 添加自动建表逻辑

**Files:**
- Modify: `JdbcClientAddressRepository.java`

- [ ] **Step 1: 在 JdbcClientAddressRepository 中添加建表逻辑**

在类中添加静态代码块：

```java
static {
    createTableIfNotExists();
}

private static void createTableIfNotExists() {
    String checkSql = "SELECT COUNT(*) FROM information_schema.tables " +
                      "WHERE table_schema = DATABASE() AND table_name = 'cif_address'";
    String createSql = "CREATE TABLE IF NOT EXISTS cif_address (" +
            "seq_no VARCHAR(64) PRIMARY KEY," +
            "client_no VARCHAR(32) NOT NULL," +
            "address_type VARCHAR(2) NOT NULL," +
            "address_detail VARCHAR(256) NOT NULL," +
            "last_change_date DATETIME," +
            "is_mailing_address CHAR(1) DEFAULT 'N'," +
            "is_newest CHAR(1) DEFAULT 'N'," +
            "del_flag CHAR(1) DEFAULT 'N'," +
            "INDEX idx_client_no (client_no)," +
            "INDEX idx_client_type (client_no, address_type)" +
            ")";

    try (Connection conn = DriverManager.getConnection(
            DbConfig.getUrl(), DbConfig.getUsername(), DbConfig.getPassword())) {

        try (PreparedStatement ps = conn.prepareStatement(checkSql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            if (rs.getInt(1) == 0) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(createSql);
                }
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("初始化数据库表失败", e);
    }
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: 无错误

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/JdbcClientAddressRepository.java
git commit -m "feat: 添加自动建表逻辑"
```

---

## Task 5: 修改 ClientAddressService 使用 JdbcClientAddressRepository

**Files:**
- Modify: `src/main/java/com/address/service/ClientAddressService.java`

- [ ] **Step 1: 修改构造函数注入**

将:
```java
public ClientAddressService(ClientAddressRepository repository,
                            MailingAddressStrategy mailingStrategy,
                            NewestAddressStrategy newestStrategy) {
    this.repository = repository;
    this.mailingStrategy = mailingStrategy;
    this.newestStrategy = newestStrategy;
    this.merger = new AddressMerger();
}
```

改为:
```java
public ClientAddressService(ClientAddressRepository repository,
                            MailingAddressStrategy mailingStrategy,
                            NewestAddressStrategy newestStrategy) {
    this.repository = repository;
    this.mailingStrategy = mailingStrategy;
    this.newestStrategy = newestStrategy;
    this.merger = new AddressMerger();
}

// 便捷构造方法，使用 JdbcClientAddressRepository
public ClientAddressService(MailingAddressStrategy mailingStrategy,
                            NewestAddressStrategy newestStrategy) {
    this(new JdbcClientAddressRepository(), mailingStrategy, newestStrategy);
}
```

- [ ] **Step 2: 验证编译**

Run: `mvn compile -q`
Expected: 无错误

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/service/ClientAddressService.java
git commit -m "refactor: ClientAddressService 支持便捷构造方法使用 JdbcClientAddressRepository"
```

---

## Task 6: 创建 JdbcClientAddressRepositoryTest

**Files:**
- Create: `src/test/java/com/address/repository/JdbcClientAddressRepositoryTest.java`

- [ ] **Step 1: 编写测试用例**

```java
package com.address.repository;

import com.address.model.AddressType;
import com.address.model.CifAddress;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Date;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class JdbcClientAddressRepositoryTest {

    private JdbcClientAddressRepository repository;

    @BeforeEach
    public void setUp() {
        repository = new JdbcClientAddressRepository();
    }

    @Test
    public void testSaveAndFind() {
        CifAddress address = createAddress("C001", "01", "北京市朝阳区");
        repository.save(address);

        List<CifAddress> result = repository.findByClientNo("C001");
        assertEquals(1, result.size());
        assertEquals("北京市朝阳区", result.get(0).getAddressDetail());
    }

    @Test
    public void testUpdate() {
        CifAddress address = createAddress("C001", "01", "北京市朝阳区");
        repository.save(address);

        address.setAddressDetail("上海市浦东新区");
        repository.update(address);

        List<CifAddress> result = repository.findByClientNo("C001");
        assertEquals("上海市浦东新区", result.get(0).getAddressDetail());
    }

    @Test
    public void testDelete() {
        CifAddress address = createAddress("C001", "01", "北京市朝阳区");
        repository.save(address);

        repository.delete(address.getSeqNo());

        List<CifAddress> result = repository.findByClientNo("C001");
        assertEquals(0, result.size());
    }

    private CifAddress createAddress(String clientNo, String type, String detail) {
        CifAddress address = new CifAddress();
        address.setSeqNo("SN" + System.currentTimeMillis());
        address.setClientNo(clientNo);
        address.setAddressType(type);
        address.setAddressDetail(detail);
        address.setLastChangeDate(new Date());
        address.setIsMailingAddress("N");
        address.setIsNewest("N");
        address.setDelFlag("N");
        return address;
    }
}
```

- [ ] **Step 2: 运行测试**

Run: `mvn test -Dtest=JdbcClientAddressRepositoryTest`
Expected: PASS（需要 MySQL 数据库运行）

- [ ] **Step 3: 提交**

```bash
git add src/test/java/com/address/repository/JdbcClientAddressRepositoryTest.java
git commit -m "test: 添加 JdbcClientAddressRepository 测试"
```

---

## 自检清单

- [ ] pom.xml 添加了 MySQL 和 SnakeYAML 依赖
- [ ] config.yaml 包含完整的数据库配置
- [ ] DbConfig 能正确加载 YAML 配置
- [ ] JdbcClientAddressRepository 实现了 ClientAddressRepository 所有方法
- [ ] 自动建表逻辑在静态块中执行
- [ ] ClientAddressService 提供了便捷构造方法
- [ ] 所有测试编译通过
