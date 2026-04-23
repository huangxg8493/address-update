package com.address.service;

import com.address.dto.RoleCreateRequest;
import com.address.dto.RoleQueryRequest;
import com.address.dto.RoleResponse;
import com.address.dto.RoleUpdateRequest;
import com.address.model.SysRole;
import com.address.repository.SysRoleMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RoleServiceTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Test
    @Sql(scripts = "classpath:sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testCreate() {
        RoleCreateRequest request = new RoleCreateRequest();
        request.setRoleCode("ROLE_TEST_" + System.currentTimeMillis());
        request.setRoleName("测试角色");
        request.setStatus("Y");

        RoleResponse response = roleService.create(request);

        assertNotNull(response);
        assertNotNull(response.getRoleId());
        assertEquals("测试角色", response.getRoleName());
        assertEquals("Y", response.getStatus());
    }

    @Test
    @Sql(scripts = "classpath:sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testQuery() {
        SysRole role = new SysRole();
        role.setRoleId(System.currentTimeMillis());
        role.setRoleCode("ROLE_QUERY_" + System.currentTimeMillis());
        role.setRoleName("查询测试角色");
        role.setStatus("Y");
        role.setCreateTime(LocalDateTime.now());
        sysRoleMapper.insert(role);

        RoleQueryRequest request = new RoleQueryRequest();
        List<RoleResponse> responses = roleService.query(request);

        assertFalse(responses.isEmpty());
    }

    @Test
    @Sql(scripts = "classpath:sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testUpdate() {
        SysRole role = new SysRole();
        role.setRoleId(System.currentTimeMillis());
        role.setRoleCode("ROLE_UPDATE_" + System.currentTimeMillis());
        role.setRoleName("更新前角色");
        role.setStatus("Y");
        role.setCreateTime(LocalDateTime.now());
        sysRoleMapper.insert(role);

        RoleUpdateRequest request = new RoleUpdateRequest();
        request.setRoleId(role.getRoleId());
        request.setRoleName("更新后角色");
        request.setStatus("N");

        RoleResponse response = roleService.update(request);

        assertEquals("更新后角色", response.getRoleName());
        assertEquals("N", response.getStatus());
    }

    @Test
    @Sql(scripts = "classpath:sql/clean_test_data.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void testDelete() {
        SysRole role = new SysRole();
        role.setRoleId(System.currentTimeMillis());
        role.setRoleCode("ROLE_DELETE_" + System.currentTimeMillis());
        role.setRoleName("删除测试角色");
        role.setStatus("Y");
        role.setCreateTime(LocalDateTime.now());
        sysRoleMapper.insert(role);

        roleService.delete(role.getRoleId());

        SysRole deleted = sysRoleMapper.findById(role.getRoleId());
        assertNull(deleted);
    }
}
