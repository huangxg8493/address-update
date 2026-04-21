# MyBatis 迁移实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 新建 MyBatis 实现（JdbcClientAddressRepository 保留），完成功能验证

**Architecture:** 在现有 JDBC 实现基础上，新增 MyBatis 持久层实现。使用注解处理简单 SQL（delete），XML 处理复杂 SQL（findByClientNo, save, update, saveAll, updateAll）。复用现有 HikariCP DataSource。

**Tech Stack:** Java8, Maven, MyBatis 3.5.13, mybatis-spring 2.1.1, HikariCP, MySQL

---

## 文件结构

| 操作 | 文件路径 |
|------|----------|
| 创建 | `pom.xml` (修改) |
| 创建 | `src/main/resources/mybatis-config.xml` |
| 创建 | `src/main/resources/mapper/CifAddressMapper.xml` |
| 创建 | `src/main/java/com/address/repository/CifAddressMapper.java` |
| 创建 | `src/main/java/com/address/config/MyBatisConfig.java` |
| 创建 | `src/main/java/com/address/repository/MyBatisClientAddressRepository.java` |
| 创建 | `src/test/java/com/address/repository/MyBatisClientAddressRepositoryTest.java` |
| 修改 | 无 |

---

## Task 1: 添加 MyBatis Maven 依赖

**Files:**
- Modify: `pom.xml:15-43`

- [ ] **Step 1: 在 pom.xml 添加 MyBatis 依赖**

在 `<dependencies>` 节点内，在 HikariCP 依赖后面添加：

```xml
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.13</version>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>2.1.1</version>
</dependency>
```

- [ ] **Step 2: 验证依赖添加成功**

Run: `mvn dependency:resolve -DincludeArtifactIds=mybatis,mybatis-spring`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add pom.xml
git commit -m "feat: 添加 MyBatis 依赖"
```

---

## Task 2: 创建 mybatis-config.xml

**Files:**
- Create: `src/main/resources/mybatis-config.xml`

- [ ] **Step 1: 创建 mybatis-config.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <settings>
        <!-- 下划线转驼峰 -->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <!-- 日志实现 -->
        <setting name="logImpl" value="SLF4J"/>
    </settings>

    <typeAliases>
        <typeAlias type="com.address.model.CifAddress" alias="CifAddress"/>
    </typeAliases>
</configuration>
```

- [ ] **Step 2: 验证配置文件格式**

Run: `xmllint --noout src/main/resources/mybatis-config.xml`
Expected: 无输出（格式正确）

- [ ] **Step 3: 提交**

```bash
git add src/main/resources/mybatis-config.xml
git commit -m "feat: 添加 MyBatis 配置文件"
```

---

## Task 3: 创建 CifAddressMapper.xml

**Files:**
- Create: `src/main/resources/mapper/CifAddressMapper.xml`

- [ ] **Step 1: 创建 mapper 目录**

Run: `mkdir -p src/main/resources/mapper`

