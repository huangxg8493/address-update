package com.address.service;

import com.address.model.CifAddress;
import com.address.repository.ClientAddressRepository;
import com.address.strategy.MailingAddressStrategy;
import com.address.strategy.NewestAddressStrategy;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ClientAddressService {
    private final ClientAddressRepository repository;
    private final MailingAddressStrategy mailingStrategy;
    private final NewestAddressStrategy newestStrategy;
    private final AddressMerger merger;

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
        List<CifAddress> stock = repository.findByClientNo(clientNo);

        // Step 2: 合并上送地址（去重）
        List<CifAddress> mergedIncoming = merger.mergeIncoming(incoming);

        // Step 3: 存量自身去重合并
        List<CifAddress> mergedStock = merger.mergeStock(stock);

        // Step 4: 遍历上送地址找匹配存量，确定是新增还是更新
        for (CifAddress addr : mergedIncoming) {
            CifAddress matched = findMatchedStock(addr, mergedStock);
            if (matched != null) {
                addr.setSeqNo(matched.getSeqNo());
            } else {
                addr.setSeqNo(null);
            }
        }

        // Step 5: 对两个数组应用通讯地址和最新地址规则，挑选结果不设置标识
        CifAddress mailing = mailingStrategy.select(mergedIncoming);
        java.util.Map<String, CifAddress> newestByType = newestStrategy.selectByType(mergedIncoming);

        // Step 6: 重置两个数组所有标识为 N
        for (CifAddress addr : mergedStock) {
            addr.setIsMailingAddress("N");
            addr.setIsNewest("N");
        }
        for (CifAddress addr : mergedIncoming) {
            addr.setIsMailingAddress("N");
            addr.setIsNewest("N");
        }

        // Step 7: 设置通讯地址标识为 Y
        if (mailing != null) {
            mailing.setIsMailingAddress("Y");
            mailing.setIsNewest("Y");
        }

        // Step 8: 设置最新地址标识为 Y
        for (java.util.Map.Entry<String, CifAddress> entry : newestByType.entrySet()) {
            CifAddress newestAddr = entry.getValue();
            if (!Objects.equals(mailing, newestAddr)) {
                newestAddr.setIsNewest("Y");
            }
        }

        // Step 9: 对上送数组中seqNo为空的地址，生成id，然后批量insert
        List<CifAddress> toInsert = new ArrayList<>();
        for (CifAddress addr : mergedIncoming) {
            if (addr.getSeqNo() == null) {
                addr.setSeqNo(generateId());
                toInsert.add(addr);
            }
        }
        if (!toInsert.isEmpty()) {
            repository.saveAll(toInsert);
        }

        // Step 10: 对上送数组中seqNo不为空的地址，根据其seqNo，更新存量地址中对应的数据，然后批量update
        List<CifAddress> toUpdate = new ArrayList<>();
        for (CifAddress incomingAddr : mergedIncoming) {
            if (incomingAddr.getSeqNo() != null) {
                for (CifAddress stockAddr : mergedStock) {
                    if (Objects.equals(stockAddr.getSeqNo(), incomingAddr.getSeqNo())) {
                        stockAddr.setAddressType(incomingAddr.getAddressType());
                        stockAddr.setAddressDetail(incomingAddr.getAddressDetail());
                        stockAddr.setLastChangeDate(new Date());
                        stockAddr.setIsMailingAddress(incomingAddr.getIsMailingAddress());
                        stockAddr.setIsNewest(incomingAddr.getIsNewest());
                        toUpdate.add(stockAddr);
                        break;
                    }
                }
            }
        }
        if (!toUpdate.isEmpty()) {
            repository.updateAll(toUpdate);
        }

        return repository.findByClientNo(clientNo);
    }

    private CifAddress findMatchedStock(CifAddress addr, List<CifAddress> stock) {
        for (CifAddress s : stock) {
            if (!"Y".equals(s.getDelFlag()) &&
                Objects.equals(s.getAddressType(), addr.getAddressType()) &&
                Objects.equals(s.getAddressDetail(), addr.getAddressDetail())) {
                return s;
            }
        }
        return null;
    }

    private CifAddress findBySeqNo(String seqNo, List<CifAddress> addresses) {
        for (CifAddress addr : addresses) {
            if (Objects.equals(addr.getSeqNo(), seqNo)) {
                return addr;
            }
        }
        return null;
    }

    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
