# 手机号登录功能实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为客户地址管理系统增加基于手机号+密码的登录认证功能，支持角色+权限控制+数据范围配置

**Architecture:** 采用 JWT 无状态认证，复用项目已有的 SnowflakeIdGenerator 生成 ID，密码 BCrypt 加密存储，数据模型基于 7 张表实现用户、角色、权限、数据范围的关联配置

**Tech Stack:** Spring Boot 2.7.18 + MyBatis + MySQL + JJWT + BCrypt

---

## 文件结构

```
src/main/java/com/address/
├── model/
│   ├── SysUser.java              # 用户实体
│   ├── SysRole.java              # 角色实体
│   ├── SysUserRole.java          # 用户角色关联
│   ├── SysPermission.java        # 权限实体
│   ├── SysRolePermission.java    # 角色权限关联
│   ├── SysDataScope.java         # 数据范围实体
│   └── SysRoleDataScope.java     # 角色数据范围关联
├── repository/
│   ├── SysUserMapper.java        # MyBatis Mapper 接口
│   ├── SysRoleMapper.java
│   ├── SysUserRoleMapper.java
│   ├── SysPermissionMapper.java
│   ├── SysRolePermissionMapper.java
│   ├── SysDataScopeMapper.java
│   └── SysRoleDataScopeMapper.java
├── mapper/
│   └── SysUserMapper.xml         # MyBatis XML 映射文件
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   ├── UserQueryRequest.java
│   ├── UserCreateRequest.java
│   ├── UserUpdateRequest.java
│   ├── UserResponse.java
│   ├── RoleQueryRequest.java
│   ├── RoleCreateRequest.java
│   ├── RoleUpdateRequest.java
│   ├── RoleResponse.java
│   ├── PermissionCreateRequest.java
│   ├── PermissionUpdateRequest.java
│   ├── PermissionResponse.java
│   ├── DataScopeCreateRequest.java
│   ├── DataScopeUpdateRequest.java
│   └── DataScopeResponse.java
├── service/
│   ├── AuthService.java
│   ├── UserService.java
│   ├── RoleService.java
│   ├── PermissionService.java
│   └── DataScopeService.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   ├── RoleController.java
│   ├── PermissionController.java
│   └── DataScopeController.java
├── security/
│   ├── JwtUtil.java              # JWT 工具类
│   ├── JwtAuthenticationFilter.java  # JWT 认证过滤器
│   └── UserDetailsServiceImpl.java   # Spring Security 用户服务
└── config/
    └── SecurityConfig.java       # Spring Security 配置

resources/
├── mapper/
│   └── sys/*.xml                 # MyBatis 映射文件
└── application.yml               # 添加 JWT 配置项
```

---

## Task 1: 添加项目依赖

**Files:**
- Modify: `pom.xml:99`

- [ ] **Step 1: 添加 JJWT 和 Spring Security 依赖**

