package com.address.service;

import com.address.dto.UserCreateRequest;
import com.address.dto.UserQueryRequest;
import com.address.dto.UserResponse;
import com.address.dto.UserUpdateRequest;
import com.address.model.SysUser;
import com.address.repository.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @Sql(scripts = "/sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreate() {
        UserCreateRequest request = new UserCreateRequest();
        request.setPhone("13900001001");
        request.setPassword("password123");
        request.setStatus("Y");

        UserResponse response = userService.create(request);

        assertNotNull(response);
        assertNotNull(response.getUserId());
        assertEquals("13900001001", response.getPhone());
        assertEquals("Y", response.getStatus());
    }

    @Test
    @Sql(scripts = "/sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testQuery() {
        SysUser user = new SysUser();
        user.setUserId(System.currentTimeMillis());
        user.setPhone("13900001002");
        user.setPassword(passwordEncoder.encode("password"));
        user.setStatus("Y");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);

        UserQueryRequest request = new UserQueryRequest();
        List<UserResponse> responses = userService.query(request);

        assertFalse(responses.isEmpty());
        boolean found = responses.stream().anyMatch(r -> r.getPhone().equals("13900001002"));
        assertTrue(found);
    }

    @Test
    @Sql(scripts = "/sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdate() {
        SysUser user = new SysUser();
        user.setUserId(System.currentTimeMillis());
        user.setPhone("13900001003");
        user.setPassword(passwordEncoder.encode("password"));
        user.setStatus("Y");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);

        UserUpdateRequest request = new UserUpdateRequest();
        request.setUserId(user.getUserId());
        request.setPhone("13900001003");
        request.setStatus("N");

        UserResponse response = userService.update(request);

        assertEquals("N", response.getStatus());
    }

    @Test
    @Sql(scripts = "/sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testDelete() {
        SysUser user = new SysUser();
        user.setUserId(System.currentTimeMillis());
        user.setPhone("13900001004");
        user.setPassword(passwordEncoder.encode("password"));
        user.setStatus("Y");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserMapper.insert(user);

        userService.delete(user.getUserId());

        SysUser deleted = sysUserMapper.findById(user.getUserId());
        assertNull(deleted);
    }
}
