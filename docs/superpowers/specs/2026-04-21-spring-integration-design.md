# Spring 引入设计规格

## 1. 背景与目标

当前项目使用 Java8 + Maven，手工管理 DataSource（HikariCP）和 MyBatis SqlSessionFactory。

**目标：**
1. 引入 Spring Boot 作为容器，利用依赖注入和生命周期管理
2. 新增 JdbcTemplateClientAddressRepository（基于 JdbcTemplate）
3. 新增 JdbcClientClientAddressRepository（基于 JdbcClient）
4. 保留现有所有实现（MyBatis、Jdbc、Memory）
5. 通过 @Primary 标记默认实现

## 2. 技术选型

| 组件 | 选择 | 原因 |
|------|------|------|
| Spring Boot 版本 | 2.7.18 | 要求 Java 8，与当前项目兼容 |
| MyBatis 集成 | mybatis-spring-boot-starter 3.0.3 | 官方 Starter，自动配置 |
| JdbcTemplate | spring-boot-starter-jdbc 内置 | 稳定、文档丰富 |
| JdbcClient | Spring JDBC 5.x 内置 | 链式 API，比 JdbcTemplate 更现代 |
| DataSource | HikariCP（Spring Boot 内置） | 性能最优，Spring Boot 默认 |

## 3. 架构设计

### 3.1 依赖结构

```
Spring Boot 2.7.18 (parent)
├── spring-boot-starter
├── spring-boot-starter-jdbc
│   └── spring-jdbc
│   └── HikariCP
├── mybatis-spring-boot-starter 3.0.3
│   └── mybatis-spring
│   └── mybatis
├── mysql-connector-java 8.0.33
├── HikariCP 4.0.3 (保留，Spring Boot 自动管理)
└── slf4j + logback
```

### 3.2 Repository 实现策略

四种实现并存：

| 实现类 | 注解 | 用途 |
|--------|------|------|
| JdbcTemplateClientAddressRepository | @Repository @Primary | **默认实现**，使用 JdbcTemplate |
| JdbcClientClientAddressRepository | @Repository | 使用 JdbcClient |
| MyBatisClientAddressRepository | @Repository | 使用 MyBatis（由 Starter 管理） |
| MemoryClientAddressRepository | @Repository @Primary("memory") | 内存实现，测试用 |
| JdbcClientAddressRepository | @Repository | 原生 JDBC（静态初始化） |

### 3.3 配置管理

**DbConfig.java 和 MyBatisConfig.java 保留不变，不修改，不使用。**

Spring Boot 通过 application.yml 配置 DataSource：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/address_db
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 2
```

**关键约束：**
- 不修改 DbConfig.java
- 不修改 MyBatisConfig.java
- 不使用 DbConfig.getDataSource()

### 3.4 MyBatis 集成

- 移除原手动 mybatis 依赖（保留 mybatis-spring-boot-starter）
- 使用 @MapperScan("com.address.repository") 扫描 Mapper
- SqlSessionFactory 由 Starter 自动配置
- Mapper XML 位置：`classpath:mapper/*.xml`

## 4. 文件变更清单

| 文件 | 操作 | 说明 |
|------|------|------|
| pom.xml | 修改 | 添加 Spring Boot parent 和依赖 |
| Application.java | 新增 | Spring Boot 启动类，@MapperScan |
| application.yml | 新增 | Spring DataSource/MyBatis 配置 |
| JdbcTemplateClientAddressRepository.java | 新增 | JdbcTemplate 实现，@Primary |
| JdbcClientClientAddressRepository.java | 新增 | JdbcClient 实现 |
| DbConfig.java | **不修改** | 保留静态方式 |
| MyBatisConfig.java | **不修改** | 保留但不使用 |
| ClientAddressService.java | 修改 | @Autowired 注入 Repository |

## 5. 实现顺序

1. **pom.xml** — 引入 Spring Boot 2.7.18 和依赖
2. **Application.java** — 创建启动类，@MapperScan
3. **application.yml** — DataSource 和 MyBatis 配置
4. **JdbcTemplateClientAddressRepository** — JdbcTemplate 实现，@Primary
5. **JdbcClientClientAddressRepository** — JdbcClient 实现
6. **ClientAddressService** — 改为 @Autowired 注入
7. **测试验证** — mvn test

## 6. Profile 扩展（可选）

后续可通过 @Profile 注解切换实现：
- @Profile("jdbctemplate")
- @Profile("jdbcclient")
- @Profile("mybatis")
- @Profile("memory")

## 7. 验证标准

- [ ] mvn compile 编译通过
- [ ] mvn test 所有测试通过
- [ ] Application 启动类可正常启动
- [ ] 四种 Repository 实现可切换
