package com.address.repository;

import com.address.config.MyBatisConfig;
import com.address.constants.Constants;
import com.address.model.CifAddress;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MyBatisClientAddressRepository implements ClientAddressRepository {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisClientAddressRepository.class);

    private static SqlSessionFactory getSqlSessionFactory() {
        return MyBatisConfig.getSqlSessionFactory();
    }

    private SqlSession getSqlSession() {
        return getSqlSessionFactory().openSession();
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
            session.getMapper(CifAddressMapper.class).updateAll(addresses);
            return null;
        });
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
