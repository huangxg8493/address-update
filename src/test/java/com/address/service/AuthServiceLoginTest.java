package com.address.service;

import com.address.dto.LoginRequest;
import com.address.dto.LoginResponse;
import com.address.dto.LoginResult;
import com.address.model.SysUser;
import com.address.repository.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceLoginTest extends BaseServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testLoginWithFullInfo_success() {
        // 准备测试用户
        Long userId = 1000000001L;
        String phone = "13800001111";
        String password = "test123";

        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus("Y");
        user.setUserName("测试用户");
        user.setEmail("test@example.com");
        user.setCity("广东省深圳市南山区");
        user.setHobby("篮球");
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setUpdateTime(java.time.LocalDateTime.now());
        sysUserMapper.insert(user);

        try {
            LoginRequest request = new LoginRequest();
            request.setPhone(phone);
            request.setPassword(password);

            LoginResult result = authService.login(request);

            assertTrue(result.isSuccess(), "登录应成功");
            assertNotNull(result.getToken(), "token不为空");

            LoginResponse response = result.toLoginResponse();
            assertNotNull(response.getUser(), "user不为空");
            assertEquals("测试用户", response.getUser().getUserName(), "用户姓名正确");
            assertNotNull(response.getRoles(), "roles不为空");
            assertNotNull(response.getMenus(), "menus不为空");
        } finally {
            // 清理测试数据
            sysUserMapper.deleteById(userId);
        }
    }

    @Test
    public void testLogin_userNotFound() {
        LoginRequest request = new LoginRequest();
        request.setPhone("13999999999");
        request.setPassword("any");

        LoginResult result = authService.login(request);

        assertFalse(result.isSuccess(), "用户不存在应失败");
        assertEquals("101001", result.getCode());
    }

    @Test
    public void testLogin_wrongPassword() {
        // 创建用户
        Long userId = 1000000002L;
        String phone = "13800002222";
        String password = "test123";

        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPhone(phone);
        user.setPassword(passwordEncoder.encode(password));
        user.setStatus("Y");
        user.setUserName("测试用户2");
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setUpdateTime(java.time.LocalDateTime.now());
        sysUserMapper.insert(user);

        try {
            LoginRequest request = new LoginRequest();
            request.setPhone(phone);
            request.setPassword("wrongpassword");

            LoginResult result = authService.login(request);

            assertFalse(result.isSuccess(), "密码错误应失败");
            assertEquals("101003", result.getCode());
        } finally {
            sysUserMapper.deleteById(userId);
        }
    }
}
