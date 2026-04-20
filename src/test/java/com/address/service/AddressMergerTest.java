package com.address.service;

import com.address.model.CifAddress;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AddressMergerTest {

    private AddressMerger merger = new AddressMerger();

    @Test
    void testMergeIncomingRemovesDuplicates() {
        List<CifAddress> incoming = new ArrayList<>();

        CifAddress addr1 = new CifAddress("C001", "02", "联系地址");
        addr1.setSeqNo("A001");
        incoming.add(addr1);

        CifAddress addr2 = new CifAddress("C001", "02", "联系地址");
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

        CifAddress addr1 = new CifAddress("C001", "02", "联系地址1");
        addr1.setSeqNo("A001");
        incoming.add(addr1);

        CifAddress addr2 = new CifAddress("C001", "03", "居住地址");
        addr2.setSeqNo("A002");
        incoming.add(addr2);

        List<CifAddress> result = merger.mergeIncoming(incoming);
        assertEquals(2, result.size());
    }
}
