# sys_user 省市区合并实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 sys_user 表的 province/city/district 三字段合并为 city（省市区组合字符串），新增 addr_detail 详细地址字段

**Architecture:** 修改 Model、DTO、Mapper、Service 层相关文件，更新 schema.sql 和接口文档

**Tech Stack:** Java8, MyBatis, Spring Boot, MySQL

---

## 涉及的文件

| 文件 | 修改内容 |
|------|----------|
| `src/main/java/com/address/model/SysUser.java` | 删除 province/city/district，新增 city/addrDetail |
| `src/main/java/com/address/repository/SysUserMapper.java` | 更新所有 SQL 和 @Results 映射 |
| `src/main/java/com/address/dto/UserInfo.java` | 删除 province/city/district，新增 city/addrDetail |
| `src/main/java/com/address/dto/UserResponse.java` | 同上 |
| `src/main/java/com/address/dto/UserCreateRequest.java` | 同上 |
| `src/main/java/com/address/dto/UserUpdateRequest.java` | 同上 |
| `src/main/java/com/address/service/AuthService.java` | 修改 register()、buildUserInfo() 字段映射 |
| `src/main/java/com/address/service/UserService.java` | 修改 create()、update()、toResponse() 字段映射 |
| `src/test/resources/sql/schema.sql` | 更新建表语句 |
| `docs/http/interface.md` | 更新接口文档 |

---

## Task 1: 修改 SysUser.java

**Files:**
- Modify: `src/main/java/com/address/model/SysUser.java`

- [ ] **Step 1: 修改 SysUser.java**

删除 province、city、district 字段，新增 city、addrDetail 字段：

```java
public class SysUser {
    private Long userId;
    private String phone;
    private String password;
    private String status;
    private String userName;
    private String email;
    private String city;        // 省市区组合，如"广东省深圳市南山区"
    private String addrDetail;  // 详细地址，如"科技园1号路101室"
    private String hobby;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // getter/setter...
}
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/model/SysUser.java
git commit -m "refactor: SysUser合并省市区字段为city，新增addrDetail"
```

---

## Task 2: 修改 SysUserMapper.java

**Files:**
- Modify: `src/main/java/com/address/repository/SysUserMapper.java`

- [ ] **Step 1: 修改 SysUserMapper.java**

更新所有 SQL 语句和 @Results 映射，将 province/city/district 替换为 city/addrDetail：

```java
@Insert("INSERT INTO SYS_USER(user_id, phone, password, status, user_name, email, city, addr_detail, hobby, create_time, update_time) " +
        "VALUES(#{userId}, #{phone}, #{password}, #{status}, #{userName}, #{email}, #{city}, #{addrDetail}, #{hobby}, #{createTime}, #{updateTime})")
void insert(SysUser user);

@Update("UPDATE SYS_USER SET phone=#{phone}, password=#{password}, status=#{status}, " +
        "user_name=#{userName}, email=#{email}, city=#{city}, addr_detail=#{addrDetail}, hobby=#{hobby}, " +
        "update_time=#{updateTime} WHERE user_id=#{userId}")
void update(SysUser user);

@Select("SELECT user_id, phone, password, status, user_name, email, city, addr_detail, hobby, create_time, update_time FROM SYS_USER WHERE user_id = #{userId}")
@Results({
    @Result(property = "userId", column = "user_id"),
    @Result(property = "phone", column = "phone"),
    @Result(property = "password", column = "password"),
    @Result(property = "status", column = "status"),
    @Result(property = "userName", column = "user_name"),
    @Result(property = "email", column = "email"),
    @Result(property = "city", column = "city"),
    @Result(property = "addrDetail", column = "addr_detail"),
    @Result(property = "hobby", column = "hobby"),
    @Result(property = "createTime", column = "create_time"),
    @Result(property = "updateTime", column = "update_time")
})
SysUser findById(@Param("userId") Long userId);
```

findByPhone、findActiveByPhone、findAll 方法同样更新。

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/repository/SysUserMapper.java
git commit -m "refactor: SysUserMapper更新字段映射"
```

---

## Task 3: 修改 DTO 文件

**Files:**
- Modify: `src/main/java/com/address/dto/UserInfo.java`
- Modify: `src/main/java/com/address/dto/UserResponse.java`
- Modify: `src/main/java/com/address/dto/UserCreateRequest.java`
- Modify: `src/main/java/com/address/dto/UserUpdateRequest.java`

- [ ] **Step 1: 修改 UserInfo.java**

删除 province/city/district，新增 city/addrDetail：

```java
public class UserInfo {
    private Long userId;
    private String userName;
    private String email;
    private String city;
    private String addrDetail;
    private String hobby;
    private String status;
    // getter/setter...
}
```

- [ ] **Step 2: 修改 UserResponse.java** (同样删除 province/city/district，新增 city/addrDetail)

- [ ] **Step 3: 修改 UserCreateRequest.java** (同样删除 province/city/district，新增 city/addrDetail)

- [ ] **Step 4: 修改 UserUpdateRequest.java** (同样删除 province/city/district，新增 city/addrDetail)

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/address/dto/UserInfo.java src/main/java/com/address/dto/UserResponse.java src/main/java/com/address/dto/UserCreateRequest.java src/main/java/com/address/dto/UserUpdateRequest.java
git commit -m "refactor: DTO删除省市区字段，新增city和addrDetail"
```

