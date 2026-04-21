package com.address.repository;

import com.address.model.CifAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Primary
public class MapperClientAddressRepository implements ClientAddressRepository {

    private static final Logger logger = LoggerFactory.getLogger(MapperClientAddressRepository.class);

    private final CifAddressMapper mapper;

    public MapperClientAddressRepository(CifAddressMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CifAddress> findByClientNo(String clientNo) {
        return mapper.findByClientNo(clientNo, "N");
    }

    @Override
    @Transactional
    public void save(CifAddress address) {
        logger.info("保存地址 clientNo={}", address.getClientNo());
        mapper.save(address);
    }

    @Override
    @Transactional
    public void update(CifAddress address) {
        logger.info("更新地址 seqNo={}", address.getSeqNo());
        mapper.update(address);
    }

    @Override
    @Transactional
    public void saveAll(List<CifAddress> addresses) {
        logger.info("批量保存地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        mapper.saveAll(addresses);
    }

    @Override
    @Transactional
    public void updateAll(List<CifAddress> addresses) {
        logger.info("批量更新地址 clientNo={}, 数量={}", addresses.get(0).getClientNo(), addresses.size());
        mapper.updateAll(addresses);
    }

    @Override
    @Transactional
    public void delete(String seqNo) {
        logger.info("删除地址 seqNo={}", seqNo);
        mapper.delete(seqNo);
    }
}
