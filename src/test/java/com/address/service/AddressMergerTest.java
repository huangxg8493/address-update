package com.address.service;

import com.address.model.CifAddress;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AddressMergerTest {

    @Autowired
    private AddressMerger merger;

    @Test
    void testMergeIncomingRemovesDuplicates() {
        List<CifAddress> incoming = new ArrayList<>();

        CifAddress addr1 = new CifAddress();
        addr1.setClientNo("C001");
        addr1.setAddressType("02");
        addr1.setAddressDetail("联系地址");
        addr1.setSeqNo("A001");
        incoming.add(addr1);

        CifAddress addr2 = new CifAddress();
        addr2.setClientNo("C001");
        addr2.setAddressType("02");
        addr2.setAddressDetail("联系地址");
        addr2.setSeqNo("A002");
        addr2.setIsMailingAddress("Y");
        incoming.add(addr2);

        List<CifAddress> result = merger.mergeIncoming(incoming);
        assertEquals(1, result.size());
        assertEquals("Y", result.get(0).getIsMailingAddress());
    }

    @Test
    void testMergeIncomingKeepsDifferentAddresses() {
        List<CifAddress> incoming = new ArrayList<>();

        CifAddress addr1 = new CifAddress();
        addr1.setClientNo("C001");
        addr1.setAddressType("02");
        addr1.setAddressDetail("联系地址1");
        addr1.setSeqNo("A001");
        incoming.add(addr1);

        CifAddress addr2 = new CifAddress();
        addr2.setClientNo("C001");
        addr2.setAddressType("03");
        addr2.setAddressDetail("居住地址");
        addr2.setSeqNo("A002");
        incoming.add(addr2);

        List<CifAddress> result = merger.mergeIncoming(incoming);
        assertEquals(2, result.size());
    }
}
