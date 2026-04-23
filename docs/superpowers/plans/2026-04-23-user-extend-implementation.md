# SysUser 字段扩展实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为 sys_user 表新增 6 个字段（userName, email, province, city, district, hobby）

**Architecture:** 修改现有实体类和 DTO，新增字段映射，保持接口兼容

**Tech Stack:** Java8 + Maven + MyBatis + Spring Boot

---

## 文件清单

| 文件 | 变更 |
|------|------|
| SysUser.java | 修改 - 新增字段及 getter/setter |
| SysUserMapper.java | 修改 - 新增字段映射 |
| UserCreateRequest.java | 修改 - 新增字段 |
| UserUpdateRequest.java | 修改 - 新增字段 |
| UserResponse.java | 修改 - 新增字段 |
| sql/sys_user.sql | 修改 - 更新建表语句 |

---

### Task 1: 修改 SysUser 实体

**Files:**
- Modify: `src/main/java/com/address/model/SysUser.java`

- [ ] **Step 1: 修改实体类，添加新字段**

```java
private String userName;    // 用户名称
private String email;       // 邮箱
private String province;     // 省
private String city;         // 市
private String district;     // 区
private String hobby;       // 业余爱好
```

- [ ] **Step 2: 添加 getter/setter 方法**

```java
public String getUserName() { return userName; }
public void setUserName(String userName) { this.userName = userName; }
public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }
public String getProvince() { return province; }
public void setProvince(String province) { this.province = province; }
public String getCity() { return city; }
public void setCity(String city) { this.city = city; }
public String getDistrict() { return district; }
public void setDistrict(String district) { this.district = district; }
public String getHobby() { return hobby; }
public void setHobby(String hobby) { this.hobby = hobby; }
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/model/SysUser.java
git commit -m "feat(model): SysUser 新增 userName, email, province, city, district, hobby 字段"
```

---

### Task 2: 修改 SysUserMapper

**Files:**
- Modify: `src/main/java/com/address/repository/SysUserMapper.java`

- [ ] **Step 1: 修改 insert 语句**

```java
@Insert("INSERT INTO sys_user(user_id, phone, password, status, user_name, email, province, city, district, hobby, create_time, update_time) " +
        "VALUES(#{userId}, #{phone}, #{password}, #{status}, #{userName}, #{email}, #{province}, #{city}, #{district}, #{hobby}, #{createTime}, #{updateTime})")
void insert(SysUser user);
```

- [ ] **Step 2: 修改 update 语句**

```java
@Update("UPDATE sys_user SET phone=#{phone}, password=#{password}, status=#{status}, " +
        "user_name=#{userName}, email=#{email}, province=#{province}, city=#{city}, district=#{district}, hobby=#{hobby}, " +
        "update_time=#{updateTime} WHERE user_id=#{userId}")
void update(SysUser user);
```

- [ ] **Step 3: 修改 findById 查询，添加字段映射**

```java
@Select("SELECT user_id, phone, password, status, user_name, email, province, city, district, hobby, create_time, update_time FROM sys_user WHERE user_id = #{userId}")
@Results({
    @Result(property = "userId", column = "user_id"),
    @Result(property = "phone", column = "phone"),
    @Result(property = "password", column = "password"),
    @Result(property = "status", column = "status"),
    @Result(property = "userName", column = "user_name"),
    @Result(property = "email", column = "email"),
    @Result(property = "province", column = "province"),
    @Result(property = "city", column = "city"),
    @Result(property = "district", column = "district"),
    @Result(property = "hobby", column = "hobby"),
    @Result(property = "createTime", column = "create_time"),
    @Result(property = "updateTime", column = "update_time")
})
SysUser findById(@Param("userId") Long userId);
```

- [ ] **Step 4: 修改 findByPhone 查询，添加字段映射**

```java
@Select("SELECT user_id, phone, password, status, user_name, email, province, city, district, hobby, create_time, update_time FROM sys_user WHERE phone = #{phone}")
@Results({
    @Result(property = "userId", column = "user_id"),
    @Result(property = "phone", column = "phone"),
    @Result(property = "password", column = "password"),
    @Result(property = "status", column = "status"),
    @Result(property = "userName", column = "user_name"),
    @Result(property = "email", column = "email"),
    @Result(property = "province", column = "province"),
    @Result(property = "city", column = "city"),
    @Result(property = "district", column = "district"),
    @Result(property = "hobby", column = "hobby"),
    @Result(property = "createTime", column = "create_time"),
    @Result(property = "updateTime", column = "update_time")
})
SysUser findByPhone(@Param("phone") String phone);
```

- [ ] **Step 5: 修改 findAll 查询，添加字段映射**

```java
@Select("SELECT user_id, phone, password, status, user_name, email, province, city, district, hobby, create_time, update_time FROM sys_user")
@Results({
    @Result(property = "userId", column = "user_id"),
    @Result(property = "phone", column = "phone"),
    @Result(property = "password", column = "password"),
    @Result(property = "status", column = "status"),
    @Result(property = "userName", column = "user_name"),
    @Result(property = "email", column = "email"),
    @Result(property = "province", column = "province"),
    @Result(property = "city", column = "city"),
    @Result(property = "district", column = "district"),
    @Result(property = "hobby", column = "hobby"),
    @Result(property = "createTime", column = "create_time"),
    @Result(property = "updateTime", column = "update_time")
})
java.util.List<SysUser> findAll();
```

