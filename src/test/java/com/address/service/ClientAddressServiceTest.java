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

    @BeforeEach
    void setUp() {
        repository = new MemoryClientAddressRepository();
        service = new ClientAddressService(
                repository,
                new PriorityMailingAddressStrategy(),
                new PriorityNewestAddressStrategy()
        );
    }

    @Test
    void testFirstTimeAddAddress() {
        List<CifAddress> incoming = new ArrayList<>();
        CifAddress addr = new CifAddress("C001", "02", "联系地址");
        incoming.add(addr);

        List<CifAddress> result = service.updateAddresses("C001", incoming);

        assertEquals(1, result.size());
        assertNotNull(result.get(0).getSeqNo());
    }

    @Test
    void testUpdateExistingAddress() {
        CifAddress existing = new CifAddress("C001", "02", "联系地址");
        repository.save(existing);

        List<CifAddress> incoming = new ArrayList<>();
        CifAddress addr = new CifAddress("C001", "02", "联系地址");
        incoming.add(addr);

        List<CifAddress> result = service.updateAddresses("C001", incoming);

        assertEquals(1, result.size());
    }
}