- [ ] **Step 2: 创建 CifAddressMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.address.repository.CifAddressMapper">

    <resultMap id="CifAddressResultMap" type="CifAddress">
        <id property="seqNo" column="SEQ_NO"/>
        <result property="clientNo" column="CLIENT_NO"/>
        <result property="addressType" column="ADDRESS_TYPE"/>
        <result property="addressDetail" column="ADDRESS_DETAIL"/>
        <result property="lastChangeDate" column="LAST_CHANGE_DATE"/>
        <result property="isMailingAddress" column="IS_MAILING_ADDRESS"/>
        <result property="isNewest" column="IS_NEWEST"/>
        <result property="delFlag" column="DEL_FLAG"/>
    </resultMap>

    <select id="findByClientNo" resultMap="CifAddressResultMap">
        SELECT SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL,
               LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG
        FROM CIF_ADDRESS
        WHERE CLIENT_NO = #{clientNo} AND DEL_FLAG = #{delFlag}
    </select>

    <insert id="save" parameterType="CifAddress">
        INSERT INTO CIF_ADDRESS (
            SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL,
            LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG
        ) VALUES (
            #{seqNo}, #{clientNo}, #{addressType}, #{addressDetail},
            #{lastChangeDate}, #{isMailingAddress}, #{isNewest}, #{delFlag}
        )
    </insert>

    <update id="update" parameterType="CifAddress">
        UPDATE CIF_ADDRESS
        SET ADDRESS_TYPE = #{addressType},
            ADDRESS_DETAIL = #{addressDetail},
            LAST_CHANGE_DATE = #{lastChangeDate},
            IS_MAILING_ADDRESS = #{isMailingAddress},
            IS_NEWEST = #{isNewest},
            DEL_FLAG = #{delFlag}
        WHERE SEQ_NO = #{seqNo}
    </update>

    <insert id="saveAll" parameterType="java.util.List">
        INSERT INTO CIF_ADDRESS (
            SEQ_NO, CLIENT_NO, ADDRESS_TYPE, ADDRESS_DETAIL,
            LAST_CHANGE_DATE, IS_MAILING_ADDRESS, IS_NEWEST, DEL_FLAG
        ) VALUES
        <foreach collection="list" item="addr" separator=",">
            (
                #{addr.seqNo}, #{addr.clientNo}, #{addr.addressType}, #{addr.addressDetail},
                #{addr.lastChangeDate}, #{addr.isMailingAddress},
                #{addr.isNewest}, #{addr.delFlag}
            )
        </foreach>
    </insert>

    <update id="updateAll" parameterType="java.util.List">
        <foreach collection="list" item="addr">
            UPDATE CIF_ADDRESS
            SET ADDRESS_TYPE = #{addr.addressType},
                ADDRESS_DETAIL = #{addr.addressDetail},
                LAST_CHANGE_DATE = #{addr.lastChangeDate},
                IS_MAILING_ADDRESS = #{addr.isMailingAddress},
                IS_NEWEST = #{addr.isNewest},
                DEL_FLAG = #{addr.delFlag}
            WHERE SEQ_NO = #{addr.seqNo};
        </foreach>
    </update>

</mapper>
```

- [ ] **Step 3: 验证 XML 格式**

Run: `xmllint --noout src/main/resources/mapper/CifAddressMapper.xml`
Expected: 无输出（格式正确）

- [ ] **Step 4: 提交**

```bash
git add src/main/resources/mapper/CifAddressMapper.xml
git commit -m "feat: 添加 CifAddressMapper.xml"
```

---

## Task 4: 创建 CifAddressMapper.java

**Files:**
- Create: `src/main/java/com/address/repository/CifAddressMapper.java`

- [ ] **Step 1: 创建 CifAddressMapper.java**

```java
package com.address.repository;

