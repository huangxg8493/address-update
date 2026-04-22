package com.address.repository;

import com.address.constants.Constants;
import com.address.model.CifAddress;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MyBatisClientAddressRepository implements ClientAddressRepository {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisClientAddressRepository.class);

    private final SqlSessionFactory sqlSessionFactory;

    @Autowired
    public MyBatisClientAddressRepository(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    private SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }

    private <T> T execute(SqlSessionCallback<T> callback) {
        try (SqlSession session = getSqlSession()) {
            T result = callback.doInSession(session);
            session.commit();
            return result;
        }
    }

    @FunctionalInterface
    private interface SqlSessionCallback<T> {
        T doInSession(SqlSession session);
    }

    @Override
    public List<CifAddress> findByClientNo(String clientNo) {
        return execute(session -> {
            CifAddressMapper mapper = session.getMapper(CifAddressMapper.class);
            return mapper.findByClientNo(clientNo, Constants.NO);
        });
    }

    @Override
    public CifAddress findBySeqNo(String seqNo) {
        return execute(session -> {
            CifAddressMapper mapper = session.getMapper(CifAddressMapper.class);
            return mapper.findBySeqNo(seqNo);
        });
    }

    @Override
    public void save(CifAddress address) {
        logger.info("保存地址 clientNo={}", address.getClientNo());
        execute(session -> {
            session.getMapper(CifAddressMapper.class).save(address);
            return null;
        });
    }

    @Override
    public void update(CifAddress address) {
        logger.info("更新地址 seqNo={}", address.getSeqNo());
        execute(session -> {
            session.getMapper(CifAddressMapper.class).update(address);
            return null;
        });
    }

    @Override
    public void saveAll(List<CifAddress> addresses) {
        logger.info("批量保存地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        execute(session -> {
            session.getMapper(CifAddressMapper.class).saveAll(addresses);
            return null;
        });
    }

    @Override
    public void updateAll(List<CifAddress> addresses) {
        logger.info("批量更新地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        execute(session -> {
            session.getMapper(CifAddressMapper.class).batchUpdate(addresses);
            return null;
        });
    }

    /**
     * 使用 MyBatis BATCH Executor 模式的批量更新
     * 所有 SQL 在 flushStatements() 时一次性发送，效率高于循环单条 update
     */
    public void batchUpdateWithExecutor(List<CifAddress> addresses) {
        logger.info("BATCH模式批量更新地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        // 使用 BATCH executor 打开 session
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            CifAddressMapper mapper = session.getMapper(CifAddressMapper.class);
            for (CifAddress address : addresses) {
                mapper.update(address);
            }
            // 一次性发送所有批量 SQL 到数据库
            session.flushStatements();
            session.commit();
        }
    }

    @Override
    public void delete(String seqNo) {
        logger.info("删除地址 seqNo={}", seqNo);
        execute(session -> {
            session.getMapper(CifAddressMapper.class).delete(seqNo);
            return null;
        });
    }
}
