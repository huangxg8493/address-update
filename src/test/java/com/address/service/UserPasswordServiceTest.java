package com.address.service;

import com.address.dto.PasswordChangeRequest;
import com.address.dto.PasswordResetRequest;
import com.address.model.SysUser;
import com.address.repository.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserPasswordServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testChangePassword_success() {
        String testPhone = "13800138000";
        String oldPassword = "123456";
        String newPassword = "654321";

        SysUser existUser = sysUserMapper.findByPhone(testPhone);
        if (existUser == null) {
            return;
        }

        userService.changePassword(testPhone, oldPassword, newPassword);

        SysUser updatedUser = sysUserMapper.findByPhone(testPhone);
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }

    @Test
    public void testChangePassword_oldPasswordError() {
        String testPhone = "13800138000";
        String wrongOldPassword = "wrong";
        String newPassword = "654321";

        SysUser existUser = sysUserMapper.findByPhone(testPhone);
        if (existUser == null) {
            return;
        }

        assertThrows(RuntimeException.class, () -> {
            userService.changePassword(testPhone, wrongOldPassword, newPassword);
        });
    }

    @Test
    public void testChangePassword_invalidNewPassword() {
        String testPhone = "13800138000";
        String oldPassword = "123456";
        String invalidNewPassword = "123";

        SysUser existUser = sysUserMapper.findByPhone(testPhone);
        if (existUser == null) {
            return;
        }

        assertThrows(RuntimeException.class, () -> {
            userService.changePassword(testPhone, oldPassword, invalidNewPassword);
        });
    }

    @Test
    public void testResetPassword_success() {
        Long testUserId = 1L;
        String newPassword = "reset123";

        SysUser existUser = sysUserMapper.findById(testUserId);
        if (existUser == null) {
            return;
        }

        userService.resetPassword(testUserId, newPassword);

        SysUser updatedUser = sysUserMapper.findById(testUserId);
        assertTrue(passwordEncoder.matches(newPassword, updatedUser.getPassword()));
    }

    @Test
    public void testResetPassword_userNotExist() {
        Long nonExistUserId = 999999L;

        assertThrows(RuntimeException.class, () -> {
            userService.resetPassword(nonExistUserId, "123456");
        });
    }
}