package com.address.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Map;

public class DbConfig {
    private static final Logger logger = LoggerFactory.getLogger(DbConfig.class);
    private static final Map<String, Object> config;
    private static final DataSource dataSource;

    static {
        Yaml yaml = new Yaml();
        try (InputStream in = DbConfig.class.getClassLoader().getResourceAsStream("config.yaml")) {
            config = yaml.load(in);
        } catch (Exception e) {
            logger.error("加载 config.yaml 失败", e);
            throw new RuntimeException("加载 config.yaml 失败", e);
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getUrl());
        hikariConfig.setUsername(getUsername());
        hikariConfig.setPassword(getPassword());
        hikariConfig.setMaximumPoolSize(getMaximumPoolSize());
        hikariConfig.setMinimumIdle(getMinimumIdle());
        hikariConfig.setConnectionTimeout(getConnectionTimeout());
        hikariConfig.setIdleTimeout(getIdleTimeout());
        hikariConfig.setMaxLifetime(getMaxLifetime());
        dataSource = new HikariDataSource(hikariConfig);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getDatabase() {
        return (Map<String, Object>) config.get("database");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getHikari() {
        return (Map<String, Object>) config.get("hikari");
    }

    public static String getDriver() {
        return (String) getDatabase().get("driver");
    }

    public static String getUrl() {
        return (String) getDatabase().get("url");
    }

    public static String getUsername() {
        return (String) getDatabase().get("username");
    }

    public static String getPassword() {
        return (String) getDatabase().get("password");
    }

    public static int getMaximumPoolSize() {
        return (Integer) getHikari().getOrDefault("maximumPoolSize", 10);
    }

    public static int getMinimumIdle() {
        return (Integer) getHikari().getOrDefault("minimumIdle", 2);
    }

    public static long getConnectionTimeout() {
        return ((Number) getHikari().getOrDefault("connectionTimeout", 30000L)).longValue();
    }

    public static long getIdleTimeout() {
        return ((Number) getHikari().getOrDefault("idleTimeout", 600000L)).longValue();
    }

    public static long getMaxLifetime() {
        return ((Number) getHikari().getOrDefault("maxLifetime", 1800000L)).longValue();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
