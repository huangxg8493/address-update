package com.address.repository;

import com.address.model.SysMenu;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SysMenuMapperTest {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        // 使用 JDBC 建表
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS sys_menu (" +
            "menu_id BIGINT PRIMARY KEY, " +
            "menu_name VARCHAR(100) NOT NULL, " +
            "menu_url VARCHAR(255), " +
            "icon VARCHAR(100), " +
            "sort_order INT DEFAULT 0, " +
            "status CHAR(1) DEFAULT 'Y', " +
            "is_leaf CHAR(1) DEFAULT 'Y', " +
            "level_depth INT DEFAULT 1, " +
            "component VARCHAR(255), " +
            "component_path VARCHAR(255), " +
            "parent_id BIGINT, " +
            "del_flag CHAR(1) DEFAULT 'N', " +
            "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
            "INDEX idx_parent_id (parent_id), " +
            "INDEX idx_del_flag (del_flag))");

        // 清理测试数据
        jdbcTemplate.update("DELETE FROM sys_menu WHERE menu_id >= 900000");
    }

    @Test
    void testInsertAndFindById() {
        SysMenu menu = new SysMenu();
        menu.setMenuId(999999L);
        menu.setMenuName("测试菜单");
        menu.setMenuUrl("/test");
        menu.setStatus("Y");
        menu.setIsLeaf("Y");
        menu.setLevelDepth(1);
        menu.setDelFlag("N");
        menu.setCreateTime(java.time.LocalDateTime.now());

        sysMenuMapper.insert(menu);

        SysMenu found = sysMenuMapper.findById(999999L);
        assertNotNull(found);
        assertEquals("测试菜单", found.getMenuName());
        assertEquals("/test", found.getMenuUrl());
    }

    @Test
    void testFindAll() {
        List<SysMenu> menus = sysMenuMapper.findAll();
        assertNotNull(menus);
    }

    @Test
    void testSoftDelete() {
        SysMenu menu = new SysMenu();
        menu.setMenuId(999998L);
        menu.setMenuName("待删除菜单");
        menu.setMenuUrl("/delete");
        menu.setStatus("Y");
        menu.setIsLeaf("Y");
        menu.setLevelDepth(1);
        menu.setDelFlag("N");
        menu.setCreateTime(java.time.LocalDateTime.now());

        sysMenuMapper.insert(menu);

        sysMenuMapper.deleteById(999998L);

        SysMenu found = sysMenuMapper.findById(999998L);
        assertNull(found);
    }

    @Test
    void testUpdate() {
        SysMenu menu = new SysMenu();
        menu.setMenuId(999997L);
        menu.setMenuName("原名称");
        menu.setMenuUrl("/original");
        menu.setStatus("Y");
        menu.setIsLeaf("Y");
        menu.setLevelDepth(1);
        menu.setDelFlag("N");
        menu.setCreateTime(java.time.LocalDateTime.now());

        sysMenuMapper.insert(menu);

        menu.setMenuName("新名称");
        sysMenuMapper.update(menu);

        SysMenu updated = sysMenuMapper.findById(999997L);
        assertEquals("新名称", updated.getMenuName());
    }
}
