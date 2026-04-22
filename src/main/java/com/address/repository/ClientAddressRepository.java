package com.address.repository;

import com.address.model.CifAddress;
import java.util.List;

public interface ClientAddressRepository {
    List<CifAddress> findByClientNo(String clientNo);
    CifAddress findBySeqNo(String seqNo);
    void save(CifAddress address);
    void update(CifAddress address);
    void saveAll(List<CifAddress> addresses);
    void updateAll(List<CifAddress> addresses);
    void delete(String seqNo);
}