import com.address.model.CifAddress;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface CifAddressMapper {

    @Update("UPDATE CIF_ADDRESS SET DEL_FLAG = 'Y' WHERE SEQ_NO = #{seqNo}")
    void delete(@Param("seqNo") String seqNo);

    List<CifAddress> findByClientNo(@Param("clientNo") String clientNo, @Param("delFlag") String delFlag);

    void save(CifAddress address);

    void update(CifAddress address);

    void saveAll(List<CifAddress> addresses);

    void updateAll(List<CifAddress> addresses);
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/CifAddressMapper.java
git commit -m "feat: 添加 CifAddressMapper 接口"
```

---

## Task 5: 创建 MyBatisConfig.java

**Files:**
- Create: `src/main/java/com/address/config/MyBatisConfig.java`

- [ ] **Step 1: 创建 MyBatisConfig.java**

```java
package com.address.config;

import com.zaxxer.hikari.DataSource;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.InputStream;

public class MyBatisConfig {

    private static final SqlSessionFactory SQL_SESSION_FACTORY;

    static {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);

            XMLConfigBuilder parser = new XMLConfigBuilder(inputStream);
            org.apache.ibatis.session.Configuration config = parser.parse();

            DataSource dataSource = (DataSource) DbConfig.getDataSource();
            config.setEnvironment(
                new org.apache.ibatis.mapping.Environment(
                    "development",
                    new JdbcTransactionFactory(),
                    dataSource
                )
            );

            SQL_SESSION_FACTORY = new SqlSessionFactoryBuilder().build(config);
        } catch (Exception e) {
            throw new RuntimeException("初始化 MyBatis SqlSessionFactory 失败", e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return SQL_SESSION_FACTORY;
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/config/MyBatisConfig.java
git commit -m "feat: 添加 MyBatisConfig 配置类"
```

---

## Task 6: 创建 MyBatisClientAddressRepository.java

**Files:**
- Create: `src/main/java/com/address/repository/MyBatisClientAddressRepository.java`

- [ ] **Step 1: 创建 MyBatisClientAddressRepository.java**

```java
package com.address.repository;

import com.address.config.MyBatisConfig;
import com.address.constants.Constants;
import com.address.model.CifAddress;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class MyBatisClientAddressRepository implements ClientAddressRepository {

    private static final SqlSessionFactory SQL_SESSION_FACTORY = MyBatisConfig.getSqlSessionFactory();

    private SqlSession getSqlSession() {
        return SQL_SESSION_FACTORY.openSession();
    }

    private <T> T execute(SqlSessionCallback<T> callback) {
        try (SqlSession session = getSqlSession()) {
            T result = callback.doInSession(session);
            session.commit();
            return result;
        }
    }

    @FunctionalInterface
    private interface SqlSessionCallback<T> {
        T doInSession(SqlSession session);
    }

    @Override
    public List<CifAddress> findByClientNo(String clientNo) {
        return execute(session -> {
            CifAddressMapper mapper = session.getMapper(CifAddressMapper.class);
            return mapper.findByClientNo(clientNo, Constants.NO);
        });
    }

    @Override
    public void save(CifAddress address) {
        execute(session -> {
            session.getMapper(CifAddressMapper.class).save(address);
            return null;
        });
    }

    @Override
    public void update(CifAddress address) {
        execute(session -> {
            session.getMapper(CifAddressMapper.class).update(address);
            return null;
        });
    }

    @Override
    public void saveAll(List<CifAddress> addresses) {
        execute(session -> {
            session.getMapper(CifAddressMapper.class).saveAll(addresses);
            return null;
        });
    }

    @Override
    public void updateAll(List<CifAddress> addresses) {
        execute(session -> {
            session.getMapper(CifAddressMapper.class).updateAll(addresses);
            return null;
        });
    }

    @Override
    public void delete(String seqNo) {
        execute(session -> {
            session.getMapper(CifAddressMapper.class).delete(seqNo);
            return null;
        });
    }
}
```

- [ ] **Step 2: 编译验证**

Run: `mvn compile`
Expected: BUILD SUCCESS

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/repository/MyBatisClientAddressRepository.java
git commit -m "feat: 添加 MyBatisClientAddressRepository 实现"
```

---

## Task 7: 创建 MyBatisClientAddressRepositoryTest

**Files:**
- Create: `src/test/java/com/address/repository/MyBatisClientAddressRepositoryTest.java`

- [ ] **Step 1: 查看 JdbcClientAddressRepositoryTest 作为参考**

Read: `src/test/java/com/address/repository/JdbcClientAddressRepositoryTest.java`

- [ ] **Step 2: 创建 MyBatisClientAddressRepositoryTest**

```java
package com.address.repository;

import com.address.model.CifAddress;
import com.address.utils.SnowflakeIdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MyBatisClientAddressRepositoryTest {

    private MyBatisClientAddressRepository repository;

    @BeforeEach
    void setUp() {
        repository = new MyBatisClientAddressRepository();
    }

    @AfterEach
    void tearDown() {
        List<CifAddress> addresses = repository.findByClientNo("TEST_CLIENT_001");
        for (CifAddress addr : addresses) {
            repository.delete(addr.getSeqNo());
        }
    }

    @Test
    void testSaveAndFindByClientNo() {
        CifAddress address = createAddress("TEST_CLIENT_001", "01", "北京市朝阳区XXX");
        repository.save(address);

        List<CifAddress> result = repository.findByClientNo("TEST_CLIENT_001");
        assertEquals(1, result.size());
        assertEquals("01", result.get(0).getAddressType());
        assertEquals("北京市朝阳区XXX", result.get(0).getAddressDetail());
    }

    @Test
    void testUpdate() {
        CifAddress address = createAddress("TEST_CLIENT_002", "01", "原地址");
        repository.save(address);

        List<CifAddress> addresses = repository.findByClientNo("TEST_CLIENT_002");
        CifAddress saved = addresses.get(0);
        saved.setAddressDetail("新地址");
        saved.setLastChangeDate(new Date());
        repository.update(saved);

        List<CifAddress> result = repository.findByClientNo("TEST_CLIENT_002");
        assertEquals("新地址", result.get(0).getAddressDetail());
    }

    @Test
    void testSaveAll() {
        List<CifAddress> addresses = new ArrayList<>();
        addresses.add(createAddress("TEST_CLIENT_003", "01", "地址1"));
        addresses.add(createAddress("TEST_CLIENT_003", "02", "地址2"));
        repository.saveAll(addresses);

        List<CifAddress> result = repository.findByClientNo("TEST_CLIENT_003");
        assertEquals(2, result.size());
    }

    @Test
    void testUpdateAll() {
        List<CifAddress> addresses = new ArrayList<>();
        addresses.add(createAddress("TEST_CLIENT_004", "01", "地址1"));
        addresses.add(createAddress("TEST_CLIENT_004", "02", "地址2"));
        repository.saveAll(addresses);

        List<CifAddress> saved = repository.findByClientNo("TEST_CLIENT_004");
        for (CifAddress addr : saved) {
            addr.setAddressDetail("已更新");
            addr.setLastChangeDate(new Date());
        }
        repository.updateAll(saved);

        List<CifAddress> result = repository.findByClientNo("TEST_CLIENT_004");
        for (CifAddress addr : result) {
            assertEquals("已更新", addr.getAddressDetail());
        }
    }

    @Test
    void testDelete() {
        CifAddress address = createAddress("TEST_CLIENT_005", "01", "待删除地址");
        repository.save(address);

        List<CifAddress> before = repository.findByClientNo("TEST_CLIENT_005");
        assertEquals(1, before.size());

        repository.delete(before.get(0).getSeqNo());

        List<CifAddress> after = repository.findByClientNo("TEST_CLIENT_005");
        assertEquals(0, after.size());
    }

    private CifAddress createAddress(String clientNo, String addressType, String addressDetail) {
        CifAddress address = new CifAddress();
        address.setSeqNo(SnowflakeIdGenerator.getInstance().nextIdAsString());
        address.setClientNo(clientNo);
        address.setAddressType(addressType);
        address.setAddressDetail(addressDetail);
        address.setLastChangeDate(new Date());
        address.setIsMailingAddress("N");
        address.setIsNewest("N");
        address.setDelFlag("N");
        return address;
    }
}
```

- [ ] **Step 3: 运行测试验证**

Run: `mvn test -Dtest=MyBatisClientAddressRepositoryTest`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add src/test/java/com/address/repository/MyBatisClientAddressRepositoryTest.java
git commit -m "test: 添加 MyBatisClientAddressRepositoryTest"
```

---

## Task 8: 完整测试验证

**Files:**
- 无修改

- [ ] **Step 1: 运行所有测试**

Run: `mvn test`
Expected: BUILD SUCCESS，所有测试通过

- [ ] **Step 2: 提交**

```bash
git add .
git commit -m "feat: 完成 MyBatis 迁移"
```

---

## 验证

```bash
# 1. 编译
mvn compile

# 2. 运行 MyBatis 测试
mvn test -Dtest=MyBatisClientAddressRepositoryTest

# 3. 运行 JDBC 测试（确保不破坏现有功能）
mvn test -Dtest=JdbcClientAddressRepositoryTest

# 4. 运行所有测试
mvn test
```

---

## 自我检查清单

- [ ] Task 1: pom.xml 添加了 mybatis 和 mybatis-spring 依赖
- [ ] Task 2: mybatis-config.xml 存在且格式正确
- [ ] Task 3: CifAddressMapper.xml 包含所有 6 个方法的 SQL
- [ ] Task 4: CifAddressMapper.java 使用 @Update 注解实现 delete
- [ ] Task 5: MyBatisConfig 正确初始化 SqlSessionFactory 并注入 DataSource
- [ ] Task 6: MyBatisClientAddressRepository 实现了 ClientAddressRepository 接口
- [ ] Task 7: MyBatisClientAddressRepositoryTest 覆盖了 CRUD 所有操作
- [ ] Task 8: 所有测试通过
