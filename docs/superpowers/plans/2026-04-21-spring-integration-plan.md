# Spring Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 引入 Spring Boot 容器，新增 JdbcTemplate 和 JdbcClient 两种 Repository 实现，保留现有所有实现，通过 @Primary 标记默认实现

**Architecture:** Spring Boot 2.7.x 管理 DataSource 和 MyBatis；JdbcTemplate/JdbcClient/MyBatis/Memory 四种 Repository 实现并存；通过 @Primary 切换默认实现

**Tech Stack:** Spring Boot 2.7.18, spring-boot-starter-jdbc, mybatis-spring-boot-starter 3.0.3, HikariCP, Java 8

---

## File Structure

| 文件 | 操作 |
|------|------|
| `pom.xml` | 修改：替换 parent，添加 Spring Boot 依赖 |
| `src/main/java/com/address/Application.java` | 新增：Spring Boot 启动类 |
| `src/main/resources/application.yml` | 新增：Spring DataSource 配置 |
| `src/main/java/com/address/repository/JdbcTemplateClientAddressRepository.java` | 新增：JdbcTemplate 实现 |
| `src/main/java/com/address/repository/JdbcClientClientAddressRepository.java` | 新增：JdbcClient 实现 |
| `src/main/java/com/address/config/MyBatisConfig.java` | **保留不变** |
| `src/main/java/com/address/config/DbConfig.java` | **不修改** |
| `src/main/java/com/address/service/ClientAddressService.java` | 修改：@Autowired 注入 Repository |

---

## Task 1: pom.xml 添加 Spring Boot 依赖

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: 备份并修改 pom.xml parent**

```xml
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.7.18</version>
        <relativePath/>
    </parent>

    <groupId>com.address</groupId>
    <artifactId>client-address-service</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- Spring Boot -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>

        <!-- MyBatis Spring Boot Starter -->
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>3.0.3</version>
        </dependency>

        <!-- 保留原有依赖 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.33</version>
        </dependency>
        <dependency>
            <groupId>com.zaxxer</groupId>
            <artifactId>HikariCP</artifactId>
            <version>4.0.3</version>
        </dependency>
        <dependency>
            <groupId>org.yaml</groupId>
            <artifactId>snakeyaml</artifactId>
            <version>2.0</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.9</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.4.11</version>
        </dependency>

        <!-- Test -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.9.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.1.2</version>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 验证编译**

```bash
mvn compile
```

Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add pom.xml
git commit -m "feat: 引入 Spring Boot 2.7.18 依赖"
```

---

## Task 2: 创建 Spring Boot 启动类

**Files:**
- Create: `src/main/java/com/address/Application.java`

- [ ] **Step 1: 创建 Application.java**

```java
package com.address;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.address.repository")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

- [ ] **Step 2: 验证编译**

```bash
mvn compile
```

Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/Application.java
git commit -m "feat: 创建 Spring Boot 启动类 Application"
```

---

## Task 3: 创建 application.yml 配置

**Files:**
- Create: `src/main/resources/application.yml`

- [ ] **Step 1: 创建 application.yml**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/address_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000

mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.address.model
```

- [ ] **Step 2: 验证编译**

```bash
mvn compile
```

Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/resources/application.yml
git commit -m "feat: 添加 Spring Boot 配置文件 application.yml"
```

---

## Task 4: 创建 JdbcTemplateClientAddressRepository

**Files:**
- Create: `src/main/java/com/address/repository/JdbcTemplateClientAddressRepository.java`

- [ ] **Step 1: 创建 JdbcTemplateClientAddressRepository.java**

