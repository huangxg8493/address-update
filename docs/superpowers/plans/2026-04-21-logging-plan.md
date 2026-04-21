# 日志记录功能实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为客户地址信息维护系统添加日志记录功能

**Architecture:** 使用 SLF4J API + Logback 实现，输出到控制台和文件

**Tech Stack:** SLF4J 2.0.9, Logback 1.4.11

---

## 文件结构

| 操作 | 文件路径 |
|------|----------|
| 修改 | `pom.xml` |
| 创建 | `src/main/resources/logback.xml` |
| 修改 | `src/main/java/com/address/repository/MyBatisClientAddressRepository.java` |
| 修改 | `src/main/java/com/address/repository/JdbcClientAddressRepository.java` |

---

## Task 1: 添加 Maven 依赖

**Files:**
- Modify: `pom.xml`

- [ ] **Step 1: 添加 slf4j-api 和 logback-classic 依赖**

```xml
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
```

- [ ] **Step 2: 验证依赖下载**

Run: `mvn dependency:resolve -f D:/AI/address-update/pom.xml`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add pom.xml && git commit -m "deps: 添加 slf4j 和 logback 依赖"
```

---

## Task 2: 创建 logback.xml

**Files:**
- Create: `src/main/resources/logback.xml`

- [ ] **Step 1: 创建 logback.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 日志目录 -->
    <property name="LOG_PATH" value="logs"/>
    <property name="APP_NAME" value="client-address-service"/>

    <!-- 控制台 appender -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 文件 appender，按日期滚动 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_PATH}/app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_PATH}/app.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 根 logger -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>

    <!-- Repository 日志 -->
    <logger name="com.address.repository" level="INFO" additivity="false">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </logger>
</configuration>
```

- [ ] **Step 2: 提交**

```bash
git add src/main/resources/logback.xml && git commit -m "feat: 添加 logback 日志配置"
```

---

## Task 3: 修改 MyBatisClientAddressRepository 添加日志

**Files:**
- Modify: `src/main/java/com/address/repository/MyBatisClientAddressRepository.java`

- [ ] **Step 1: 添加 Logger 和日志语句**

在类开头添加：
```java
private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(MyBatisClientAddressRepository.class);
```

在 save 方法中添加：
```java
logger.info("保存地址 clientNo={}", address.getClientNo());
```

在 saveAll 方法中添加：
```java
logger.info("批量保存地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
```

在 update 方法中添加：
```java
logger.info("更新地址 seqNo={}", address.getSeqNo());
```

在 updateAll 方法中添加：
```java
logger.info("批量更新地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
```

在 delete 方法中添加：
```java
logger.info("删除地址 seqNo={}", seqNo);
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -f D:/AI/address-update/pom.xml`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/MyBatisClientAddressRepository.java && git commit -m "feat: MyBatisClientAddressRepository 添加日志"
```

---

## Task 4: 修改 JdbcClientAddressRepository 添加日志

**Files:**
- Modify: `src/main/java/com/address/repository/JdbcClientAddressRepository.java`

- [ ] **Step 1: 添加 Logger 和日志语句**

在类开头添加：
```java
private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(JdbcClientAddressRepository.class);
```

在 save 方法中添加：
```java
logger.info("保存地址 clientNo={}", address.getClientNo());
```

在 saveAll 方法中添加：
```java
logger.info("批量保存地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
```

在 update 方法中添加：
```java
logger.info("更新地址 seqNo={}", address.getSeqNo());
```

在 updateAll 方法中添加：
```java
logger.info("批量更新地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
```

在 delete 方法中添加：
```java
logger.info("删除地址 seqNo={}", seqNo);
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile -f D:/AI/address-update/pom.xml`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/JdbcClientAddressRepository.java && git commit -m "feat: JdbcClientAddressRepository 添加日志"
```

---

## Task 5: 验证日志输出

**Files:**
- 无修改

- [ ] **Step 1: 运行测试**

Run: `mvn test -f D:/AI/address-update/pom.xml`
Expected: BUILD SUCCESS

- [ ] **Step 2: 检查日志文件**

Run: `ls -la logs/`
Expected: app.log 文件存在

- [ ] **Step 3: 提交**

```bash
git add . && git commit -m "feat: 完成日志记录功能"
```

---

## 验证

```bash
# 编译
mvn compile -f D:/AI/address-update/pom.xml

# 测试
mvn test -f D:/AI/address-update/pom.xml

# 检查日志文件
ls -la logs/app.log
```

---

## 自我检查清单

- [ ] Task 1: pom.xml 添加了 slf4j-api 和 logback-classic 依赖
- [ ] Task 2: logback.xml 配置了控制台和文件 appender
- [ ] Task 3: MyBatisClientAddressRepository 添加了日志
- [ ] Task 4: JdbcClientAddressRepository 添加了日志
- [ ] Task 5: 验证日志输出正常
