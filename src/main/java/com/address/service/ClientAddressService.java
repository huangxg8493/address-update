package com.address.service;

import com.address.model.CifAddress;
import com.address.repository.ClientAddressRepository;
import com.address.strategy.MailingAddressStrategy;
import com.address.strategy.NewestAddressStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        // Step 2: 合并两个数组
        // 2.1 找出待删除地址（存量中有但上送中无的）
        List<CifAddress> toDelete = findDeletedAddresses(stock, incoming);
        // 2.2 合并存量地址（标记删除项 del_flag=Y）
        List<CifAddress> mergedStock = merger.mergeStock(stock, toDelete);
        // 2.3 合并上送地址（去重）
        List<CifAddress> mergedIncoming = merger.mergeIncoming(incoming);

        // Step 3: 应用更新/新增规则（设置seqNo）
        Map<String, String> seqNoMap = buildSeqNoMap(mergedStock, mergedIncoming);
        List<CifAddress> insertList = new ArrayList<>();
        List<CifAddress> updateList = new ArrayList<>();

        for (CifAddress addr : mergedIncoming) {
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
            String seqNo = seqNoMap.get(key);
            if (seqNo == null) {
                addr.setSeqNo(null);  // 新增
                insertList.add(addr);
            } else {
                addr.setSeqNo(seqNo);  // 更新
                updateList.add(addr);
            }
        }

        // Step 4: 从两个数组（del_flag≠Y）中应用规则，仅收集结果
        List<CifAddress> allActive = new ArrayList<>();
        for (CifAddress addr : mergedStock) {
            if (!"Y".equals(addr.getDelFlag())) {
                allActive.add(addr);
            }
        }
        allActive.addAll(mergedIncoming);

        CifAddress mailing = mailingStrategy.select(allActive);
        Map<String, CifAddress> newestByType = newestStrategy.selectByType(allActive);

        // Step 5: 将两个数组中的标识都设置为 N
        for (CifAddress addr : mergedStock) {
            addr.setIsMailingAddress("N");
            addr.setIsNewest("N");
        }
        for (CifAddress addr : mergedIncoming) {
            addr.setIsMailingAddress("N");
            addr.setIsNewest("N");
        }

        // Step 6: 将挑选出的通讯地址的标识设置为 Y
        if (mailing != null) {
            mailing.setIsMailingAddress("Y");
            mailing.setIsNewest("Y");
        }

        // Step 7: 将挑选出的最新地址的标识设置为 Y
        for (Map.Entry<String, CifAddress> entry : newestByType.entrySet()) {
            CifAddress newestAddr = entry.getValue();
            if (mailing == null || !Objects.equals(mailing.getSeqNo(), newestAddr.getSeqNo())) {
                newestAddr.setIsNewest("Y");
            }
        }

        // Step 8-9: 批量入库
        if (!insertList.isEmpty()) {
            for (CifAddress addr : insertList) {
                addr.setSeqNo(generateId());
            }
            repository.saveAll(insertList);
        }
        if (!updateList.isEmpty()) {
            repository.updateAll(updateList);
        }

        // 删除已标记的地址
        for (CifAddress addr : toDelete) {
            repository.delete(addr.getSeqNo());
        }

        return repository.findByClientNo(clientNo);
    }

    private Map<String, String> buildSeqNoMap(List<CifAddress> stock, List<CifAddress> incoming) {
        Map<String, String> map = new HashMap<>();
        for (CifAddress addr : stock) {
            if (!"Y".equals(addr.getDelFlag())) {
                String key = addr.getAddressType() + "_" + addr.getAddressDetail();
                if (!map.containsKey(key)) {
                    map.put(key, addr.getSeqNo());
                }
            }
        }
        return map;
    }

    private List<CifAddress> findDeletedAddresses(List<CifAddress> stock, List<CifAddress> incoming) {
        List<CifAddress> deleted = new ArrayList<>();
        for (CifAddress s : stock) {
            boolean found = false;
            for (CifAddress i : incoming) {
                if (s.getAddressType().equals(i.getAddressType()) &&
                    s.getAddressDetail().equals(i.getAddressDetail())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                deleted.add(s);
            }
        }
        return deleted;
    }

    private String generateId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