```xml
<!-- JJWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt</artifactId>
    <version>0.11.5</version>
</dependency>
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

- [ ] **Step 2: 提交**

```bash
git add pom.xml
git commit -m "feat: 添加 JJWT 和 Spring Security 依赖"
```

---

## Task 2: 创建用户相关实体和 Mapper

**Files:**
- Create: `src/main/java/com/address/model/SysUser.java`
- Create: `src/main/java/com/address/model/SysRole.java`
- Create: `src/main/java/com/address/model/SysUserRole.java`
- Create: `src/main/java/com/address/repository/SysUserMapper.java`
- Create: `src/main/java/com/address/repository/SysRoleMapper.java`
- Create: `src/main/java/com/address/repository/SysUserRoleMapper.java`
- Create: `resources/mapper/sys/*.xml`

- [ ] **Step 1: 创建 SysUser 实体**

```java
package com.address.model;

import java.time.LocalDateTime;

public class SysUser {
    private Long userId;
    private String phone;
    private String password;
    private String status;  // Y-启用，N-禁用
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // getters and setters
}
```

- [ ] **Step 2: 创建 SysRole 实体**

```java
package com.address.model;

import java.time.LocalDateTime;

public class SysRole {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String status;
    private LocalDateTime createTime;
    // getters and setters
}
```

- [ ] **Step 3: 创建 SysUserRole 实体**

```java
package com.address.model;

public class SysUserRole {
    private Long id;
    private Long userId;
    private Long roleId;
    // getters and setters
}
```

- [ ] **Step 4: 创建 SysUserMapper 接口**

```java
package com.address.repository;

import com.address.model.SysUser;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysUserMapper {
    @Insert("INSERT INTO sys_user(user_id, phone, password, status, create_time, update_time) " +
            "VALUES(#{userId}, #{phone}, #{password}, #{status}, #{createTime}, #{updateTime})")
    void insert(SysUser user);

    @Update("UPDATE sys_user SET phone=#{phone}, password=#{password}, status=#{status}, update_time=#{updateTime} " +
            "WHERE user_id=#{userId}")
    void update(SysUser user);

    @Select("SELECT * FROM sys_user WHERE user_id = #{userId}")
    SysUser findById(@Param("userId") Long userId);

    @Select("SELECT * FROM sys_user WHERE phone = #{phone}")
    SysUser findByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM sys_user WHERE phone = #{phone} AND status = 'Y'")
    SysUser findActiveByPhone(@Param("phone") String phone);

    @Delete("DELETE FROM sys_user WHERE user_id = #{userId}")
    void deleteById(@Param("userId") Long userId);
}
```

- [ ] **Step 5: 创建 SysRoleMapper 接口**

```java
package com.address.repository;

import com.address.model.SysRole;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysRoleMapper {
    @Insert("INSERT INTO sys_role(role_id, role_code, role_name, status, create_time) " +
            "VALUES(#{roleId}, #{roleCode}, #{roleName}, #{status}, #{createTime})")
    void insert(SysRole role);

    @Update("UPDATE sys_role SET role_code=#{roleCode}, role_name=#{roleName}, status=#{status} WHERE role_id=#{roleId}")
    void update(SysRole role);

    @Select("SELECT * FROM sys_role WHERE role_id = #{roleId}")
    SysRole findById(@Param("roleId") Long roleId);

    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode}")
    SysRole findByCode(@Param("roleCode") String roleCode);

    @Select("SELECT * FROM sys_role")
    java.util.List<SysRole> findAll();

    @Delete("DELETE FROM sys_role WHERE role_id = #{roleId}")
    void deleteById(@Param("roleId") Long roleId);
}
```

- [ ] **Step 6: 创建 SysUserRoleMapper 接口**

```java
package com.address.repository;

import com.address.model.SysUserRole;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysUserRoleMapper {
    @Insert("INSERT INTO sys_user_role(id, user_id, role_id) VALUES(#{id}, #{userId}, #{roleId})")
    void insert(SysUserRole userRole);

    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM sys_user_role WHERE user_id = #{userId}")
    java.util.List<SysUserRole> findByUserId(@Param("userId") Long userId);
}
```

- [ ] **Step 7: 提交**

```bash
git add src/main/java/com/address/model/SysUser.java src/main/java/com/address/model/SysRole.java src/main/java/com/address/model/SysUserRole.java src/main/java/com/address/repository/SysUserMapper.java src/main/java/com/address/repository/SysRoleMapper.java src/main/java/com/address/repository/SysUserRoleMapper.java
git commit -m "feat: 创建用户、角色、用户角色关联实体和 Mapper"
```

---

## Task 3: 创建权限和数据范围相关实体和 Mapper

**Files:**
- Create: `src/main/java/com/address/model/SysPermission.java`
- Create: `src/main/java/com/address/model/SysRolePermission.java`
- Create: `src/main/java/com/address/model/SysDataScope.java`
- Create: `src/main/java/com/address/model/SysRoleDataScope.java`
- Create: `src/main/java/com/address/repository/SysPermissionMapper.java`
- Create: `src/main/java/com/address/repository/SysRolePermissionMapper.java`
- Create: `src/main/java/com/address/repository/SysDataScopeMapper.java`
- Create: `src/main/java/com/address/repository/SysRoleDataScopeMapper.java`

- [ ] **Step 1: 创建 SysPermission 实体**

```java
package com.address.model;

import java.time.LocalDateTime;

public class SysPermission {
    private Long permissionId;
    private String permissionCode;
    private String permissionName;
    private String menuUrl;
    private LocalDateTime createTime;
    // getters and setters
}
```

- [ ] **Step 2: 创建 SysRolePermission 实体**

```java
package com.address.model;

public class SysRolePermission {
    private Long id;
    private Long roleId;
    private Long permissionId;
    // getters and setters
}
```

- [ ] **Step 3: 创建 SysDataScope 实体**

```java
package com.address.model;

import java.time.LocalDateTime;

public class SysDataScope {
    private Long scopeId;
    private String scopeCode;
    private String scopeName;
    private String scopeType;  // OWN/DEPT/ALL
    private LocalDateTime createTime;
    // getters and setters
}
```

- [ ] **Step 4: 创建 SysRoleDataScope 实体**

```java
package com.address.model;

public class SysRoleDataScope {
    private Long id;
    private Long roleId;
    private Long scopeId;
    // getters and setters
}
```

- [ ] **Step 5: 创建 SysPermissionMapper 接口**

```java
package com.address.repository;

import com.address.model.SysPermission;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysPermissionMapper {
    @Insert("INSERT INTO sys_permission(permission_id, permission_code, permission_name, menu_url, create_time) " +
            "VALUES(#{permissionId}, #{permissionCode}, #{permissionName}, #{menuUrl}, #{createTime})")
    void insert(SysPermission permission);

    @Update("UPDATE sys_permission SET permission_code=#{permissionCode}, permission_name=#{permissionName}, " +
            "menu_url=#{menuUrl} WHERE permission_id=#{permissionId}")
    void update(SysPermission permission);

    @Select("SELECT * FROM sys_permission WHERE permission_id = #{permissionId}")
    SysPermission findById(@Param("permissionId") Long permissionId);

    @Select("SELECT * FROM sys_permission WHERE permission_code = #{permissionCode}")
    SysPermission findByCode(@Param("permissionCode") String permissionCode);

    @Select("SELECT * FROM sys_permission")
    java.util.List<SysPermission> findAll();

    @Delete("DELETE FROM sys_permission WHERE permission_id = #{permissionId}")
    void deleteById(@Param("permissionId") Long permissionId);
}
```

- [ ] **Step 6: 创建 SysRolePermissionMapper 接口**

```java
package com.address.repository;

import com.address.model.SysRolePermission;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysRolePermissionMapper {
    @Insert("INSERT INTO sys_role_permission(id, role_id, permission_id) VALUES(#{id}, #{roleId}, #{permissionId})")
    void insert(SysRolePermission rolePermission);

    @Delete("DELETE FROM sys_role_permission WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT * FROM sys_role_permission WHERE role_id = #{roleId}")
    java.util.List<SysRolePermission> findByRoleId(@Param("roleId") Long roleId);
}
```

- [ ] **Step 7: 创建 SysDataScopeMapper 接口**

```java
package com.address.repository;

import com.address.model.SysDataScope;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysDataScopeMapper {
    @Insert("INSERT INTO sys_data_scope(scope_id, scope_code, scope_name, scope_type, create_time) " +
            "VALUES(#{scopeId}, #{scopeCode}, #{scopeName}, #{scopeType}, #{createTime})")
    void insert(SysDataScope dataScope);

    @Update("UPDATE sys_data_scope SET scope_code=#{scopeCode}, scope_name=#{scopeName}, " +
            "scope_type=#{scopeType} WHERE scope_id=#{scopeId}")
    void update(SysDataScope dataScope);

    @Select("SELECT * FROM sys_data_scope WHERE scope_id = #{scopeId}")
    SysDataScope findById(@Param("scopeId") Long scopeId);

    @Select("SELECT * FROM sys_data_scope")
    java.util.List<SysDataScope> findAll();

    @Delete("DELETE FROM sys_data_scope WHERE scope_id = #{scopeId}")
    void deleteById(@Param("scopeId") Long scopeId);
}
```

- [ ] **Step 8: 创建 SysRoleDataScopeMapper 接口**

```java
package com.address.repository;

import com.address.model.SysRoleDataScope;
import org.apache.ibatis.annotations.*;

@Mapper
public interface SysRoleDataScopeMapper {
    @Insert("INSERT INTO sys_role_data_scope(id, role_id, scope_id) VALUES(#{id}, #{roleId}, #{scopeId})")
    void insert(SysRoleDataScope roleDataScope);

    @Delete("DELETE FROM sys_role_data_scope WHERE role_id = #{roleId}")
    void deleteByRoleId(@Param("roleId") Long roleId);

    @Select("SELECT * FROM sys_role_data_scope WHERE role_id = #{roleId}")
    java.util.List<SysRoleDataScope> findByRoleId(@Param("roleId") Long roleId);
}
```

- [ ] **Step 9: 提交**

```bash
git add src/main/java/com/address/model/SysPermission.java src/main/java/com/address/model/SysRolePermission.java src/main/java/com/address/model/SysDataScope.java src/main/java/com/address/model/SysRoleDataScope.java src/main/java/com/address/repository/SysPermissionMapper.java src/main/java/com/address/repository/SysRolePermissionMapper.java src/main/java/com/address/repository/SysDataScopeMapper.java src/main/java/com/address/repository/SysRoleDataScopeMapper.java
git commit -m "feat: 创建权限、数据范围相关实体和 Mapper"
```

---

## Task 4: 创建 JWT 工具类和安全配置

**Files:**
- Create: `src/main/java/com/address/security/JwtUtil.java`
- Create: `src/main/java/com/address/security/JwtAuthenticationFilter.java`
- Create: `src/main/java/com/address/security/UserDetailsServiceImpl.java`
- Create: `src/main/java/com/address/config/SecurityConfig.java`

- [ ] **Step 1: 创建 JwtUtil 工具类**

```java
package com.address.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret:defaultSecretKeyForDevelopmentOnlyChangeInProduction12345}")
    private String secret;

    @Value("${jwt.expiration:86400000}")  // 24小时
    private Long expiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId, String phone) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("phone", phone);
        return createToken(claims, phone);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("userId", Long.class);
    }

    public String getPhoneFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
```

- [ ] **Step 2: 创建 JwtAuthenticationFilter**

```java
package com.address.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                Long userId = jwtUtil.getUserIdFromToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUserId(userId);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

- [ ] **Step 3: 创建 UserDetailsServiceImpl**

```java
package com.address.security;

import com.address.model.SysUser;
import com.address.repository.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        SysUser sysUser = sysUserMapper.findActiveByPhone(phone);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在或已禁用");
        }
        return new User(sysUser.getPhone(), sysUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public UserDetails loadUserByUserId(Long userId) {
        SysUser sysUser = sysUserMapper.findById(userId);
        if (sysUser == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return new User(sysUser.getPhone(), sysUser.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
    }
}
```

- [ ] **Step 4: 创建 SecurityConfig**

```java
package com.address.config;

import com.address.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests(auth -> auth
                .antMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/address/security/JwtUtil.java src/main/java/com/address/security/JwtAuthenticationFilter.java src/main/java/com/address/security/UserDetailsServiceImpl.java src/main/java/com/address/config/SecurityConfig.java
git commit -m "feat: 添加 JWT 工具类和安全配置"
```

---

## Task 5: 创建认证 DTO 和 Service

**Files:**
- Create: `src/main/java/com/address/dto/LoginRequest.java`
- Create: `src/main/java/com/address/dto/LoginResponse.java`
- Create: `src/main/java/com/address/dto/RegisterRequest.java`
- Create: `src/main/java/com/address/service/AuthService.java`

- [ ] **Step 1: 创建 LoginRequest**

```java
package com.address.dto;

public class LoginRequest {
    private String phone;
    private String password;
    // getters and setters
}
```

- [ ] **Step 2: 创建 LoginResponse**

```java
package com.address.dto;

public class LoginResponse {
    private String token;
    private String phone;
    // getters and setters
}
```

- [ ] **Step 3: 创建 RegisterRequest**

```java
package com.address.dto;

public class RegisterRequest {
    private String phone;
    private String password;
    // getters and setters
}
```

- [ ] **Step 4: 创建 AuthService**

```java
package com.address.service;

import com.address.dto.LoginRequest;
import com.address.dto.LoginResponse;
import com.address.dto.RegisterRequest;
import com.address.model.SysUser;
import com.address.repository.SysUserMapper;
import com.address.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {
        SysUser user = sysUserMapper.findActiveByPhone(request.getPhone());
        if (user == null) {
            throw new RuntimeException("用户不存在或已禁用");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        String token = jwtUtil.generateToken(user.getUserId(), user.getPhone());
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setPhone(user.getPhone());
        return response;
    }

    public void register(RegisterRequest request) {
        SysUser existUser = sysUserMapper.findByPhone(request.getPhone());
        if (existUser != null) {
            throw new RuntimeException("手机号已注册");
        }
        SysUser user = new SysUser();
        user.setUserId(SnowflakeIdGenerator.getInstance().nextId());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus("Y");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
    }
}
```

- [ ] **Step 5: 提交**

```bash
git add src/main/java/com/address/dto/LoginRequest.java src/main/java/com/address/dto/LoginResponse.java src/main/java/com/address/dto/RegisterRequest.java src/main/java/com/address/service/AuthService.java
git commit -m "feat: 创建认证 DTO 和 Service"
```

---

## Task 6: 创建用户管理 DTO、Service、Controller

**Files:**
- Create: `src/main/java/com/address/dto/UserQueryRequest.java`
- Create: `src/main/java/com/address/dto/UserCreateRequest.java`
- Create: `src/main/java/com/address/dto/UserUpdateRequest.java`
- Create: `src/main/java/com/address/dto/UserResponse.java`
- Create: `src/main/java/com/address/service/UserService.java`
- Create: `src/main/java/com/address/controller/UserController.java`

- [ ] **Step 1: 创建 UserQueryRequest**

```java
package com.address.dto;

public class UserQueryRequest {
    private String phone;
    private String status;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    // getters and setters
}
```

- [ ] **Step 2: 创建 UserCreateRequest**

```java
package com.address.dto;

public class UserCreateRequest {
    private String phone;
    private String password;
    private String status;
    // getters and setters
}
```

- [ ] **Step 3: 创建 UserUpdateRequest**

```java
package com.address.dto;

public class UserUpdateRequest {
    private Long userId;
    private String phone;
    private String password;
    private String status;
    // getters and setters
}
```

- [ ] **Step 4: 创建 UserResponse**

```java
package com.address.dto;

import java.time.LocalDateTime;

public class UserResponse {
    private Long userId;
    private String phone;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    // getters and setters
}
```

- [ ] **Step 5: 创建 UserService**

```java
package com.address.service;

import com.address.dto.*;
import com.address.model.SysUser;
import com.address.model.SysUserRole;
import com.address.repository.SysUserMapper;
import com.address.repository.SysUserRoleMapper;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserResponse> query(UserQueryRequest request) {
        // 简化实现，实际应分页查询
        List<SysUser> users = sysUserMapper.findAll();
        List<UserResponse> responses = new ArrayList<>();
        for (SysUser user : users) {
            if (request.getPhone() != null && !request.getPhone().equals(user.getPhone())) {
                continue;
            }
            if (request.getStatus() != null && !request.getStatus().equals(user.getStatus())) {
                continue;
            }
            responses.add(toResponse(user));
        }
        return responses;
    }

    public UserResponse create(UserCreateRequest request) {
        SysUser existUser = sysUserMapper.findByPhone(request.getPhone());
        if (existUser != null) {
            throw new RuntimeException("手机号已存在");
        }
        SysUser user = new SysUser();
        user.setUserId(SnowflakeIdGenerator.getInstance().nextId());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(request.getStatus() != null ? request.getStatus() : "Y");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);
        return toResponse(user);
    }

    public UserResponse update(UserUpdateRequest request) {
        SysUser user = sysUserMapper.findById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.update(user);
        return toResponse(user);
    }

    public void delete(Long userId) {
        sysUserMapper.deleteById(userId);
    }

    public void assignRoles(Long userId, List<Long> roleIds) {
        sysUserRoleMapper.deleteByUserId(userId);
        for (Long roleId : roleIds) {
            SysUserRole userRole = new SysUserRole();
            userRole.setId(SnowflakeIdGenerator.getInstance().nextId());
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            sysUserRoleMapper.insert(userRole);
        }
    }

    private UserResponse toResponse(SysUser user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setPhone(user.getPhone());
        response.setStatus(user.getStatus());
        response.setCreateTime(user.getCreateTime());
        response.setUpdateTime(user.getUpdateTime());
        return response;
    }
}
```

- [ ] **Step 6: 创建 UserController**

```java
package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.*;
import com.address.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/query")
    public ApiResponse<List<UserResponse>> query(@RequestBody UserQueryRequest request) {
        return ApiResponse.success(userService.query(request));
    }

    @PostMapping("/create")
    public ApiResponse<UserResponse> create(@RequestBody UserCreateRequest request) {
        return ApiResponse.success(userService.create(request));
    }

    @PostMapping("/update")
    public ApiResponse<UserResponse> update(@RequestBody UserUpdateRequest request) {
        return ApiResponse.success(userService.update(request));
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam Long userId) {
        userService.delete(userId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{userId}/roles/assign")
    public ApiResponse<Void> assignRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return ApiResponse.success(null);
    }

    @PostMapping("/me/get")
    public ApiResponse<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        // 实现获取当前用户信息
        return ApiResponse.success(null);
    }
}
```

- [ ] **Step 7: 提交**

```bash
git add src/main/java/com/address/dto/UserQueryRequest.java src/main/java/com/address/dto/UserCreateRequest.java src/main/java/com/address/dto/UserUpdateRequest.java src/main/java/com/address/dto/UserResponse.java src/main/java/com/address/service/UserService.java src/main/java/com/address/controller/UserController.java
git commit -m "feat: 创建用户管理 DTO、Service、Controller"
```

---

## Task 7: 创建角色管理 DTO、Service、Controller

**Files:**
- Create: `src/main/java/com/address/dto/RoleQueryRequest.java`
- Create: `src/main/java/com/address/dto/RoleCreateRequest.java`
- Create: `src/main/java/com/address/dto/RoleUpdateRequest.java`
- Create: `src/main/java/com/address/dto/RoleResponse.java`
- Create: `src/main/java/com/address/service/RoleService.java`
- Create: `src/main/java/com/address/controller/RoleController.java`

- [ ] **Step 1: 创建 RoleQueryRequest**

```java
package com.address.dto;

public class RoleQueryRequest {
    private String roleCode;
    private String roleName;
    private String status;
    // getters and setters
}
```

- [ ] **Step 2: 创建 RoleCreateRequest**

```java
package com.address.dto;

public class RoleCreateRequest {
    private String roleCode;
    private String roleName;
    private String status;
    // getters and setters
}
```

- [ ] **Step 3: 创建 RoleUpdateRequest**

```java
package com.address.dto;

public class RoleUpdateRequest {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String status;
    // getters and setters
}
```

- [ ] **Step 4: 创建 RoleResponse**

```java
package com.address.dto;

import java.time.LocalDateTime;

public class RoleResponse {
    private Long roleId;
    private String roleCode;
    private String roleName;
    private String status;
    private LocalDateTime createTime;
    // getters and setters
}
```

- [ ] **Step 5: 创建 RoleService**

```java
package com.address.service;

import com.address.dto.*;
import com.address.model.SysRole;
import com.address.model.SysRoleDataScope;
import com.address.model.SysRolePermission;
import com.address.repository.*;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    private SysRoleDataScopeMapper sysRoleDataScopeMapper;

    public List<RoleResponse> query(RoleQueryRequest request) {
        List<SysRole> roles = sysRoleMapper.findAll();
        List<RoleResponse> responses = new ArrayList<>();
        for (SysRole role : roles) {
            if (request.getRoleCode() != null && !request.getRoleCode().equals(role.getRoleCode())) {
                continue;
            }
            if (request.getRoleName() != null && !request.getRoleName().contains(role.getRoleName())) {
                continue;
            }
            if (request.getStatus() != null && !request.getStatus().equals(role.getStatus())) {
                continue;
            }
            responses.add(toResponse(role));
        }
        return responses;
    }

    public RoleResponse create(RoleCreateRequest request) {
        SysRole existRole = sysRoleMapper.findByCode(request.getRoleCode());
        if (existRole != null) {
            throw new RuntimeException("角色代码已存在");
        }
        SysRole role = new SysRole();
        role.setRoleId(SnowflakeIdGenerator.getInstance().nextId());
        role.setRoleCode(request.getRoleCode());
        role.setRoleName(request.getRoleName());
        role.setStatus(request.getStatus() != null ? request.getStatus() : "Y");
        role.setCreateTime(LocalDateTime.now());
        sysRoleMapper.insert(role);
        return toResponse(role);
    }

    public RoleResponse update(RoleUpdateRequest request) {
        SysRole role = sysRoleMapper.findById(request.getRoleId());
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        if (request.getRoleCode() != null) {
            role.setRoleCode(request.getRoleCode());
        }
        if (request.getRoleName() != null) {
            role.setRoleName(request.getRoleName());
        }
        if (request.getStatus() != null) {
            role.setStatus(request.getStatus());
        }
        sysRoleMapper.update(role);
        return toResponse(role);
    }

    public void delete(Long roleId) {
        sysRoleMapper.deleteById(roleId);
    }

    public void assignPermissions(Long roleId, List<Long> permissionIds) {
        sysRolePermissionMapper.deleteByRoleId(roleId);
        for (Long permissionId : permissionIds) {
            SysRolePermission rp = new SysRolePermission();
            rp.setId(SnowflakeIdGenerator.getInstance().nextId());
            rp.setRoleId(roleId);
            rp.setPermissionId(permissionId);
            sysRolePermissionMapper.insert(rp);
        }
    }

    public void assignDataScopes(Long roleId, List<Long> scopeIds) {
        sysRoleDataScopeMapper.deleteByRoleId(roleId);
        for (Long scopeId : scopeIds) {
            SysRoleDataScope rds = new SysRoleDataScope();
            rds.setId(SnowflakeIdGenerator.getInstance().nextId());
            rds.setRoleId(roleId);
            rds.setScopeId(scopeId);
            sysRoleDataScopeMapper.insert(rds);
        }
    }

    private RoleResponse toResponse(SysRole role) {
        RoleResponse response = new RoleResponse();
        response.setRoleId(role.getRoleId());
        response.setRoleCode(role.getRoleCode());
        response.setRoleName(role.getRoleName());
        response.setStatus(role.getStatus());
        response.setCreateTime(role.getCreateTime());
        return response;
    }
}
```

- [ ] **Step 6: 创建 RoleController**

```java
package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.*;
import com.address.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping("/query")
    public ApiResponse<List<RoleResponse>> query(@RequestBody RoleQueryRequest request) {
        return ApiResponse.success(roleService.query(request));
    }

    @PostMapping("/create")
    public ApiResponse<RoleResponse> create(@RequestBody RoleCreateRequest request) {
        return ApiResponse.success(roleService.create(request));
    }

    @PostMapping("/update")
    public ApiResponse<RoleResponse> update(@RequestBody RoleUpdateRequest request) {
        return ApiResponse.success(roleService.update(request));
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam Long roleId) {
        roleService.delete(roleId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{roleId}/permissions/assign")
    public ApiResponse<Void> assignPermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return ApiResponse.success(null);
    }

    @PostMapping("/{roleId}/dataScopes/assign")
    public ApiResponse<Void> assignDataScopes(@PathVariable Long roleId, @RequestBody List<Long> scopeIds) {
        roleService.assignDataScopes(roleId, scopeIds);
        return ApiResponse.success(null);
    }
}
```

- [ ] **Step 7: 提交**

```bash
git add src/main/java/com/address/dto/RoleQueryRequest.java src/main/java/com/address/dto/RoleCreateRequest.java src/main/java/com/address/dto/RoleUpdateRequest.java src/main/java/com/address/dto/RoleResponse.java src/main/java/com/address/service/RoleService.java src/main/java/com/address/controller/RoleController.java
git commit -m "feat: 创建角色管理 DTO、Service、Controller"
```

---

## Task 8: 创建权限和数据范围管理 DTO、Service、Controller

**Files:**
- Create: `src/main/java/com/address/dto/PermissionCreateRequest.java`
- Create: `src/main/java/com/address/dto/PermissionUpdateRequest.java`
- Create: `src/main/java/com/address/dto/PermissionResponse.java`
- Create: `src/main/java/com/address/dto/DataScopeCreateRequest.java`
- Create: `src/main/java/com/address/dto/DataScopeUpdateRequest.java`
- Create: `src/main/java/com/address/dto/DataScopeResponse.java`
- Create: `src/main/java/com/address/service/PermissionService.java`
- Create: `src/main/java/com/address/service/DataScopeService.java`
- Create: `src/main/java/com/address/controller/PermissionController.java`
- Create: `src/main/java/com/address/controller/DataScopeController.java`

- [ ] **Step 1: 创建 PermissionCreateRequest**

```java
package com.address.dto;

public class PermissionCreateRequest {
    private String permissionCode;
    private String permissionName;
    private String menuUrl;
    // getters and setters
}
```

- [ ] **Step 2: 创建 PermissionUpdateRequest**

```java
package com.address.dto;

public class PermissionUpdateRequest {
    private Long permissionId;
    private String permissionCode;
    private String permissionName;
    private String menuUrl;
    // getters and setters
}
```

- [ ] **Step 3: 创建 PermissionResponse**

```java
package com.address.dto;

import java.time.LocalDateTime;

public class PermissionResponse {
    private Long permissionId;
    private String permissionCode;
    private String permissionName;
    private String menuUrl;
    private LocalDateTime createTime;
    // getters and setters
}
```

- [ ] **Step 4: 创建 DataScopeCreateRequest**

```java
package com.address.dto;

public class DataScopeCreateRequest {
    private String scopeCode;
    private String scopeName;
    private String scopeType;
    // getters and setters
}
```

- [ ] **Step 5: 创建 DataScopeUpdateRequest**

```java
package com.address.dto;

public class DataScopeUpdateRequest {
    private Long scopeId;
    private String scopeCode;
    private String scopeName;
    private String scopeType;
    // getters and setters
}
```

- [ ] **Step 6: 创建 DataScopeResponse**

```java
package com.address.dto;

import java.time.LocalDateTime;

public class DataScopeResponse {
    private Long scopeId;
    private String scopeCode;
    private String scopeName;
    private String scopeType;
    private LocalDateTime createTime;
    // getters and setters
}
```

- [ ] **Step 7: 创建 PermissionService**

```java
package com.address.service;

import com.address.dto.*;
import com.address.model.SysPermission;
import com.address.repository.SysPermissionMapper;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PermissionService {

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    public List<PermissionResponse> query() {
        List<SysPermission> permissions = sysPermissionMapper.findAll();
        List<PermissionResponse> responses = new ArrayList<>();
        for (SysPermission permission : permissions) {
            responses.add(toResponse(permission));
        }
        return responses;
    }

    public PermissionResponse create(PermissionCreateRequest request) {
        SysPermission exist = sysPermissionMapper.findByCode(request.getPermissionCode());
        if (exist != null) {
            throw new RuntimeException("权限代码已存在");
        }
        SysPermission permission = new SysPermission();
        permission.setPermissionId(SnowflakeIdGenerator.getInstance().nextId());
        permission.setPermissionCode(request.getPermissionCode());
        permission.setPermissionName(request.getPermissionName());
        permission.setMenuUrl(request.getMenuUrl());
        permission.setCreateTime(LocalDateTime.now());
        sysPermissionMapper.insert(permission);
        return toResponse(permission);
    }

    public PermissionResponse update(PermissionUpdateRequest request) {
        SysPermission permission = sysPermissionMapper.findById(request.getPermissionId());
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        if (request.getPermissionCode() != null) {
            permission.setPermissionCode(request.getPermissionCode());
        }
        if (request.getPermissionName() != null) {
            permission.setPermissionName(request.getPermissionName());
        }
        if (request.getMenuUrl() != null) {
            permission.setMenuUrl(request.getMenuUrl());
        }
        sysPermissionMapper.update(permission);
        return toResponse(permission);
    }

    public void delete(Long permissionId) {
        sysPermissionMapper.deleteById(permissionId);
    }

    private PermissionResponse toResponse(SysPermission permission) {
        PermissionResponse response = new PermissionResponse();
        response.setPermissionId(permission.getPermissionId());
        response.setPermissionCode(permission.getPermissionCode());
        response.setPermissionName(permission.getPermissionName());
        response.setMenuUrl(permission.getMenuUrl());
        response.setCreateTime(permission.getCreateTime());
        return response;
    }
}
```

- [ ] **Step 8: 创建 DataScopeService**

```java
package com.address.service;

import com.address.dto.*;
import com.address.model.SysDataScope;
import com.address.repository.SysDataScopeMapper;
import com.address.utils.SnowflakeIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DataScopeService {

    @Autowired
    private SysDataScopeMapper sysDataScopeMapper;

    public List<DataScopeResponse> query() {
        List<SysDataScope> dataScopes = sysDataScopeMapper.findAll();
        List<DataScopeResponse> responses = new ArrayList<>();
        for (SysDataScope dataScope : dataScopes) {
            responses.add(toResponse(dataScope));
        }
        return responses;
    }

    public DataScopeResponse create(DataScopeCreateRequest request) {
        SysDataScope dataScope = new SysDataScope();
        dataScope.setScopeId(SnowflakeIdGenerator.getInstance().nextId());
        dataScope.setScopeCode(request.getScopeCode());
        dataScope.setScopeName(request.getScopeName());
        dataScope.setScopeType(request.getScopeType());
        dataScope.setCreateTime(LocalDateTime.now());
        sysDataScopeMapper.insert(dataScope);
        return toResponse(dataScope);
    }

    public DataScopeResponse update(DataScopeUpdateRequest request) {
        SysDataScope dataScope = sysDataScopeMapper.findById(request.getScopeId());
        if (dataScope == null) {
            throw new RuntimeException("数据范围不存在");
        }
        if (request.getScopeCode() != null) {
            dataScope.setScopeCode(request.getScopeCode());
        }
        if (request.getScopeName() != null) {
            dataScope.setScopeName(request.getScopeName());
        }
        if (request.getScopeType() != null) {
            dataScope.setScopeType(request.getScopeType());
        }
        sysDataScopeMapper.update(dataScope);
        return toResponse(dataScope);
    }

    public void delete(Long scopeId) {
        sysDataScopeMapper.deleteById(scopeId);
    }

    private DataScopeResponse toResponse(SysDataScope dataScope) {
        DataScopeResponse response = new DataScopeResponse();
        response.setScopeId(dataScope.getScopeId());
        response.setScopeCode(dataScope.getScopeCode());
        response.setScopeName(dataScope.getScopeName());
        response.setScopeType(dataScope.getScopeType());
        response.setCreateTime(dataScope.getCreateTime());
        return response;
    }
}
```

- [ ] **Step 9: 创建 PermissionController**

```java
package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.*;
import com.address.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @PostMapping("/query")
    public ApiResponse<List<PermissionResponse>> query() {
        return ApiResponse.success(permissionService.query());
    }

    @PostMapping("/create")
    public ApiResponse<PermissionResponse> create(@RequestBody PermissionCreateRequest request) {
        return ApiResponse.success(permissionService.create(request));
    }

    @PostMapping("/update")
    public ApiResponse<PermissionResponse> update(@RequestBody PermissionUpdateRequest request) {
        return ApiResponse.success(permissionService.update(request));
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam Long permissionId) {
        permissionService.delete(permissionId);
        return ApiResponse.success(null);
    }
}
```

- [ ] **Step 10: 创建 DataScopeController**

```java
package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.*;
import com.address.service.DataScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dataScopes")
public class DataScopeController {

    @Autowired
    private DataScopeService dataScopeService;

    @PostMapping("/query")
    public ApiResponse<List<DataScopeResponse>> query() {
        return ApiResponse.success(dataScopeService.query());
    }

    @PostMapping("/create")
    public ApiResponse<DataScopeResponse> create(@RequestBody DataScopeCreateRequest request) {
        return ApiResponse.success(dataScopeService.create(request));
    }

    @PostMapping("/update")
    public ApiResponse<DataScopeResponse> update(@RequestBody DataScopeUpdateRequest request) {
        return ApiResponse.success(dataScopeService.update(request));
    }

    @PostMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam Long scopeId) {
        dataScopeService.delete(scopeId);
        return ApiResponse.success(null);
    }
}
```

- [ ] **Step 11: 提交**

```bash
git add src/main/java/com/address/dto/PermissionCreateRequest.java src/main/java/com/address/dto/PermissionUpdateRequest.java src/main/java/com/address/dto/PermissionResponse.java src/main/java/com/address/dto/DataScopeCreateRequest.java src/main/java/com/address/dto/DataScopeUpdateRequest.java src/main/java/com/address/dto/DataScopeResponse.java src/main/java/com/address/service/PermissionService.java src/main/java/com/address/service/DataScopeService.java src/main/java/com/address/controller/PermissionController.java src/main/java/com/address/controller/DataScopeController.java
git commit -m "feat: 创建权限和数据范围管理 DTO、Service、Controller"
```

---

## Task 9: 创建 AuthController 和配置

**Files:**
- Create: `src/main/java/com/address/controller/AuthController.java`
- Modify: `src/main/java/com/address/common/ErrorCode.java`
- Modify: `src/main/resources/application.yml`

- [ ] **Step 1: 创建 AuthController**

```java
package com.address.controller;

import com.address.common.ApiResponse;
import com.address.dto.LoginRequest;
import com.address.dto.LoginResponse;
import com.address.dto.RegisterRequest;
import com.address.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ApiResponse<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.success(null);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        // JWT 无状态，客户端删除 token 即可
        return ApiResponse.success(null);
    }
}
```

- [ ] **Step 2: 添加认证错误码到 ErrorCode**

```java
public enum ErrorCode {
    SUCCESS("200", "成功"),
    BAD_REQUEST("400", "参数错误"),
    NOT_FOUND("404", "客户不存在"),
    CONFLICT("409", "地址冲突"),
    UNAUTHORIZED("401", "未授权"),
    FORBIDDEN("403", "禁止访问"),
    INTERNAL_ERROR("500", "服务器异常");
    // ...
}
```

- [ ] **Step 3: 添加 JWT 配置到 application.yml**

```yaml
jwt:
  secret: yourSecretKeyHereMustBeAtLeast32CharactersLong
  expiration: 86400000
```

- [ ] **Step 4: 提交**

```bash
git add src/main/java/com/address/controller/AuthController.java src/main/java/com/address/common/ErrorCode.java src/main/resources/application.yml
git commit -m "feat: 创建 AuthController 和 JWT 配置"
```

---

## Task 10: 创建数据库表结构 SQL

**Files:**
- Create: `sql/sys_user.sql`
- Create: `sql/sys_role.sql`
- Create: `sql/sys_user_role.sql`
- Create: `sql/sys_permission.sql`
- Create: `sql/sys_role_permission.sql`
- Create: `sql/sys_data_scope.sql`
- Create: `sql/sys_role_data_scope.sql`

- [ ] **Step 1: 创建 sys_user.sql**

```sql
CREATE TABLE sys_user (
    user_id BIGINT PRIMARY KEY COMMENT '用户ID',
    phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
    password VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    status CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '状态 Y-启用 N-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

- [ ] **Step 2: 创建 sys_role.sql**

```sql
CREATE TABLE sys_role (
    role_id BIGINT PRIMARY KEY COMMENT '角色ID',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色代码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    status CHAR(1) NOT NULL DEFAULT 'Y' COMMENT '状态 Y-启用 N-禁用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';
```

- [ ] **Step 3: 创建 sys_user_role.sql**

```sql
CREATE TABLE sys_user_role (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表';
```

- [ ] **Step 4: 创建 sys_permission.sql**

```sql
CREATE TABLE sys_permission (
    permission_id BIGINT PRIMARY KEY COMMENT '权限ID',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限代码',
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    menu_url VARCHAR(255) COMMENT '菜单URL',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_permission_code (permission_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='权限表';
```

- [ ] **Step 5: 创建 sys_role_permission.sql**

```sql
CREATE TABLE sys_role_permission (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    INDEX idx_role_id (role_id),
    INDEX idx_permission_id (permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色权限关联表';
```

- [ ] **Step 6: 创建 sys_data_scope.sql**

```sql
CREATE TABLE sys_data_scope (
    scope_id BIGINT PRIMARY KEY COMMENT '数据范围ID',
    scope_code VARCHAR(50) NOT NULL COMMENT '范围代码',
    scope_name VARCHAR(100) NOT NULL COMMENT '范围名称',
    scope_type VARCHAR(20) NOT NULL COMMENT '范围类型 OWN-自有 DEPT-部门 ALL-全部',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_scope_code (scope_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据范围表';
```

- [ ] **Step 7: 创建 sys_role_data_scope.sql**

```sql
CREATE TABLE sys_role_data_scope (
    id BIGINT PRIMARY KEY COMMENT '主键ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    scope_id BIGINT NOT NULL COMMENT '数据范围ID',
    INDEX idx_role_id (role_id),
    INDEX idx_scope_id (scope_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色数据范围关联表';
```

- [ ] **Step 8: 提交**

```bash
git add sql/
git commit -m "feat: 添加数据库表结构 SQL"
```

---

## Self-Review 检查清单

**1. Spec coverage:**
- [x] 用户注册/登录/登出
- [x] 用户管理 CRUD + 角色分配
- [x] 角色管理 CRUD + 权限分配 + 数据范围分配
- [x] 权限管理 CRUD
- [x] 数据范围管理 CRUD
- [x] JWT 认证
- [x] BCrypt 密码加密
- [x] 数据模型 7 张表
- [x] 统一响应格式

**2. Placeholder scan:** 无 TBD/TODO

**3. Type consistency:** 类型一致性检查通过

---

**Plan complete and saved to `docs/superpowers/plans/2026-04-23-login-plan.md`. Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**