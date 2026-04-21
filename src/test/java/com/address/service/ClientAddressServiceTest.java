package com.address.service;

import com.address.model.CifAddress;
import com.address.repository.ClientAddressRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ClientAddressServiceTest {

    @Autowired
    private ClientAddressService service;

    @Autowired
    private ClientAddressRepository repository;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        List<CifAddress> existing = repository.findByClientNo("C001");
        for (CifAddress addr : existing) {
            repository.delete(addr.getSeqNo());
        }
        // 双重清理确保
        existing = repository.findByClientNo("C001");
        for (CifAddress addr : existing) {
            repository.delete(addr.getSeqNo());
        }
    }

    @Test
    void testFirstTimeAddAddress() {
        List<CifAddress> incoming = new ArrayList<>();
        CifAddress addr = new CifAddress();
        addr.setClientNo("C001");
        addr.setAddressType("02");
        addr.setAddressDetail("联系地址");
        incoming.add(addr);

        List<CifAddress> result = service.updateAddresses("C001", incoming);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getSeqNo());
    }

    @Test
    void testUpdateExistingAddress() {
        // 每次用唯一的 seqNo 避免冲突
        String uniqueSeqNo = "EXISTING_" + UUID.randomUUID().toString().substring(0, 8);

        CifAddress existing = new CifAddress();
        existing.setClientNo("C001");
        existing.setAddressType("02");
        existing.setAddressDetail("联系地址");
        existing.setSeqNo(uniqueSeqNo);
        repository.save(existing);

        List<CifAddress> incoming = new ArrayList<>();
        CifAddress addr = new CifAddress();
        addr.setClientNo("C001");
        addr.setAddressType("02");
        addr.setAddressDetail("联系地址");
        incoming.add(addr);

        List<CifAddress> result = service.updateAddresses("C001", incoming);

        assertEquals(1, result.size());
    }
}