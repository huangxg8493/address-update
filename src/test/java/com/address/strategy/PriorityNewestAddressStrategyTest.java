package com.address.strategy;

import com.address.model.CifAddress;
import com.address.strategy.impl.PriorityNewestAddressStrategy;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class PriorityNewestAddressStrategyTest {

    private PriorityNewestAddressStrategy strategy = new PriorityNewestAddressStrategy();

    @Test
    void testSelectByType() {
        List<CifAddress> addresses = new ArrayList<>();

        CifAddress addr1 = new CifAddress();
        addr1.setClientNo("C001");
        addr1.setAddressType("02");
        addr1.setAddressDetail("联系地址1");
        addr1.setSeqNo("A001");
        addr1.setLastChangeDate(new Date(1000));
        addresses.add(addr1);

        CifAddress addr2 = new CifAddress();
        addr2.setClientNo("C001");
        addr2.setAddressType("02");
        addr2.setAddressDetail("联系地址2");
        addr2.setSeqNo("A002");
        addr2.setLastChangeDate(new Date(2000));
        addresses.add(addr2);

        CifAddress addr3 = new CifAddress();
        addr3.setClientNo("C001");
        addr3.setAddressType("03");
        addr3.setAddressDetail("居住地址");
        addr3.setSeqNo("A003");
        addr3.setLastChangeDate(new Date(3000));
        addresses.add(addr3);

        Map<String, CifAddress> result = strategy.selectByType(addresses);
        assertEquals(2, result.size());
        assertEquals("A002", result.get("02").getSeqNo());
        assertEquals("A003", result.get("03").getSeqNo());
    }
}
