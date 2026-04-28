package com.address.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class DeleteMenuTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void deleteMenu() {
        int deleted = jdbcTemplate.update("DELETE FROM sys_menu WHERE menu_id = 999997");
        System.out.println("Deleted " + deleted + " row(s)");
    }
}