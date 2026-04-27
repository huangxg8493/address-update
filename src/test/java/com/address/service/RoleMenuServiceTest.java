package com.address.service;

import com.address.model.SysMenu;
import com.address.repository.SysRoleMenuMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(scripts = "classpath:sql/schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RoleMenuServiceTest {

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Test
    @Transactional
    public void testAssignMenus() {
        Long roleId = 1L;
        List<Long> menuIds = Arrays.asList(1L, 2L, 3L);

        roleMenuService.assignMenus(roleId, menuIds);

        List<Long> result = sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }

    @Test
    @Transactional
    public void testAssignMenus_EmptyList() {
        Long roleId = 1L;

        roleMenuService.assignMenus(roleId, Arrays.asList());

        List<Long> result = sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
        assertEquals(0, result.size());
    }

    @Test
    @Transactional
    public void testAssignMenus_ClearOldMenus() {
        Long roleId = 1L;
        roleMenuService.assignMenus(roleId, Arrays.asList(1L, 2L));

        roleMenuService.assignMenus(roleId, Arrays.asList(3L));

        List<Long> result = sysRoleMenuMapper.selectMenuIdsByRoleId(roleId);
        assertEquals(1, result.size());
        assertTrue(result.contains(3L));
        assertFalse(result.contains(1L));
        assertFalse(result.contains(2L));
    }
}