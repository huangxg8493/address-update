package com.address.repository;

import com.address.constants.Constants;
import com.address.model.CifAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemoryClientAddressRepository implements ClientAddressRepository {
    private final List<CifAddress> allAddresses = new ArrayList<>();

    @Override
    public List<CifAddress> findByClientNo(String clientNo) {
        return allAddresses.stream()
                .filter(a -> clientNo.equals(a.getClientNo()))
                .filter(a -> !Constants.YES.equals(a.getDelFlag()))
                .collect(Collectors.toList());
    }

    @Override
    public CifAddress findBySeqNo(String seqNo, String clientNo) {
        return allAddresses.stream()
                .filter(a -> seqNo.equals(a.getSeqNo()))
                .filter(a -> clientNo.equals(a.getClientNo()))
                .filter(a -> !Constants.YES.equals(a.getDelFlag()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void save(CifAddress address) {
        allAddresses.add(address);
    }

    @Override
    public void update(CifAddress address) {
        for (int i = 0; i < allAddresses.size(); i++) {
            if (address.getSeqNo().equals(allAddresses.get(i).getSeqNo())) {
                allAddresses.set(i, address);
                return;
            }
        }
    }

    @Override
    public void saveAll(List<CifAddress> addresses) {
        allAddresses.addAll(addresses);
    }

    @Override
    public void updateAll(List<CifAddress> addresses) {
        for (CifAddress address : addresses) {
            update(address);
        }
    }

    @Override
    public void delete(String seqNo) {
        for (int i = 0; i < allAddresses.size(); i++) {
            if (seqNo.equals(allAddresses.get(i).getSeqNo())) {
                allAddresses.get(i).setDelFlag(Constants.YES);
                return;
            }
        }
    }

    public void clear() {
        allAddresses.clear();
    }
}
