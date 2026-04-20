package com.address.integration;

import com.address.model.CifAddress;
import com.address.repository.MemoryClientAddressRepository;
import com.address.strategy.impl.PriorityMailingAddressStrategy;
import com.address.strategy.impl.PriorityNewestAddressStrategy;
import com.address.service.ClientAddressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ClientAddressServiceIntegrationTest {

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
    void testFullWorkflow() {
        List<CifAddress> firstIncoming = new ArrayList<>();
        CifAddress addr1 = new CifAddress();
        addr1.setClientNo("C001");
        addr1.setAddressType("02");
        addr1.setAddressDetail("联系地址");
        CifAddress addr2 = new CifAddress();
        addr2.setClientNo("C001");
        addr2.setAddressType("03");
        addr2.setAddressDetail("居住地址");
        firstIncoming.add(addr1);
        firstIncoming.add(addr2);

        List<CifAddress> firstResult = service.updateAddresses("C001", firstIncoming);
        assertEquals(2, firstResult.size());

        long mailingCount = firstResult.stream()
                .filter(a -> "Y".equals(a.getIsMailingAddress()))
                .count();
        assertEquals(1, mailingCount);
    }
}
