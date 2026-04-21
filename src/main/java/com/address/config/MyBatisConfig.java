package com.address.config;

import com.address.model.CifAddress;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import java.io.InputStream;

public class MyBatisConfig {

    private static final SqlSessionFactory SQL_SESSION_FACTORY;

    static {
        try {
            org.apache.ibatis.session.Configuration config = new org.apache.ibatis.session.Configuration();

            config.setEnvironment(
                new org.apache.ibatis.mapping.Environment(
                    "development",
                    new JdbcTransactionFactory(),
                    DbConfig.getDataSource()
                )
            );
            config.setMapUnderscoreToCamelCase(true);
            config.getTypeAliasRegistry().registerAlias("CifAddress", CifAddress.class);

            InputStream mapperInputStream = Resources.getResourceAsStream("mapper/CifAddressMapper.xml");
            org.apache.ibatis.builder.xml.XMLMapperBuilder mapperBuilder =
                new org.apache.ibatis.builder.xml.XMLMapperBuilder(
                    mapperInputStream, config, "mapper/CifAddressMapper.xml", config.getSqlFragments());
            mapperBuilder.parse();

            SQL_SESSION_FACTORY = new SqlSessionFactoryBuilder().build(config);
            System.out.println("MyBatisConfig 初始化成功");
        } catch (Exception e) {
            System.err.println("MyBatisConfig 初始化失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("初始化 MyBatis SqlSessionFactory 失败", e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return SQL_SESSION_FACTORY;
    }
}
