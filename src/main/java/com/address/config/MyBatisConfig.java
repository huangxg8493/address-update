package com.address.config;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.io.InputStream;

public class MyBatisConfig {

    private static final SqlSessionFactory SQL_SESSION_FACTORY;

    static {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);

            XMLConfigBuilder parser = new XMLConfigBuilder(inputStream);
            org.apache.ibatis.session.Configuration config = parser.parse();

            DataSource dataSource = DbConfig.getDataSource();
            config.setEnvironment(
                new org.apache.ibatis.mapping.Environment(
                    "development",
                    new JdbcTransactionFactory(),
                    dataSource
                )
            );

            SQL_SESSION_FACTORY = new SqlSessionFactoryBuilder().build(config);
        } catch (Exception e) {
            throw new RuntimeException("初始化 MyBatis SqlSessionFactory 失败", e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return SQL_SESSION_FACTORY;
    }
}