- [ ] **Step 6: 提交**

```bash
git add src/main/java/com/address/repository/SysUserMapper.java
git commit -m "feat(mapper): SysUserMapper 新增字段映射"
```

---

### Task 3: 修改 UserCreateRequest

**Files:**
- Modify: `src/main/java/com/address/dto/UserCreateRequest.java`

- [ ] **Step 1: 添加新字段**

```java
private String userName;    // 用户名称
private String email;       // 邮箱
private String province;     // 省
private String city;         // 市
private String district;     // 区
private String hobby;       // 业余爱好
```

- [ ] **Step 2: 添加 getter/setter**

```java
public String getUserName() { return userName; }
public void setUserName(String userName) { this.userName = userName; }
public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }
public String getProvince() { return province; }
public void setProvince(String province) { this.province = province; }
public String getCity() { return city; }
public void setCity(String city) { this.city = city; }
public String getDistrict() { return district; }
public void setDistrict(String district) { this.district = district; }
public String getHobby() { return hobby; }
public void setHobby(String hobby) { this.hobby = hobby; }
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/dto/UserCreateRequest.java
git commit -m "feat(dto): UserCreateRequest 新增字段"
```

---

### Task 4: 修改 UserUpdateRequest

**Files:**
- Modify: `src/main/java/com/address/dto/UserUpdateRequest.java`

- [ ] **Step 1: 添加新字段**

```java
private String userName;    // 用户名称
private String email;       // 邮箱
private String province;     // 省
private String city;         // 市
private String district;     // 区
private String hobby;       // 业余爱好
```

- [ ] **Step 2: 添加 getter/setter**

```java
public String getUserName() { return userName; }
public void setUserName(String userName) { this.userName = userName; }
public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }
public String getProvince() { return province; }
public void setProvince(String province) { this.province = province; }
public String getCity() { return city; }
public void setCity(String city) { this.city = city; }
public String getDistrict() { return district; }
public void setDistrict(String district) { this.district = district; }
public String getHobby() { return hobby; }
public void setHobby(String hobby) { this.hobby = hobby; }
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/dto/UserUpdateRequest.java
git commit -m "feat(dto): UserUpdateRequest 新增字段"
```

---

### Task 5: 修改 UserResponse

**Files:**
- Modify: `src/main/java/com/address/dto/UserResponse.java`

- [ ] **Step 1: 添加新字段**

```java
private String userName;    // 用户名称
private String email;       // 邮箱
private String province;     // 省
private String city;         // 市
private String district;     // 区
private String hobby;       // 业余爱好
```

- [ ] **Step 2: 添加 getter/setter**

```java
public String getUserName() { return userName; }
public void setUserName(String userName) { this.userName = userName; }
public String getEmail() { return email; }
public void setEmail(String email) { this.email = email; }
public String getProvince() { return province; }
public void setProvince(String province) { this.province = province; }
public String getCity() { return city; }
public void setCity(String city) { this.city = city; }
public String getDistrict() { return district; }
public void setDistrict(String district) { this.district = district; }
public String getHobby() { return hobby; }
public void setHobby(String hobby) { this.hobby = hobby; }
```

- [ ] **Step 3: 提交**

```bash
git add src/main/java/com/address/dto/UserResponse.java
git commit -m "feat(dto): UserResponse 新增字段"
```

---

### Task 6: 修改 UserService

**Files:**
- Modify: `src/main/java/com/address/service/UserService.java`

- [ ] **Step 1: 修改 toResponse 方法，添加新字段映射**

```java
response.setUserName(user.getUserName());
response.setEmail(user.getEmail());
response.setProvince(user.getProvince());
response.setCity(user.getCity());
response.setDistrict(user.getDistrict());
response.setHobby(user.getHobby());
```

- [ ] **Step 2: 提交**

```bash
git add src/main/java/com/address/service/UserService.java
git commit -m "feat(service): UserService 新增字段映射"
```

---

### Task 7: 修改 sql/sys_user.sql

**Files:**
- Modify: `sql/sys_user.sql`

- [ ] **Step 1: 更新建表语句，添加新字段**

```sql
CREATE TABLE IF NOT EXISTS sys_user (
    user_id BIGINT PRIMARY KEY,
    phone VARCHAR(20) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    status CHAR(1) DEFAULT 'Y',
    user_name VARCHAR(100),
    email VARCHAR(100),
    province VARCHAR(50),
    city VARCHAR(50),
    district VARCHAR(50),
    hobby VARCHAR(500),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_phone (phone)
);
```

- [ ] **Step 2: 提交**

```bash
git add sql/sys_user.sql
git commit -m "feat(sql): sys_user 表新增字段"
```

---

### Task 8: 编译验证

- [ ] **Step 1: 运行编译**

```bash
mvn compile -q
```

- [ ] **Step 2: 运行测试**

```bash
mvn test -q
```

---

## 自检清单

1. **Spec coverage**: 设计文档中的所有字段都有对应实现
2. **Placeholder scan**: 无 TBD/TODO 占位符
3. **Type consistency**: 所有字段类型和名称一致
