package com.address.repository;

import com.address.constants.Constants;
import com.address.dto.PageResult;
import com.address.model.CifAddress;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Primary
public class MyBatisAddressQueryRepository implements AddressQueryRepository {

    private static final Logger logger = LoggerFactory.getLogger(MyBatisAddressQueryRepository.class);

    private final SqlSessionFactory sqlSessionFactory;

    @Autowired
    public MyBatisAddressQueryRepository(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    @Override
    public PageResult<CifAddress> findPage(String clientNo, String addressType, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CifAddressMapper mapper = session.getMapper(CifAddressMapper.class);
            List<CifAddress> list = mapper.findPage(clientNo, addressType, Constants.NO, offset, pageSize);
            long total = mapper.countPage(clientNo, addressType, Constants.NO);
            return new PageResult<>(clientNo, pageNum, pageSize, total, list);
        }
    }
}
