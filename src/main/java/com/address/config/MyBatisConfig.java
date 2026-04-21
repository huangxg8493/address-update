package com.address.config;

import com.address.model.CifAddress;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.Configuration;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

public class MyBatisConfig {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisConfig.class);
    private static final SqlSessionFactory SQL_SESSION_FACTORY;

    static {
        try {
            Configuration config = new Configuration();

            config.setEnvironment(
                new Environment(
                    "development",
                    new JdbcTransactionFactory(),
                    DbConfig.getDataSource()
                )
            );
            config.setMapUnderscoreToCamelCase(true);
            config.getTypeAliasRegistry().registerAlias("CifAddress", CifAddress.class);

            InputStream mapperInputStream = Resources.getResourceAsStream("mapper/CifAddressMapper.xml");
            XMLMapperBuilder mapperBuilder =
                new XMLMapperBuilder(
                    mapperInputStream, config, "mapper/CifAddressMapper.xml", config.getSqlFragments());
            mapperBuilder.parse();

            SQL_SESSION_FACTORY = new SqlSessionFactoryBuilder().build(config);
            logger.info("MyBatisConfig 初始化成功");
        } catch (Exception e) {
            logger.error("MyBatisConfig 初始化失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("初始化 MyBatis SqlSessionFactory 失败", e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return SQL_SESSION_FACTORY;
    }
}
