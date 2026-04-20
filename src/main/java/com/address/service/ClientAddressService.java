package com.address.service;

import com.address.model.CifAddress;
import com.address.repository.ClientAddressRepository;
import com.address.strategy.MailingAddressStrategy;
import com.address.strategy.NewestAddressStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        List<CifAddress> stock = repository.findByClientNo(clientNo);

        List<CifAddress> mergedIncoming = merger.mergeIncoming(incoming);

        Map<String, String> seqNoMap = buildSeqNoMap(stock, mergedIncoming);

        List<CifAddress> toInsert = new ArrayList<>();
        List<CifAddress> toUpdate = new ArrayList<>();

        for (CifAddress addr : mergedIncoming) {
            String seqNo = seqNoMap.get(addr.getAddressType() + "_" + addr.getAddressDetail());
            if (seqNo == null) {
                addr.setSeqNo(generateId());
                toInsert.add(addr);
            } else {
                addr.setSeqNo(seqNo);
                toUpdate.add(addr);
            }
        }

        for (CifAddress addr : mergedIncoming) {
            addr.setIsMailingAddress("N");
            addr.setIsNewest("N");
        }

        CifAddress mailing = mailingStrategy.select(mergedIncoming);
        if (mailing != null) {
            mailing.setIsMailingAddress("Y");
            mailing.setIsNewest("Y");
        }

        Map<String, CifAddress> newestByType = newestStrategy.selectByType(mergedIncoming);
        for (Map.Entry<String, CifAddress> entry : newestByType.entrySet()) {
            if (mailing == null || !entry.getValue().getSeqNo().equals(mailing.getSeqNo())) {
                entry.getValue().setIsNewest("Y");
            }
        }

        List<CifAddress> toDelete = findDeletedAddresses(stock, mergedIncoming);
        for (CifAddress addr : toDelete) {
            repository.delete(addr.getSeqNo());
        }

        if (!toInsert.isEmpty()) {
            repository.saveAll(toInsert);
        }

        if (!toUpdate.isEmpty()) {
            repository.updateAll(toUpdate);
        }

        return repository.findByClientNo(clientNo);
    }

    private Map<String, String> buildSeqNoMap(List<CifAddress> stock, List<CifAddress> incoming) {
        Map<String, String> map = new HashMap<>();
        for (CifAddress addr : stock) {
            String key = addr.getAddressType() + "_" + addr.getAddressDetail();
            if (!map.containsKey(key)) {
                map.put(key, addr.getSeqNo());
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
