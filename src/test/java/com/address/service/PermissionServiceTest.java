package com.address.service;

import com.address.dto.PermissionCreateRequest;
import com.address.dto.PermissionResponse;
import com.address.dto.PermissionUpdateRequest;
import com.address.model.SysPermission;
import com.address.repository.SysPermissionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PermissionServiceTest {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Test
    public void testCreate() {
        PermissionCreateRequest request = new PermissionCreateRequest();
        request.setPermissionCode("PERM_" + System.currentTimeMillis());
        request.setPermissionName("测试权限");
        request.setMenuUrl("/test");

        PermissionResponse response = permissionService.create(request);

        assertNotNull(response);
        assertNotNull(response.getPermissionId());
        assertEquals("测试权限", response.getPermissionName());
    }

    @Test
    public void testQuery() {
        SysPermission permission = new SysPermission();
        permission.setPermissionId(System.currentTimeMillis());
        permission.setPermissionCode("PERM_QUERY_" + System.currentTimeMillis());
        permission.setPermissionName("查询测试权限");
        permission.setMenuUrl("/query");
        permission.setCreateTime(LocalDateTime.now());
        sysPermissionMapper.insert(permission);

        List<PermissionResponse> responses = permissionService.query();

        assertFalse(responses.isEmpty());
    }

    @Test
    @Sql(scripts = "classpath:sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdate() {
        SysPermission permission = new SysPermission();
        permission.setPermissionId(System.currentTimeMillis());
        permission.setPermissionCode("PERM_UPDATE_" + System.currentTimeMillis());
        permission.setPermissionName("更新前权限");
        permission.setMenuUrl("/old");
        permission.setCreateTime(LocalDateTime.now());
        sysPermissionMapper.insert(permission);

        PermissionUpdateRequest request = new PermissionUpdateRequest();
        request.setPermissionId(permission.getPermissionId());
        request.setPermissionName("更新后权限");
        request.setMenuUrl("/new");

        PermissionResponse response = permissionService.update(request);

        assertEquals("更新后权限", response.getPermissionName());
        assertEquals("/new", response.getMenuUrl());
    }

    @Test
    public void testDelete() {
        SysPermission permission = new SysPermission();
        permission.setPermissionId(System.currentTimeMillis());
        permission.setPermissionCode("PERM_DELETE_" + System.currentTimeMillis());
        permission.setPermissionName("删除测试权限");
        permission.setMenuUrl("/delete");
        permission.setCreateTime(LocalDateTime.now());
        sysPermissionMapper.insert(permission);

        permissionService.delete(permission.getPermissionId());

        SysPermission deleted = sysPermissionMapper.findById(permission.getPermissionId());
        assertNull(deleted);
    }
}