```java
package com.address.repository;

import com.address.constants.Constants;
import com.address.model.CifAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcTemplateClientAddressRepository implements ClientAddressRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTemplateClientAddressRepository.class);

    private final JdbcTemplate jdbcTemplate;

    public JdbcTemplateClientAddressRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<CifAddress> findByClientNo(String clientNo) {
        String sql = "SELECT SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, LAST_CHANGE_DATE, " +
                     "IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG FROM CIF_ADDRESS WHERE CLIENT_NO = ? AND DEL_FLAG = ?";
        return jdbcTemplate.query(sql, new CifAddressRowMapper(), clientNo, Constants.NO);
    }

    @Override
    public void save(CifAddress address) {
        logger.info("保存地址 clientNo={}", address.getClientNo());
        String sql = "INSERT INTO CIF_ADDRESS (SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, " +
                     "LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, address.getSeqNo(), address.getClientNo(), address.getAddressType(),
                address.getAddressDetail(), address.getLastChangeDate(), address.getIsMailingAddress(),
                address.getIsNewest(), address.getDelFlag());
    }

    @Override
    public void update(CifAddress address) {
        logger.info("更新地址 seqNo={}", address.getSeqNo());
        String sql = "UPDATE CIF_ADDRESS SET ADDRESS_TYPE = ?, ADDRESS_DETAIL = ?, " +
                     "LAST_CHANGE_DATE = ?, IS_MAILING_ADDRESS = ?, IS_NEWEST = ?, DEL_FLAG = ? " +
                     "WHERE SEQ_NO = ?";
        jdbcTemplate.update(sql, address.getAddressType(), address.getAddressDetail(),
                address.getLastChangeDate(), address.getIsMailingAddress(),
                address.getIsNewest(), address.getDelFlag(), address.getSeqNo());
    }

    @Override
    public void saveAll(List<CifAddress> addresses) {
        logger.info("批量保存地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        String sql = "INSERT INTO CIF_ADDRESS (SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, " +
                     "LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, addresses.stream()
                .map(a -> new Object[]{a.getSeqNo(), a.getClientNo(), a.getAddressType(),
                        a.getAddressDetail(), a.getLastChangeDate(), a.getIsMailingAddress(),
                        a.getIsNewest(), a.getDelFlag()})
                .toArray(Object[][]::new));
    }

    @Override
    public void updateAll(List<CifAddress> addresses) {
        logger.info("批量更新地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        for (CifAddress address : addresses) {
            update(address);
        }
    }

    @Override
    public void delete(String seqNo) {
        logger.info("删除地址 seqNo={}", seqNo);
        String sql = "UPDATE CIF_ADDRESS SET DEL_FLAG = ? WHERE SEQ_NO = ?";
        jdbcTemplate.update(sql, Constants.YES, seqNo);
    }

    private static class CifAddressRowMapper implements RowMapper<CifAddress> {
        @Override
        public CifAddress mapRow(ResultSet rs, int rowNum) throws SQLException {
            CifAddress address = new CifAddress();
            address.setSeqNo(rs.getString("SEQ_NO"));
            address.setClientNo(rs.getString("CLIENT_NO"));
            address.setAddressType(rs.getString("ADDRESS_TYPE"));
            address.setAddressDetail(rs.getString("ADDRESS_DETAIL"));
            address.setLastChangeDate(rs.getDate("LAST_CHANGE_DATE"));
            address.setIsMailingAddress(rs.getString("IS_MAILING_ADDRESS"));
            address.setIsNewest(rs.getString("IS_NEWEST"));
            address.setDelFlag(rs.getString("DEL_FLAG"));
            return address;
        }
    }
}
```

- [ ] **Step 2: 验证编译**

```bash
mvn compile
```

Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/JdbcTemplateClientAddressRepository.java
git commit -m "feat: 新增 JdbcTemplateClientAddressRepository"
```

---

## Task 5: 创建 JdbcClientClientAddressRepository

**Files:**
- Create: `src/main/java/com/address/repository/JdbcClientClientAddressRepository.java`

- [ ] **Step 1: 创建 JdbcClientClientAddressRepository.java**

```java
package com.address.repository;

