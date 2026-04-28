package com.address.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class ShowMenusTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void showMenus() {
        List<Map<String, Object>> menus = jdbcTemplate.queryForList(
            "SELECT menu_id, menu_name, parent_id, is_leaf FROM sys_menu WHERE del_flag='N' ORDER BY sort_order"
        );
        System.out.println("================== Menu List ==================");
        for (int i = 0; i < menus.size(); i++) {
            Map<String, Object> menu = menus.get(i);
            System.out.println((i + 1) + ". ID=" + menu.get("menu_id") + " Name=" + menu.get("menu_name") + " ParentID=" + menu.get("parent_id") + " IsLeaf=" + menu.get("is_leaf"));
        }
        System.out.println("================== Total: " + menus.size() + " ==================");
    }
}