---

## Task 4: 修改 AuthService.java

**Files:**
- Modify: `src/main/java/com/address/service/AuthService.java`

- [ ] **Step 1: 修改 register() 方法中的字段映射**

```java
// 将
user.setProvince(request.getProvince());
user.setCity(request.getCity());
user.setDistrict(request.getDistrict());
// 替换为
user.setCity(request.getCity());
user.setAddrDetail(request.getAddrDetail());
```

- [ ] **Step 2: 修改 buildUserInfo() 方法中的字段映射**

```java
// 将
info.setProvince(user.getProvince());
info.setCity(user.getCity());
info.setDistrict(user.getDistrict());
// 替换为
info.setCity(user.getCity());
info.setAddrDetail(user.getAddrDetail());
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/service/AuthService.java
git commit -m "refactor: AuthService更新省市区字段映射"
```

---

## Task 5: 修改 UserService.java

**Files:**
- Modify: `src/main/java/com/address/service/UserService.java`

- [ ] **Step 1: 修改 create() 方法**

```java
// 将
user.setProvince(request.getProvince());
user.setCity(request.getCity());
user.setDistrict(request.getDistrict());
// 替换为
user.setCity(request.getCity());
user.setAddrDetail(request.getAddrDetail());
```

- [ ] **Step 2: 修改 update() 方法**

```java
// 将
if (request.getProvince() != null) { user.setProvince(request.getProvince()); }
if (request.getCity() != null) { user.setCity(request.getCity()); }
if (request.getDistrict() != null) { user.setDistrict(request.getDistrict()); }
// 替换为
if (request.getCity() != null) { user.setCity(request.getCity()); }
if (request.getAddrDetail() != null) { user.setAddrDetail(request.getAddrDetail()); }
```

- [ ] **Step 3: 修改 toResponse() 方法**

```java
// 将
response.setProvince(user.getProvince());
response.setCity(user.getCity());
response.setDistrict(user.getDistrict());
// 替换为
response.setCity(user.getCity());
response.setAddrDetail(user.getAddrDetail());
```

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/address/service/UserService.java
git commit -m "refactor: UserService更新省市区字段映射"
```

---

## Task 6: 更新 schema.sql

**Files:**
- Modify: `src/test/resources/sql/schema.sql`

- [ ] **Step 1: 更新建表语句**

```sql
CREATE TABLE IF NOT EXISTS sys_user (
    user_id BIGINT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status CHAR(1) NOT NULL DEFAULT 'Y',
    user_name VARCHAR(100),
    email VARCHAR(100),
    city VARCHAR(200),
    addr_detail VARCHAR(255),
    hobby VARCHAR(500),
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone)
);
```

- [ ] **Step 2: 提交**

```bash
git add src/test/resources/sql/schema.sql
git commit -m "refactor: schema.sql更新省市区字段"
```

---

## Task 7: 更新接口文档

**Files:**
- Modify: `docs/http/interface.md`

- [ ] **Step 1: 更新接口文档中所有涉及 province/city/district 的描述**

将注册接口、登录接口、用户查询接口等文档中的 province/city/district 字段替换为 city 和 addr_detail。

- [ ] **Step 2: 提交**

```bash
git add docs/http/interface.md
git commit -m "docs: 更新接口文档省市区字段说明"
```

---

## Task 8: 运行测试验证

**Files:**
- Test: `src/test/java/com/address/service/AuthServiceTest.java`
- Test: `src/test/java/com/address/service/UserServiceTest.java`

- [ ] **Step 1: 编译项目**

```bash
mvn compile
```

- [ ] **Step 2: 运行测试**

```bash
mvn test -Dtest=AuthServiceTest,UserServiceTest
```

- [ ] **Step 3: 提交**

```bash
git add -A
git commit -m "test: 验证省市区合并修改"
```

---

## 执行方式选择

**Plan complete and saved to `docs/superpowers/plans/2026-04-29-user-city-merge-plan.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