import com.address.constants.Constants;
import com.address.model.CifAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcClient;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class JdbcClientClientAddressRepository implements ClientAddressRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcClientClientAddressRepository.class);

    private final JdbcClient jdbcClient;

    public JdbcClientClientAddressRepository(DataSource dataSource) {
        this.jdbcClient = JdbcClient.create(dataSource);
    }

    @Override
    public List<CifAddress> findByClientNo(String clientNo) {
        return jdbcClient.sql("SELECT SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, LAST_CHANGE_DATE, " +
                              "IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG FROM CIF_ADDRESS WHERE CLIENT_NO = ? AND DEL_FLAG = ?")
                .param(clientNo)
                .param(Constants.NO)
                .query((rs, rowNum) -> {
                    CifAddress address = new CifAddress();
                    address.setSeqNo(rs.getString("SEQ_NO"));
                    address.setClientNo(rs.getString("CLIENT_NO"));
                    address.setAddressType(rs.getString("ADDRESS_TYPE"));
                    address.setAddressDetail(rs.getString("ADDRESS_DETAIL"));
                    address.setLastChangeDate(rs.getDate("LAST_CHANGE_DATE"));
                    address.setIsMailingAddress(rs.getString("IS_MAILING_ADDRESS"));
                    address.setIsNewest(rs.getString("IS_NEWEST"));
                    address.setDelFlag(rs.getString("DEL_FLAG"));
                    return address;
                });
    }

    @Override
    public void save(CifAddress address) {
        logger.info("保存地址 clientNo={}", address.getClientNo());
        jdbcClient.sql("INSERT INTO CIF_ADDRESS (SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL, " +
                       "LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
                .param(address.getSeqNo())
                .param(address.getClientNo())
                .param(address.getAddressType())
                .param(address.getAddressDetail())
                .param(address.getLastChangeDate())
                .param(address.getIsMailingAddress())
                .param(address.getIsNewest())
                .param(address.getDelFlag())
                .update();
    }

    @Override
    public void update(CifAddress address) {
        logger.info("更新地址 seqNo={}", address.getSeqNo());
        jdbcClient.sql("UPDATE CIF_ADDRESS SET ADDRESS_TYPE = ?, ADDRESS_DETAIL = ?, " +
                       "LAST_CHANGE_DATE = ?, IS_MAILING_ADDRESS = ?, IS_NEWEST = ?, DEL_FLAG = ? " +
                       "WHERE SEQ_NO = ?")
                .param(address.getAddressType())
                .param(address.getAddressDetail())
                .param(address.getLastChangeDate())
                .param(address.getIsMailingAddress())
                .param(address.getIsNewest())
                .param(address.getDelFlag())
                .param(address.getSeqNo())
                .update();
    }

    @Override
    public void saveAll(List<CifAddress> addresses) {
        logger.info("批量保存地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        for (CifAddress address : addresses) {
            save(address);
        }
    }

    @Override
    public void updateAll(List<CifAddress> addresses) {
        logger.info("批量更新地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        for (CifAddress address : addresses) {
            update(address);
        }
    }

    @Override
    public void delete(String seqNo) {
        logger.info("删除地址 seqNo={}", seqNo);
        jdbcClient.sql("UPDATE CIF_ADDRESS SET DEL_FLAG = ? WHERE SEQ_NO = ?")
                .param(Constants.YES)
                .param(seqNo)
                .update();
    }
}
```

- [ ] **Step 2: 验证编译**

```bash
mvn compile
```

Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/JdbcClientClientAddressRepository.java
git commit -m "feat: 新增 JdbcClientClientAddressRepository"
```

---

## Task 6: 配置 @Primary 并更新 ClientAddressService

**Files:**
- Modify: `src/main/java/com/address/repository/JdbcTemplateClientAddressRepository.java`
- Modify: `src/main/java/com/address/service/ClientAddressService.java`

- [ ] **Step 1: 在 JdbcTemplateClientAddressRepository 添加 @Primary**

在类声明上 `@Repository` 下方添加 `@Primary`：

```java
@Repository
@Primary
public class JdbcTemplateClientAddressRepository implements ClientAddressRepository {
```

- [ ] **Step 2: 更新 ClientAddressService 使用 @Autowired**

```java
@Service
public class ClientAddressService {

    private static final Logger logger = LoggerFactory.getLogger(ClientAddressService.class);

    private final ClientAddressRepository repository;

    @Autowired
    public ClientAddressService(ClientAddressRepository repository) {
        this.repository = repository;
    }
```

- [ ] **Step 3: 验证编译**

```bash
mvn compile
```

Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/address/repository/JdbcTemplateClientAddressRepository.java src/main/java/com/address/service/ClientAddressService.java
git commit -m "feat: 配置 @Primary 并更新 ClientAddressService 注入"
```

---

## Task 7: 验证测试

- [ ] **Step 1: 运行所有测试**

```bash
mvn test
```

Expected: BUILD SUCCESS, Tests run: XX, Failures: 0, Errors: 0

- [ ] **Step 2: 提交**

```bash
git add -A
git commit -m "test: 验证 Spring Boot 集成测试通过"
```

---

## Verification

```bash
# 1. 编译
mvn compile
# Expected: BUILD SUCCESS

# 2. 测试
mvn test
# Expected: BUILD SUCCESS, 所有测试通过
```
