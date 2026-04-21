package com.address.service;

import com.address.model.CifAddress;
import com.address.repository.MemoryClientAddressRepository;
import com.address.strategy.impl.PriorityMailingAddressStrategy;
import com.address.strategy.impl.PriorityNewestAddressStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClientAddressServiceTest {

    private ClientAddressService service;
    private MemoryClientAddressRepository repository;
    private PriorityMailingAddressStrategy mailingStrategy;
    private PriorityNewestAddressStrategy newestStrategy;

    @BeforeEach
    void setUp() {
        repository = new MemoryClientAddressRepository();
        mailingStrategy = new PriorityMailingAddressStrategy();
        newestStrategy = new PriorityNewestAddressStrategy();
        service = new ClientAddressService(repository, mailingStrategy, newestStrategy);
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
        CifAddress existing = new CifAddress();
        existing.setClientNo("C001");
        existing.setAddressType("02");
        existing.setAddressDetail("联系地址");
        existing.setSeqNo("EXISTING001");
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
