package com.address.config;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.Map;

public class DbConfig {
    private static final Map<String, Object> config;

    static {
        Yaml yaml = new Yaml();
        try (InputStream in = DbConfig.class.getClassLoader().getResourceAsStream("config.yaml")) {
            config = yaml.load(in);
        } catch (Exception e) {
            throw new RuntimeException("加载 config.yaml 失败", e);
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getDatabase() {
        return (Map<String, Object>) config.get("database");
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
}
