package com.address.service;

import com.address.dto.LoginRequest;
import com.address.dto.LoginResponse;
import com.address.dto.RegisterRequest;
import com.address.model.SysUser;
import com.address.repository.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Sql(scripts = "classpath:sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setPhone("13900000001");
        request.setPassword("password123");

        authService.register(request);

        SysUser user = sysUserMapper.findByPhone("13900000001");
        assertNotNull(user);
        assertEquals("13900000001", user.getPhone());
        assertEquals("Y", user.getStatus());
        assertTrue(passwordEncoder.matches("password123", user.getPassword()));
    }

    @Test
    public void testRegisterDuplicatePhone() {
        SysUser existUser = new SysUser();
        existUser.setUserId(System.currentTimeMillis());
        existUser.setPhone("13900000002");
        existUser.setPassword(passwordEncoder.encode("password"));
        existUser.setStatus("Y");
        existUser.setCreateTime(LocalDateTime.now());
        existUser.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(existUser);

        RegisterRequest request = new RegisterRequest();
        request.setPhone("13900000002");
        request.setPassword("password123");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.register(request);
        });
        assertEquals("手机号已注册", exception.getMessage());
    }

    @Test
    @Sql(scripts = "classpath:sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testLogin() {
        SysUser user = new SysUser();
        user.setUserId(System.currentTimeMillis());
        user.setPhone("13900000003");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setStatus("Y");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);

        LoginRequest request = new LoginRequest();
        request.setPhone("13900000003");
        request.setPassword("password123");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("13900000003", response.getPhone());
    }

    @Test
    public void testLoginUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setPhone("13999999999");
        request.setPassword("password123");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });
        assertEquals("用户不存在或已禁用", exception.getMessage());
    }

    @Test
    @Sql(scripts = "classpath:sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testLoginWrongPassword() {
        SysUser user = new SysUser();
        user.setUserId(System.currentTimeMillis());
        user.setPhone("13900000004");
        user.setPassword(passwordEncoder.encode("correctPassword"));
        user.setStatus("Y");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);

        LoginRequest request = new LoginRequest();
        request.setPhone("13900000004");
        request.setPassword("wrongPassword");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login(request);
        });
        assertEquals("密码错误", exception.getMessage());
    }
}
