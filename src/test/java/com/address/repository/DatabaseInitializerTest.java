package com.address.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@org.springframework.test.context.TestPropertySource(properties = {
    "spring.sql.init.mode=never"
})
public class DatabaseInitializerTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testCreateTables() {
        createTableIfNotExists("sys_user",
            "user_id BIGINT PRIMARY KEY, " +
            "phone VARCHAR(20) NOT NULL UNIQUE, " +
            "password VARCHAR(255) NOT NULL, " +
            "status CHAR(1) NOT NULL DEFAULT 'Y', " +
            "user_name VARCHAR(100), " +
            "email VARCHAR(100), " +
            "province VARCHAR(50), " +
            "city VARCHAR(50), " +
            "district VARCHAR(50), " +
            "hobby VARCHAR(500), " +
            "create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
            "update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
        );

        createTableIfNotExists("sys_role",
            "role_id BIGINT PRIMARY KEY, " +
            "role_code VARCHAR(50) NOT NULL UNIQUE, " +
            "role_name VARCHAR(100) NOT NULL, " +
            "status CHAR(1) NOT NULL DEFAULT 'Y', " +
            "create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
        );

        createTableIfNotExists("sys_user_role",
            "id BIGINT PRIMARY KEY, " +
            "user_id BIGINT NOT NULL, " +
            "role_id BIGINT NOT NULL"
        );

        createTableIfNotExists("sys_permission",
            "permission_id BIGINT PRIMARY KEY, " +
            "permission_code VARCHAR(100) NOT NULL UNIQUE, " +
            "permission_name VARCHAR(100) NOT NULL, " +
            "menu_url VARCHAR(255), " +
            "create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
        );

        createTableIfNotExists("sys_role_permission",
            "id BIGINT PRIMARY KEY, " +
            "role_id BIGINT NOT NULL, " +
            "permission_id BIGINT NOT NULL"
        );

        createTableIfNotExists("sys_data_scope",
            "scope_id BIGINT PRIMARY KEY, " +
            "scope_code VARCHAR(50) NOT NULL, " +
            "scope_name VARCHAR(100) NOT NULL, " +
            "scope_type VARCHAR(20) NOT NULL, " +
            "create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP"
        );

        createTableIfNotExists("sys_role_data_scope",
            "id BIGINT PRIMARY KEY, " +
            "role_id BIGINT NOT NULL, " +
            "scope_id BIGINT NOT NULL"
        );

        assertTrue(tableExists("sys_user"));
        assertTrue(tableExists("sys_role"));
        assertTrue(tableExists("sys_user_role"));
        assertTrue(tableExists("sys_permission"));
        assertTrue(tableExists("sys_role_permission"));
        assertTrue(tableExists("sys_data_scope"));
        assertTrue(tableExists("sys_role_data_scope"));
    }

    private void createTableIfNotExists(String tableName, String columns) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" + columns + ")";
        jdbcTemplate.execute(sql);
    }

    private boolean tableExists(String tableName) {
        try (Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            String catalog = conn.getCatalog();
            String schema = null;

            try (ResultSet rs = metaData.getTables(catalog, schema, tableName, new String[]{"TABLE"})) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }
}
