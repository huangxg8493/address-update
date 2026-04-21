package com.address.service;

import com.address.constants.Constants;
import com.address.model.CifAddress;
import com.address.repository.ClientAddressRepository;
import com.address.strategy.MailingAddressStrategy;
import com.address.strategy.NewestAddressStrategy;
import com.address.utils.SnowflakeIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class ClientAddressService {
    private static final Logger logger = LoggerFactory.getLogger(ClientAddressService.class);

    private final ClientAddressRepository repository;
    private final MailingAddressStrategy mailingStrategy;
    private final NewestAddressStrategy newestStrategy;
    private final AddressMerger merger;

    @Autowired
    public ClientAddressService(ClientAddressRepository repository,
                                MailingAddressStrategy mailingStrategy,
                                NewestAddressStrategy newestStrategy) {
        this.repository = repository;
        this.mailingStrategy = mailingStrategy;
        this.newestStrategy = newestStrategy;
        this.merger = new AddressMerger();
    }

    public List<CifAddress> updateAddresses(String clientNo, List<CifAddress> incoming) {
        // Step 1: 获取存量数组
        logger.info("Step 1: 获取存量数组 clientNo={}", clientNo);
        List<CifAddress> stock = repository.findByClientNo(clientNo);
        logger.info("Step 1 完成: 存量数量={}", stock.size());

        // Step 2: 合并上送地址（去重）
        logger.info("Step 2: 合并上送地址（去重） clientNo={}", clientNo);
        List<CifAddress> mergedIncoming = merger.mergeIncoming(incoming);
        logger.info("Step 2 完成: 去重后数量={}", mergedIncoming.size());

        // Step 3: 存量自身去重合并
        logger.info("Step 3: 存量自身去重合并 clientNo={}", clientNo);
        List<CifAddress> mergedStock = merger.mergeStock(stock);
        logger.info("Step 3 完成: 去重后数量={}", mergedStock.size());

        // Step 4: 遍历上送地址找匹配存量，确定是新增还是更新
        logger.info("Step 4: 遍历上送地址找匹配存量 clientNo={}", clientNo);
        for (CifAddress addr : mergedIncoming) {
            CifAddress matched = findMatchedStock(addr, mergedStock);
            if (matched != null) {
                addr.setSeqNo(matched.getSeqNo());
            } else {
                addr.setSeqNo(null);
            }
        }
        logger.info("Step 4 完成");

        // Step 5: 对两个数组应用通讯地址和最新地址规则，挑选结果不设置标识
        logger.info("Step 5: 应用地址选择策略 clientNo={}", clientNo);
        CifAddress mailing = mailingStrategy.select(mergedIncoming, mergedStock);
        Map<String, CifAddress> newestByType = newestStrategy.selectByType(mergedIncoming, mergedStock);
        logger.info("Step 5 完成: 通讯地址={}, 最新地址类型数={}",
                mailing != null ? mailing.getAddressType() : "无", newestByType.size());

        // Step 6: 重置两个数组所有标识为 N
        logger.info("Step 6: 重置所有地址标识为 N clientNo={}", clientNo);
        for (CifAddress addr : mergedStock) {
            addr.setIsMailingAddress(Constants.NO);
            addr.setIsNewest(Constants.NO);
        }
        for (CifAddress addr : mergedIncoming) {
            addr.setIsMailingAddress(Constants.NO);
            addr.setIsNewest(Constants.NO);
        }
        logger.info("Step 6 完成");

        // Step 7: 设置通讯地址标识为 Y
        logger.info("Step 7: 设置通讯地址标识 clientNo={}", clientNo);
        if (mailing != null) {
            mailing.setIsMailingAddress(Constants.YES);
            mailing.setIsNewest(Constants.YES);
            logger.info("Step 7 完成: 通讯地址 seqNo={}", mailing.getSeqNo());
        } else {
            logger.info("Step 7 完成: 无通讯地址");
        }

        // Step 8: 设置最新地址标识为 Y
        logger.info("Step 8: 设置最新地址标识 clientNo={}", clientNo);
        for (Map.Entry<String, CifAddress> entry : newestByType.entrySet()) {
            CifAddress newestAddr = entry.getValue();
            if (!Objects.equals(mailing, newestAddr)) {
                newestAddr.setIsNewest("Y");
            }
        }
        logger.info("Step 8 完成: 最新地址数量={}", newestByType.size());

        // Step 9: 对上送数组中seqNo为空的地址，生成id，然后批量insert
        logger.info("Step 9: 批量新增地址 clientNo={}", clientNo);
        List<CifAddress> toInsert = new ArrayList<>();
        for (CifAddress addr : mergedIncoming) {
            if (addr.getSeqNo() == null) {
                addr.setSeqNo(generateId());
                toInsert.add(addr);
            }
        }
        if (!toInsert.isEmpty()) {
            repository.saveAll(toInsert);
            logger.info("Step 9 完成: 新增数量={}", toInsert.size());
        } else {
            logger.info("Step 9 完成: 无新增地址");
        }

        // Step 10: 对上送数组中seqNo不为空的地址，根据其seqNo，更新存量地址中对应的数据，然后批量update
        logger.info("Step 10: 批量更新地址 clientNo={}", clientNo);
        for (CifAddress incomingAddr : mergedIncoming) {
            if (incomingAddr.getSeqNo() != null) {
                for (CifAddress stockAddr : mergedStock) {
                    if (Objects.equals(stockAddr.getSeqNo(), incomingAddr.getSeqNo())) {
                        stockAddr.setAddressType(incomingAddr.getAddressType());
                        stockAddr.setAddressDetail(incomingAddr.getAddressDetail());
                        stockAddr.setLastChangeDate(new Date());
                        stockAddr.setIsMailingAddress(incomingAddr.getIsMailingAddress());
                        stockAddr.setIsNewest(incomingAddr.getIsNewest());
                        break;
                    }
                }
            }
        }
        repository.updateAll(mergedStock);
        logger.info("Step 10 完成");

        logger.info("updateAddresses 完成 clientNo={}", clientNo);
        return repository.findByClientNo(clientNo);
    }

    private CifAddress findMatchedStock(CifAddress addr, List<CifAddress> stock) {
        for (CifAddress s : stock) {
            if (!Constants.YES.equals(s.getDelFlag()) &&
                Objects.equals(s.getAddressType(), addr.getAddressType()) &&
                Objects.equals(s.getAddressDetail(), addr.getAddressDetail())) {
                return s;
            }
        }
        return null;
    }

    private String generateId() {
        return SnowflakeIdGenerator.getInstance().nextIdAsString();
    }
}
